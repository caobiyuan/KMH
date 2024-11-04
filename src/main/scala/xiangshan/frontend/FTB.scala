/***************************************************************************************
* Copyright (c) 2020-2021 Institute of Computing Technology, Chinese Academy of Sciences
* Copyright (c) 2020-2021 Peng Cheng Laboratory
*
* XiangShan is licensed under Mulan PSL v2.
* You can use this software according to the terms and conditions of the Mulan PSL v2.
* You may obtain a copy of Mulan PSL v2 at:
*          http://license.coscl.org.cn/MulanPSL2
*
* THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
* EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
* MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
*
* See the Mulan PSL v2 for more details.
***************************************************************************************/

package xiangshan.frontend

import org.chipsalliance.cde.config.Parameters
import chisel3._
import chisel3.util._
import xiangshan._
import utils._
import utility._

import scala.math.min
import scala.{Tuple2 => &}
import os.copy


trait FTBParams extends HasXSParameter with HasBPUConst  with HasBPUParameter{

  val numEntries = FtbSize
  val numWays    = FtbWays
  val numSets    = 512 // 512
  val tagSize    = 10
  val ctrBits    = 2
  val ITTageNTables = ITTageTableInfos.size
  val TickWidth = 8

  val indirEntriesWays = 16
  def ctr_null(ctr: UInt, ctrBit: Int = ctrBits) = {
    ctr === 0.U
  }
  def ctr_unconf(ctr: UInt, ctrBit: Int = ctrBits) = {
    ctr < (1 << (ctrBit-1)).U
  }

  val TAR_STAT_SZ = 2
  def TAR_FIT = 0.U(TAR_STAT_SZ.W)
  def TAR_OVF = 1.U(TAR_STAT_SZ.W)
  def TAR_UDF = 2.U(TAR_STAT_SZ.W)

  def BR_OFFSET_LEN = 12
  def JMP_OFFSET_LEN = 24

  def FTBCLOSE_THRESHOLD_SZ = log2Ceil(500)
  def FTBCLOSE_THRESHOLD = 500.U(FTBCLOSE_THRESHOLD_SZ.W) //can be modified
}

class FtbSlot_FtqMem(implicit p: Parameters) extends XSBundle with FTBParams {
  val offset  = UInt(log2Ceil(PredictWidth).W)
  val sharing = Bool()
  val valid   = Bool()
}

class FtbSlot(val offsetLen: Int, val subOffsetLen: Option[Int] = None)(implicit p: Parameters) extends FtbSlot_FtqMem with FTBParams {
  if (subOffsetLen.isDefined) {
    require(subOffsetLen.get <= offsetLen)
  }
  val lower   = UInt(offsetLen.W)
  val tarStat = UInt(TAR_STAT_SZ.W)

  def setLowerStatByTarget(pc: UInt, target: UInt, isShare: Boolean) = {
    def getTargetStatByHigher(pc_higher: UInt, target_higher: UInt) =
      Mux(target_higher > pc_higher, TAR_OVF,
        Mux(target_higher < pc_higher, TAR_UDF, TAR_FIT))
    def getLowerByTarget(target: UInt, offsetLen: Int) = target(offsetLen, 1)
    val offLen = if (isShare) this.subOffsetLen.get else this.offsetLen
    val pc_higher = pc(VAddrBits-1, offLen+1)
    val target_higher = target(VAddrBits-1, offLen+1)
    val stat = getTargetStatByHigher(pc_higher, target_higher)
    val lower = ZeroExt(getLowerByTarget(target, offLen), this.offsetLen)
    this.lower := lower
    this.tarStat := stat
    this.sharing := isShare.B
  }

  def getTarget(pc: UInt, last_stage: Option[Tuple2[UInt, Bool]] = None) = {
    def getTarget(offLen: Int)(pc: UInt, lower: UInt, stat: UInt,
      last_stage: Option[Tuple2[UInt, Bool]] = None) = {
      val h                = pc(VAddrBits - 1, offLen + 1)
      val higher           = Wire(UInt((VAddrBits - offLen - 1).W))
      val higher_plus_one  = Wire(UInt((VAddrBits - offLen - 1).W))
      val higher_minus_one = Wire(UInt((VAddrBits-offLen-1).W))

      // Switch between previous stage pc and current stage pc
      // Give flexibility for timing
      if (last_stage.isDefined) {
        val last_stage_pc = last_stage.get._1
        val last_stage_pc_h = last_stage_pc(VAddrBits-1, offLen+1)
        val stage_en = last_stage.get._2
        higher := RegEnable(last_stage_pc_h, stage_en)
        higher_plus_one := RegEnable(last_stage_pc_h+1.U, stage_en)
        higher_minus_one := RegEnable(last_stage_pc_h-1.U, stage_en)
      } else {
        higher := h
        higher_plus_one := h + 1.U
        higher_minus_one := h - 1.U
      }
      val target =
        Cat(
          Mux1H(Seq(
            (stat === TAR_OVF, higher_plus_one),
            (stat === TAR_UDF, higher_minus_one),
            (stat === TAR_FIT, higher),
          )),
          lower(offLen-1, 0), 0.U(1.W)
        )
      require(target.getWidth == VAddrBits)
      require(offLen != 0)
      target
    }
    if (subOffsetLen.isDefined)
      Mux(sharing,
        getTarget(subOffsetLen.get)(pc, lower, tarStat, last_stage),
        getTarget(offsetLen)(pc, lower, tarStat, last_stage)
      )
    else
      getTarget(offsetLen)(pc, lower, tarStat, last_stage)
  }
  def fromAnotherSlot(that: FtbSlot) = {
    require(
      this.offsetLen > that.offsetLen && this.subOffsetLen.map(_ == that.offsetLen).getOrElse(true) ||
      this.offsetLen == that.offsetLen
    )
    this.offset := that.offset
    this.tarStat := that.tarStat
    this.sharing := (this.offsetLen > that.offsetLen && that.offsetLen == this.subOffsetLen.get).B
    this.valid := that.valid
    this.lower := ZeroExt(that.lower, this.offsetLen)
  }

  def slotConsistent(that: FtbSlot) = {
    VecInit(
      this.offset  === that.offset,
      this.lower   === that.lower,
      this.tarStat === that.tarStat,
      this.sharing === that.sharing,
      this.valid   === that.valid
    ).reduce(_&&_)
  }

}


class FTBEntry_part(implicit p: Parameters) extends XSBundle with FTBParams with BPUUtils {
  val isCall      = Bool()
  val isRet       = Bool()
  val isJalr      = Bool()

  def isJal = !isJalr
}

class FTBEntry_FtqMem(implicit p: Parameters) extends FTBEntry_part with FTBParams with BPUUtils {

  val brSlots = Vec(numBrSlot, new FtbSlot_FtqMem)
  val tailSlot = new FtbSlot_FtqMem

  def jmpValid = {
    tailSlot.valid && !tailSlot.sharing
  }

  def getBrRecordedVec(offset: UInt) = {
    VecInit(
      brSlots.map(s => s.valid && s.offset === offset) :+
      (tailSlot.valid && tailSlot.offset === offset && tailSlot.sharing)
    )
  }

  def brIsSaved(offset: UInt) = getBrRecordedVec(offset).reduce(_||_)

  def getBrMaskByOffset(offset: UInt) =
    brSlots.map{ s => s.valid && s.offset <= offset } :+
    (tailSlot.valid && tailSlot.offset <= offset && tailSlot.sharing)
  
  def newBrCanNotInsert(offset: UInt) = {
    val lastSlotForBr = tailSlot
    lastSlotForBr.valid && lastSlotForBr.offset < offset
  }

}

class FTBEntry(implicit p: Parameters) extends FTBEntry_part with FTBParams with BPUUtils {


  val valid       = Bool()

  val brSlots = Vec(numBrSlot, new FtbSlot(BR_OFFSET_LEN))

  val tailSlot = new FtbSlot(JMP_OFFSET_LEN, Some(BR_OFFSET_LEN))

  val ctr = UInt(ctrBits.W)
  val u   = Bool()

  // Partial Fall-Through Address
  val pftAddr     = UInt(log2Up(PredictWidth).W)
  val carry       = Bool()

  val last_may_be_rvi_call = Bool()

  val always_taken = Vec(numBr, Bool())

  def getSlotForBr(idx: Int): FtbSlot = {
    require(idx <= numBr-1)
    (idx, numBr) match {
      case (i, n) if i == n-1 => this.tailSlot
      case _ => this.brSlots(idx)
    }
  }
  def allSlotsForBr = {
    (0 until numBr).map(getSlotForBr(_))
  }
  def setByBrTarget(brIdx: Int, pc: UInt, target: UInt) = {
    val slot = getSlotForBr(brIdx)
    slot.setLowerStatByTarget(pc, target, brIdx == numBr-1)
  }
  def setByJmpTarget(pc: UInt, target: UInt) = {
    this.tailSlot.setLowerStatByTarget(pc, target, false)
  }

  def getTargetVec(pc: UInt, last_stage: Option[Tuple2[UInt, Bool]] = None) = {
    /*
    Previous design: Use the getTarget function of FTBSlot to calculate three sets of targets separately;
    During this process, nine sets of registers will be generated to register the values of the higher plus one minus one
    Current design: Reuse the duplicate parts of the original nine sets of registers,
    calculate the common high bits last_stage_pc_higher of brtarget and jmptarget,
    and the high bits last_stage_pc_middle that need to be added and subtracted from each other,
    and then concatenate them according to the carry situation to obtain brtarget and jmptarget
    */
    val h_br                = pc(VAddrBits - 1,  BR_OFFSET_LEN + 1)
    val higher_br           = Wire(UInt((VAddrBits - BR_OFFSET_LEN - 1).W))
    val higher_plus_one_br  = Wire(UInt((VAddrBits - BR_OFFSET_LEN - 1).W))
    val higher_minus_one_br = Wire(UInt((VAddrBits - BR_OFFSET_LEN - 1).W))
    val h_tail                = pc(VAddrBits - 1,  JMP_OFFSET_LEN + 1)
    val higher_tail           = Wire(UInt((VAddrBits - JMP_OFFSET_LEN - 1).W))
    val higher_plus_one_tail  = Wire(UInt((VAddrBits - JMP_OFFSET_LEN - 1).W))
    val higher_minus_one_tail = Wire(UInt((VAddrBits - JMP_OFFSET_LEN - 1).W))
    if (last_stage.isDefined) {
      val last_stage_pc = last_stage.get._1
      val stage_en = last_stage.get._2
      val last_stage_pc_higher = RegEnable(last_stage_pc(VAddrBits - 1, JMP_OFFSET_LEN + 1), stage_en)
      val last_stage_pc_middle = RegEnable(last_stage_pc(JMP_OFFSET_LEN, BR_OFFSET_LEN + 1), stage_en)
      val last_stage_pc_higher_plus_one  = RegEnable(last_stage_pc(VAddrBits - 1, JMP_OFFSET_LEN + 1) + 1.U, stage_en)
      val last_stage_pc_higher_minus_one = RegEnable(last_stage_pc(VAddrBits - 1, JMP_OFFSET_LEN + 1) - 1.U, stage_en)
      val last_stage_pc_middle_plus_one  = RegEnable(Cat(0.U(1.W), last_stage_pc(JMP_OFFSET_LEN, BR_OFFSET_LEN + 1)) + 1.U, stage_en)
      val last_stage_pc_middle_minus_one = RegEnable(Cat(0.U(1.W), last_stage_pc(JMP_OFFSET_LEN, BR_OFFSET_LEN + 1)) - 1.U, stage_en)

      higher_br := Cat(last_stage_pc_higher, last_stage_pc_middle)
      higher_plus_one_br := Mux(
          last_stage_pc_middle_plus_one(JMP_OFFSET_LEN - BR_OFFSET_LEN),
          Cat(last_stage_pc_higher_plus_one, last_stage_pc_middle_plus_one(JMP_OFFSET_LEN - BR_OFFSET_LEN-1, 0)),
          Cat(last_stage_pc_higher, last_stage_pc_middle_plus_one(JMP_OFFSET_LEN - BR_OFFSET_LEN-1, 0)))
      higher_minus_one_br := Mux(
          last_stage_pc_middle_minus_one(JMP_OFFSET_LEN - BR_OFFSET_LEN),
          Cat(last_stage_pc_higher_minus_one, last_stage_pc_middle_minus_one(JMP_OFFSET_LEN - BR_OFFSET_LEN-1, 0)),
          Cat(last_stage_pc_higher, last_stage_pc_middle_minus_one(JMP_OFFSET_LEN - BR_OFFSET_LEN-1, 0)))

      higher_tail := last_stage_pc_higher
      higher_plus_one_tail := last_stage_pc_higher_plus_one
      higher_minus_one_tail := last_stage_pc_higher_minus_one
    }else{
      higher_br := h_br
      higher_plus_one_br := h_br + 1.U
      higher_minus_one_br := h_br - 1.U
      higher_tail := h_tail
      higher_plus_one_tail := h_tail + 1.U
      higher_minus_one_tail := h_tail - 1.U
    }
    val br_slots_targets = VecInit(brSlots.map(s =>
      Cat(
          Mux1H(Seq(
            (s.tarStat === TAR_OVF, higher_plus_one_br),
            (s.tarStat === TAR_UDF, higher_minus_one_br),
            (s.tarStat === TAR_FIT, higher_br),
          )),
          s.lower(s.offsetLen-1, 0), 0.U(1.W)
        )
    ))
    val tail_target = Wire(UInt(VAddrBits.W))
    if(tailSlot.subOffsetLen.isDefined){
      tail_target := Mux(tailSlot.sharing,
        Cat(
          Mux1H(Seq(
            (tailSlot.tarStat === TAR_OVF, higher_plus_one_br),
            (tailSlot.tarStat === TAR_UDF, higher_minus_one_br),
            (tailSlot.tarStat === TAR_FIT, higher_br),
          )),
          tailSlot.lower(tailSlot.subOffsetLen.get-1, 0), 0.U(1.W)
        ),
        Cat(
          Mux1H(Seq(
            (tailSlot.tarStat === TAR_OVF, higher_plus_one_tail),
            (tailSlot.tarStat === TAR_UDF, higher_minus_one_tail),
            (tailSlot.tarStat === TAR_FIT, higher_tail),
          )),
          tailSlot.lower(tailSlot.offsetLen-1, 0), 0.U(1.W)
        )
      )
    }else{
      tail_target := Cat(
          Mux1H(Seq(
            (tailSlot.tarStat === TAR_OVF, higher_plus_one_tail),
            (tailSlot.tarStat === TAR_UDF, higher_minus_one_tail),
            (tailSlot.tarStat === TAR_FIT, higher_tail),
          )),
          tailSlot.lower(tailSlot.offsetLen-1, 0), 0.U(1.W)
        )
    }

    br_slots_targets.map(t => require(t.getWidth == VAddrBits))
    require(tail_target.getWidth == VAddrBits)
    val targets = VecInit(br_slots_targets :+ tail_target)
    targets
  }

  def getOffsetVec = VecInit(brSlots.map(_.offset) :+ tailSlot.offset)
  def getFallThrough(pc: UInt, last_stage_entry: Option[Tuple2[FTBEntry, Bool]] = None) = {
    if (last_stage_entry.isDefined) {
      var stashed_carry = RegEnable(last_stage_entry.get._1.carry, last_stage_entry.get._2)
      getFallThroughAddr(pc, stashed_carry, pftAddr)
    } else {
      getFallThroughAddr(pc, carry, pftAddr)
    }
  }

  def hasBr(offset: UInt) =
    brSlots.map{ s => s.valid && s.offset <= offset}.reduce(_||_) ||
    (tailSlot.valid && tailSlot.offset <= offset && tailSlot.sharing)

  def getBrMaskByOffset(offset: UInt) =
    brSlots.map{ s => s.valid && s.offset <= offset } :+
    (tailSlot.valid && tailSlot.offset <= offset && tailSlot.sharing)

  def getBrRecordedVec(offset: UInt) = {
    VecInit(
      brSlots.map(s => s.valid && s.offset === offset) :+
      (tailSlot.valid && tailSlot.offset === offset && tailSlot.sharing)
    )
  }

  def brIsSaved(offset: UInt) = getBrRecordedVec(offset).reduce(_||_)

  def brValids = {
    VecInit(
      brSlots.map(_.valid) :+ (tailSlot.valid && tailSlot.sharing)
    )
  }

  def noEmptySlotForNewBr = {
    VecInit(brSlots.map(_.valid) :+ tailSlot.valid).reduce(_&&_)
  }

  def newBrCanNotInsert(offset: UInt) = {
    val lastSlotForBr = tailSlot
    lastSlotForBr.valid && lastSlotForBr.offset < offset
  }

  def jmpValid = {
    tailSlot.valid && !tailSlot.sharing
  }

  def brOffset = {
    VecInit(brSlots.map(_.offset) :+ tailSlot.offset)
  }

  def entryConsistent(that: FTBEntry) = {
    val validDiff     = this.valid === that.valid
    val brSlotsDiffSeq  : IndexedSeq[Bool] =
      this.brSlots.zip(that.brSlots).map{
        case(x, y) => x.slotConsistent(y)
      }
    val tailSlotDiff  = this.tailSlot.slotConsistent(that.tailSlot)
    val pftAddrDiff   = this.pftAddr === that.pftAddr
    val carryDiff     = this.carry   === that.carry
    val isCallDiff    = this.isCall  === that.isCall
    val isRetDiff     = this.isRet   === that.isRet
    val isJalrDiff    = this.isJalr  === that.isJalr
    val lastMayBeRviCallDiff = this.last_may_be_rvi_call === that.last_may_be_rvi_call
    val alwaysTakenDiff : IndexedSeq[Bool] =
      this.always_taken.zip(that.always_taken).map{
        case(x, y) => x === y
      }
    VecInit(
      validDiff,
      brSlotsDiffSeq.reduce(_&&_),
      tailSlotDiff,
      pftAddrDiff,
      carryDiff,
      isCallDiff,
      isRetDiff,
      isJalrDiff,
      lastMayBeRviCallDiff,
      alwaysTakenDiff.reduce(_&&_)
    ).reduce(_&&_)
  }

  def display(cond: Bool): Unit = {
    XSDebug(cond, p"-----------FTB entry----------- \n")
    XSDebug(cond, p"v=${valid}\n")
    for(i <- 0 until numBr) {
      XSDebug(cond, p"[br$i]: v=${allSlotsForBr(i).valid}, offset=${allSlotsForBr(i).offset}," +
        p"lower=${Hexadecimal(allSlotsForBr(i).lower)}\n")
    }
    XSDebug(cond, p"[tailSlot]: v=${tailSlot.valid}, offset=${tailSlot.offset}," +
      p"lower=${Hexadecimal(tailSlot.lower)}, sharing=${tailSlot.sharing}}\n")
    XSDebug(cond, p"pftAddr=${Hexadecimal(pftAddr)}, carry=$carry\n")
    XSDebug(cond, p"isCall=$isCall, isRet=$isRet, isjalr=$isJalr\n")
    XSDebug(cond, p"last_may_be_rvi_call=$last_may_be_rvi_call\n")
    XSDebug(cond, p"------------------------------- \n")
  }

}

class FTBEntryWithTag(implicit p: Parameters) extends XSBundle with FTBParams with BPUUtils {
  val entry = new FTBEntry
  val tag = UInt(tagSize.W)
  def display(cond: Bool): Unit = {
    entry.display(cond)
    XSDebug(cond, p"tag is ${Hexadecimal(tag)}\n------------------------------- \n")
  }
}

class FTBMeta(implicit p: Parameters) extends XSBundle with FTBParams {
  val writeWay = UInt(log2Ceil(ITTageNTables).W)
  val hit = Bool()
  val pred_cycle = if (!env.FPGAPlatform) Some(UInt(64.W)) else None
  val indir = Bool()
  val provider = ValidUndirectioned(UInt(log2Ceil(ITTageNTables).W))
  val altProvider = ValidUndirectioned(UInt(log2Ceil(ITTageNTables).W))
  val altDiffers = Bool()
  val providerU = Bool()
  val providerCtr = UInt(ctrBits.W)
  val altProviderCtr = UInt(ctrBits.W)
  val allocate = ValidUndirectioned(UInt(log2Ceil(ITTageNTables).W))
  val providerTarget = UInt(VAddrBits.W)
  val altProviderTarget = UInt(VAddrBits.W)
}

object FTBMeta {
  def apply(writeWay: UInt, hit: Bool, pred_cycle: UInt,indir:Bool)(implicit p: Parameters): FTBMeta = {
    val e = Wire(new FTBMeta)
    e.writeWay := writeWay
    e.hit := hit
    e.pred_cycle.map(_ := pred_cycle)
    e.indir := indir
    e
  }
}

// indir
class FTBReq(implicit p: Parameters) extends XSBundle with FTBParams with BPUUtils{
  val pc = UInt(VAddrBits.W)
  val folded_hist = new AllFoldedHistories(foldedGHistInfos)
  val indir_valid = Bool()
} 

class FTBUpdate(implicit p: Parameters) extends XSBundle with FTBParams with BPUUtils{
  val pc = UInt(VAddrBits.W)
  val ghist = UInt(HistoryLength.W)
  val indir_valid = Bool()
} 

// class UpdateQueueEntry(implicit p: Parameters) extends XSBundle with FTBParams {
//   val pc = UInt(VAddrBits.W)
//   val ftb_entry = new FTBEntry
//   val hit = Bool()
//   val hit_way = UInt(log2Ceil(numWays).W)
// }
//
// object UpdateQueueEntry {
//   def apply(pc: UInt, fe: FTBEntry, hit: Bool, hit_way: UInt)(implicit p: Parameters): UpdateQueueEntry = {
//     val e = Wire(new UpdateQueueEntry)
//     e.pc := pc
//     e.ftb_entry := fe
//     e.hit := hit
//     e.hit_way := hit_way
//     e
//   }
// }

class FTB(implicit p: Parameters) extends BasePredictor with FTBParams with BPUUtils
  with HasCircularQueuePtrHelper with HasPerfEvents {
  override val meta_size = WireInit(0.U.asTypeOf(new FTBMeta)).getWidth

  val ftbAddr = new TableAddr(log2Up(numSets), 1)
  def inc_ctr(ctr: UInt, taken: Bool): UInt = satUpdate(ctr, ctrBits, taken)

  class FTBBank(val numSets: Int, val nWays: Int,val nRows:Int,val histLen:Int,val tagLen:Int,val tableIdx: Int) extends XSModule with BPUUtils with HasFoldedHistory {
    val io = IO(new Bundle {
      val s1_fire = Input(Bool())

      // when ftb hit, read_hits.valid is true, and read_hits.bits is OH of hit way
      // when ftb not hit, read_hits.valid is false, and read_hits is OH of allocWay
      // val read_hits = Valid(Vec(numWays, Bool()))
      val req = Flipped(DecoupledIO(new FTBReq))
      val read_resp = Output(new FTBEntry)
      val read_idx = Output(UInt(log2Ceil(numSets).W))
      val read_hit = Bool()

      // val read_multi_entry = Output(new FTBEntry)
      // val read_multi_hits = Valid(UInt(log2Ceil(numWays).W))

      val u_req = Flipped(DecoupledIO(new FTBUpdate))
      val update_hit = Bool()
      val update_access = Input(Bool())

      // val update_req = Input(new FTBUpdate)
      val update_idx = Input(UInt(log2Ceil(numSets).W))
      val update_write_data = Flipped(Valid(new FTBEntryWithTag))
      val ftb_r_entry = Output(new FTBEntry)
      // val u_reset_u = Input(Bool())
      // val update_write_way = Input(UInt(log2Ceil(numWays).W))
      // val update_write_alloc = Input(Bool())
    })

    // Extract holdRead logic to fix bug that update read override predict read result
    val ftb = Module(new SRAMTemplate(new FTBEntryWithTag, set = numSets, way = numWays, shouldReset = true, holdRead = false, singlePort = true))
    val ftb_r_entries = ftb.io.r.resp.data.map(_.entry)

    val pred_rdata   = HoldUnless(ftb.io.r.resp.data, RegNext(io.req.valid && !io.update_access))
    

    def getUnhashedIdx(pc: UInt): UInt = pc >> instOffsetBits
    // indir
    require(histLen == 0 && tagLen == 0 || histLen != 0 && tagLen != 0)
    val idxFhInfo = (histLen, min(log2Ceil(nRows), histLen))
    val tagFhInfo = (histLen, min(histLen, tagLen))
    val altTagFhInfo = (histLen, min(histLen, tagLen-1))
    val allFhInfos = Seq(idxFhInfo, tagFhInfo, altTagFhInfo)

    def getFoldedHistoryInfo = allFhInfos.filter(_._1 >0).toSet

    def compute_tag_and_hash(unhashed_idx: UInt, allFh: AllFoldedHistories) = {
      if (histLen > 0) {
        val idx_fh = allFh.getHistWithInfo(idxFhInfo).folded_hist
        val tag_fh = allFh.getHistWithInfo(tagFhInfo).folded_hist
        val alt_tag_fh = allFh.getHistWithInfo(altTagFhInfo).folded_hist
        // require(idx_fh.getWidth == log2Ceil(nRows))
        val idx = (unhashed_idx ^ idx_fh)(log2Ceil(nRows)-1, 0)
        val tag = ((unhashed_idx >> log2Ceil(nRows)) ^ tag_fh ^ (alt_tag_fh << 1)) (tagLen - 1, 0)
        (idx, tag)
      }
      else {
        require(tagLen == 0)
        (unhashed_idx(log2Ceil(nRows)-1, 0), 0.U)
      }
    }



    val s0_pc = io.req.bits.pc
    val s0_indir_valid = io.req.bits.indir_valid
    val s0_unhashed_idx = getUnhashedIdx(io.req.bits.pc)
    val u_unhashed_idx = getUnhashedIdx(io.u_req.bits.pc)
    val (s0_indir_idx,s0_indir_tag) = compute_tag_and_hash(s0_unhashed_idx, io.req.bits.folded_hist)


    //update
    val update_folded_hist = WireInit(0.U.asTypeOf(new AllFoldedHistories(foldedGHistInfos)))

    update_folded_hist.getHistWithInfo(idxFhInfo).folded_hist := compute_folded_ghist(io.u_req.bits.ghist, log2Ceil(nRows))
    update_folded_hist.getHistWithInfo(tagFhInfo).folded_hist := compute_folded_ghist(io.u_req.bits.ghist, tagLen)
    update_folded_hist.getHistWithInfo(altTagFhInfo).folded_hist := compute_folded_ghist(io.u_req.bits.ghist, tagLen-1)
    dontTouch(update_folded_hist)
    val (u_indir_idx, u_indir_tag) = compute_tag_and_hash(u_unhashed_idx, update_folded_hist)


    val s0_idx = ftbAddr.getIdx(io.req.bits.pc)
    val s0_tag = ftbAddr.getTag(io.req.bits.pc)(tagSize-1, 0)
    // update to do
    val u_idx = ftbAddr.getIdx(io.u_req.bits.pc)
    val u_tag = ftbAddr.getTag(io.u_req.bits.pc)(tagSize-1, 0)

    val real_idx = Mux(s0_indir_valid,s0_indir_idx,s0_idx)
    val real_tag = Mux(s0_indir_valid,s0_indir_tag,s0_tag)

    val u_real_idx = Mux(io.u_req.bits.indir_valid,u_indir_idx,u_idx)
    val u_real_tag = Mux(io.u_req.bits.indir_valid,u_indir_tag,u_tag)
    
    ftb.io.r.req.valid := io.req.valid || io.u_req.valid // io.s0_fire
    ftb.io.r.req.bits.setIdx := Mux(io.u_req.valid, u_real_idx, real_idx) // s0_idx

    assert(!(io.req.valid && io.u_req.valid))



    io.req.ready := ftb.io.r.req.ready
    io.u_req.ready := ftb.io.r.req.ready

    val req_tag = RegEnable(real_tag, io.req.valid)
    // val req_idx = RegEnable(real_idx, io.req.valid)



    val u_req_tag = RegEnable(u_real_tag, io.u_req.valid)

    val read_entries = pred_rdata.map(_.entry)
    val read_tags    = pred_rdata.map(_.tag)

    val total_hits = VecInit((0 until numWays).map(b => read_tags(b) === real_tag && read_entries(b).valid && io.s1_fire))
    val hit = total_hits.reduce(_||_)
    // val hit_way_1h = VecInit(PriorityEncoderOH(total_hits))
    // val hit_way = OHToUInt(total_hits)


    //Check if the entry read by ftbBank is legal.
    // for (n <- 0 to numWays -1 ) {
    //   val req_pc_reg = RegEnable(io.req.bits.pc, io.req.valid)
    //   val ftb_entry_fallThrough = read_entries(n).getFallThrough(req_pc_reg)
    //   when(read_entries(n).valid && total_hits(n) && io.s1_fire){
    //     assert(req_pc_reg + (2*PredictWidth).U >= ftb_entry_fallThrough, s"FTB sram entry in way${n} fallThrough address error!")
    //   }
    // }

    val u_total_hits = VecInit((0 until numWays).map(b =>
        ftb.io.r.resp.data(b).tag === u_req_tag && ftb.io.r.resp.data(b).entry.valid && RegNext(io.update_access)))
    val u_hit = u_total_hits.reduce(_||_)
    // val hit_way_1h = VecInit(PriorityEncoderOH(total_hits))
    // val u_hit_way = OHToUInt(u_total_hits)

    // assert(PopCount(total_hits) === 1.U || PopCount(total_hits) === 0.U)
    // assert(PopCount(u_total_hits) === 1.U || PopCount(u_total_hits) === 0.U)
    // for (n <- 1 to numWays) {
    //   XSPerfAccumulate(f"ftb_pred_${tableIdx}_way_hit", hit)
    //   XSPerfAccumulate(f"ftb_update_${n}_way_hit", u_hit)
    // }


    io.read_resp := read_entries(0) // Mux1H
    io.read_hit := hit 
    io.read_idx := real_idx

    io.ftb_r_entry := ftb_r_entries(0)

    io.update_hit := u_hit

    // reset u
    // val needReset = RegInit(false.B)
    // val useful_can_reset = !(io.req.fire || io.update_write_data.valid) && needReset
    // val (resetSet, resetFinish) = Counter(useful_can_reset, nRows)
    // when(io.u_reset_u){
    //   needReset := true.B
    // }.elsewhen(resetFinish){
    //   needReset := false
    // }


    XSPerfAccumulate(f"ftb_pred_${tableIdx}_way_hit", hit)
    XSPerfAccumulate(f"ftb_update_${tableIdx}_way_hit",u_hit)
    XSPerfAccumulate(f"ftb_hit_way${tableIdx}", hit && !io.update_access)


    // Update logic
    val update_valid = io.update_write_data.valid
    val update_data = io.update_write_data.bits
    val update_idx = io.update_idx
    // val allocWriteWay = allocWay(RegNext(VecInit(ftb_r_entries.map(_.valid))).asUInt, u_idx)
    // val u_way = Mux(io.update_write_alloc, allocWriteWay, io.update_write_way)
    // val u_mask = UIntToOH(u_way)



    // for (i <- 0 until numWays) {
    //   XSPerfAccumulate(f"ftb_replace_way$i", u_valid && io.update_write_alloc && u_way === i.U)
    //   XSPerfAccumulate(f"ftb_replace_way${i}_has_empty", u_valid && io.update_write_alloc && !ftb_r_entries.map(_.valid).reduce(_&&_) && u_way === i.U)
    //   XSPerfAccumulate(f"ftb_hit_way$i", hit && !io.update_access && hit_way === i.U)
    // }

    ftb.io.w.apply(update_valid, update_data, update_idx,true.B)

    // for replacer
    // write_set := u_idx
    // write_way.valid := u_valid
    // write_way.bits := Mux(io.update_write_alloc, allocWriteWay, io.update_write_way)

    // print hit entry info
    Mux1H(total_hits, ftb.io.r.resp.data).display(true.B)
  } // FTBBank

  //FTB switch register & temporary storage of fauftb prediction results
  val s0_close_ftb_req = RegInit(false.B)
  val s1_close_ftb_req = RegEnable(s0_close_ftb_req, false.B, io.s0_fire(0))
  val s2_close_ftb_req = RegEnable(s1_close_ftb_req, false.B, io.s1_fire(0))
  val s2_fauftb_ftb_entry_dup = io.s1_fire.map(f => RegEnable(io.fauftb_entry_in, f))
  val s2_fauftb_ftb_entry_hit_dup = io.s1_fire.map(f => RegEnable(io.fauftb_entry_hit_in, f))


  // indir pred
  val indirEntries = Seq.tabulate(indirEntriesWays)(w => RegInit(0.U(38.W)))
  val indir_replacer = ReplacementPolicy.fromString("plru",indirEntriesWays)
  val indir_replacer_touch_ways = Wire(Vec(2, Valid(UInt(log2Ceil(indirEntriesWays).W))))
  val s0_indir_req_pc = s0_pc_dup(0)(38,1)
  val s0_indir_hitWay = VecInit(indirEntries.map(_ === s0_indir_req_pc))
  val s0_indir_valid = s0_indir_hitWay.reduce(_||_)
  val s1_indir_hitWay = RegEnable(s0_indir_hitWay.asUInt,0.U,io.s0_fire(0) && s0_indir_valid)
  val s1_indir_valid = RegEnable(s0_indir_valid,false.B,io.s0_fire(0))
  val s2_indir_valid = RegEnable(s1_indir_valid,false.B,io.s1_fire(0))

  XSPerfAccumulate("s0_indir_valid", s0_indir_valid && io.s0_fire(0))

  indir_replacer_touch_ways(0).valid := RegNext(io.s1_fire(0) && s1_indir_valid)
  indir_replacer_touch_ways(0).bits := RegEnable(s1_indir_hitWay,io.s1_fire(0) && s1_indir_valid)
  
  val s1_providerTarget    = Wire(UInt(VAddrBits.W))
  val s1_altProviderTarget = Wire(UInt(VAddrBits.W))
  val s1_provided          = Wire(Bool())
  val s1_provider          = Wire(UInt(log2Ceil(ITTageNTables).W))
  val s1_altProvided       = Wire(Bool())
  val s1_altProvider       = Wire(UInt(log2Ceil(ITTageNTables).W))
  val s1_providerU         = Wire(Bool())
  val s1_providerCtr       = Wire(UInt(ctrBits.W))
  val s1_altProviderCtr    = Wire(UInt(ctrBits.W))
  val s1_altDiffers        = Wire(Bool())

  val s2_providerTarget    = RegEnable(s1_providerTarget,io.s1_fire(0))
  val s2_altProviderTarget = RegEnable(s1_altProviderTarget,io.s1_fire(0))
  val s2_provided          = RegEnable(s1_provided,io.s1_fire(0))
  val s2_provider          = RegEnable(s1_provider,io.s1_fire(0))
  val s2_altProvided       = RegEnable(s1_altProvided,io.s1_fire(0))
  val s2_altProvider       = RegEnable(s1_altProvider,io.s1_fire(0))
  val s2_providerU         = RegEnable(s1_providerU,io.s1_fire(0))
  val s2_providerCtr       = RegEnable(s1_providerCtr,io.s1_fire(0))
  val s2_altProviderCtr    = RegEnable(s1_altProviderCtr,io.s1_fire(0))
  val s2_altDiffers        = RegEnable(s1_altDiffers,io.s1_fire(0))

  // val useAltOnNa = RegInit((1 << (UAONA_bits-1)).U(UAONA_bits.W))
  val tickCtr = RegInit(0.U(TickWidth.W))



  // indir
  // val ftbBank = Module(new FTBBank(numSets, numWays))
  val ftbBanks = ITTageTableInfos.zipWithIndex.map{
    case ((nRows,histLen,tagLen),i) =>
    val t = Module(new FTBBank(numSets,numWays,nRows,histLen,tagLen,i))
    t.io.req.valid := io.s0_fire(0) && !s0_close_ftb_req
    t.io.req.bits.pc := s0_pc_dup(0)
    t.io.req.bits.folded_hist := io.in.bits.folded_hist(0)
    t.io.req.bits.indir_valid := s0_indir_valid
    t
  }
  override def getFoldedHistoryInfo = Some(ftbBanks.map(_.getFoldedHistoryInfo).reduce(_++_))


  // not indir resp
  val s1_resps = VecInit(ftbBanks.map(t => t.io.read_resp))
  val s1_hits = VecInit(ftbBanks.map(t => t.io.read_hit))
  val s1_hit_way = OHToUInt(s1_hits)
  // val s1_hit = s1_hits.reduce(_||_) && io.ctrl.btb_enable
  val s1_resp = Mux1H(s1_hits,s1_resps)
  
  val s1_multi_hit = VecInit((0 until ITTageNTables).map{
      i => (0 until ITTageNTables).map(j => {
        if(i < j) s1_hits(i) && s1_hits(j)
        else false.B
      }).reduce(_||_)
    }).reduce(_||_)
  XSPerfAccumulate("ftb_multi_hit",s1_multi_hit && !s1_indir_valid)


  // ftb plru
  val replacer = ReplacementPolicy.fromString(Some("setplru"), ITTageNTables, numSets)

  val touch_set = Seq.fill(1)(Wire(UInt(log2Ceil(numSets).W)))
  val touch_way = Seq.fill(1)(Wire(Valid(UInt(log2Ceil(ITTageNTables).W))))

  val write_set = Wire(UInt(log2Ceil(numSets).W))
  val write_way = Wire(Valid(UInt(log2Ceil(ITTageNTables).W)))

  val read_set = Wire(UInt(log2Ceil(numSets).W))
  val read_way = Wire(Valid(UInt(log2Ceil(ITTageNTables).W)))

  read_set := ftbBanks(0).io.read_idx
  read_way.valid := s1_hits.reduce(_||_)
  read_way.bits  := s1_hit_way

  // Read replacer access is postponed for 1 cycle
  // this helps timing
  touch_set(0) := Mux(write_way.valid, write_set, RegNext(read_set))
  touch_way(0).valid := write_way.valid || RegNext(read_way.valid)
  touch_way(0).bits := Mux(write_way.valid, write_way.bits, RegNext(read_way.bits))

  replacer.access(touch_set, touch_way)

  // Select the update allocate way
  // Selection logic:
  //    1. if any entries within the same index is not valid, select it
  //    2. if all entries is valid, use replacer
  def allocWay(valids: UInt, idx: UInt): UInt = {
    if (ITTageNTables > 1) {
      val w = Wire(UInt(log2Up(ITTageNTables).W))
      val valid = WireInit(valids.andR)
      w := Mux(valid, replacer.way(idx), PriorityEncoder(~valids))
      w
    } else {
      val w = WireInit(0.U(log2Up(numWays).W))
      w
    }
  }

  class indirTableInfo(implicit p: Parameters) extends XSBundle with FTBParams with BPUUtils{
    val tableIdx = UInt(log2Ceil(ITTageNTables).W)
    val ctr = UInt(ctrBits.W)
    val u = Bool()
    val entry = new FTBEntry
  }

  // indir resp
  val inputRes = VecInit(s1_resps.zipWithIndex.map{case (r,i) => {
    val tableInfo = Wire(new indirTableInfo)
    tableInfo.u := r.u
    tableInfo.ctr := r.ctr
    tableInfo.entry := r
    tableInfo.tableIdx := i.U(log2Ceil(ITTageNTables).W)
    SelectTwoInterRes(s1_hits(i) && s1_indir_valid,tableInfo)
  }})
  val selectedInfo = ParallelSelectTwo(inputRes.reverse)
  val provided = selectedInfo.hasOne
  val altProvided = selectedInfo.hasTwo

  val providerInfo = selectedInfo.first
  val altProviderInfo = selectedInfo.second
  val providerNull = providerInfo.ctr === 0.U

  val s1_indir_resp = Mux1H(Seq(
    (provided && !(providerNull && altProvided), providerInfo.entry),
    (altProvided && providerNull, altProviderInfo.entry),
    (!provided, s1_resp)
  ))
  val s1_ftb_entry = Mux(s1_indir_valid,s1_indir_resp,s1_resp)
  s1_provided := provided
  s1_provider := providerInfo.tableIdx
  s1_altProvided := altProvided
  s1_altProvider := altProviderInfo.tableIdx
  s1_providerU := providerInfo.u
  s1_providerCtr := providerInfo.ctr
  s1_altProviderCtr := altProviderInfo.ctr
  s1_providerTarget := providerInfo.entry.tailSlot.getTarget(s1_pc_dup(0))
  s1_altProviderTarget := altProviderInfo.entry.tailSlot.getTarget(s1_pc_dup(0))
  s1_altDiffers := s1_providerTarget =/= s1_altProviderTarget

  

  //for close ftb read_req
  // ftbBank.io.req_pc.valid := io.s0_fire(0) && !s0_close_ftb_req
  // ftbBank.io.req_pc.bits := s0_pc_dup(0)

  // val s1_multi_way = PriorityMux(Seq.tabulate(numWays)(i => ((s1_hits(i)) -> i.asUInt(log2Ceil(numWays).W))))
  // val s1_multi_hit_selectEntry = PriorityMux(Seq.tabulate(numWays)(i => ((s1_hits(i)) -> s1_resps(i))))
  // val s2_multi_hit = RegEnable(s1_multi_hit,io.s1_fire(0))
  // val s2_multi_hit_way = RegEnable(s1_multi_way,io.s1_fire(0))
  // val s2_multi_hit_entry = RegEnable(s1_multi_hit_selectEntry,io.s1_fire(0))
  // val s2_multi_hit_enable = s2_multi_hit && io.s2_redirect(0)
  // XSPerfAccumulate("ftb_s2_multi_hit", s2_multi_hit)
  // XSPerfAccumulate("ftb_s2_multi_hit_enable", s2_multi_hit_enable)

  //After closing ftb, the entry output from s2 is the entry of FauFTB cached in s1
  val btb_enable_dup = dup(RegNext(io.ctrl.btb_enable))
  val s1_read_resp = Mux(s1_close_ftb_req, io.fauftb_entry_in, s1_ftb_entry)
  val s2_ftbBank_dup = io.s1_fire.map(f => RegEnable(s1_ftb_entry, f))
  val s2_ftb_entry_dup = dup(0.U.asTypeOf(new FTBEntry))
  for(((s2_fauftb_entry, s2_ftbBank_entry), s2_ftb_entry) <-
    s2_fauftb_ftb_entry_dup zip s2_ftbBank_dup zip s2_ftb_entry_dup){
      s2_ftb_entry := Mux(s2_close_ftb_req, s2_fauftb_entry, s2_ftbBank_entry)
  }
  val s3_ftb_entry_dup  = io.s2_fire.zip(s2_ftb_entry_dup).map {case (f, e) => RegEnable( e, f)}

  //After closing ftb, the hit output from s2 is the hit of FauFTB cached in s1.
  //s1_hit is the ftbBank hit.
  val s1_hit = Mux(s1_close_ftb_req, false.B, s1_hits.reduce(_||_) && io.ctrl.btb_enable)
  val s2_ftb_hit_dup = io.s1_fire.map(f => RegEnable(s1_hit, 0.B, f))
  val s2_hit_dup = dup(0.U.asTypeOf(Bool()))
  for(((s2_fauftb_hit, s2_ftb_hit), s2_hit) <-
    s2_fauftb_ftb_entry_hit_dup zip s2_ftb_hit_dup zip s2_hit_dup){
      s2_hit := Mux(s2_close_ftb_req, s2_fauftb_hit, s2_ftb_hit)
  }
  val s3_hit_dup = io.s2_fire.zip(s2_hit_dup).map {case (f, h) => RegEnable( h, 0.B, f)}
  // val s3_mult_hit_dup = io.s2_fire.map(f => RegEnable(s2_multi_hit_enable,f))
  val s1_writeWay = Mux(s1_close_ftb_req, 0.U, s1_hit_way)
  val s2_writeWay = RegEnable(s1_hit_way,io.s1_fire(0))

  val s2_ftb_meta = RegInit(0.U.asTypeOf(new FTBMeta))
  s2_ftb_meta.writeWay := s2_writeWay.asUInt
  s2_ftb_meta.hit := s2_ftb_hit_dup(0)
  s2_ftb_meta.pred_cycle.map(_ := GTimer())
  s2_ftb_meta.indir := s2_indir_valid
  s2_ftb_meta.provider.valid := s2_provided
  s2_ftb_meta.provider.bits  := s2_provider
  s2_ftb_meta.altProvider.valid := s2_altProvided
  s2_ftb_meta.altProvider.bits := s2_altProvider
  s2_ftb_meta.altDiffers := s2_altDiffers
  s2_ftb_meta.providerU := s2_providerU
  s2_ftb_meta.providerCtr := s2_providerCtr
  s2_ftb_meta.altProviderCtr := s2_altProviderCtr
  s2_ftb_meta.providerTarget := s2_providerTarget
  s2_ftb_meta.altProviderTarget := s2_altProviderTarget
  val s1_allocatableSlots = VecInit(s1_resps.map(r => !r.valid && !r.u)).asUInt &
    ~(LowerMask(UIntToOH(s2_provider), ITTageNTables) & Fill(ITTageNTables, s2_provided.asUInt))
  val s1_allocLFSR   = random.LFSR(width = 15)(ITTageNTables - 1, 0)
  val s1_firstEntry  = PriorityEncoder(s1_allocatableSlots)
  val s1_maskedEntry = PriorityEncoder(s1_allocatableSlots & s1_allocLFSR)
  val s1_allocEntry  = Mux(s1_allocatableSlots(s1_maskedEntry), s1_maskedEntry, s1_firstEntry)
  s2_ftb_meta.allocate.valid := RegEnable(s1_allocatableSlots =/= 0.U, io.s1_fire(0))
  s2_ftb_meta.allocate.bits  := RegEnable(s1_allocEntry, io.s1_fire(0))

  // val s2_multi_hit_meta = FTBMeta(s2_multi_hit_way.asUInt, s2_multi_hit, GTimer(),s2_indir_valid).asUInt

  //Consistent count of entries for fauftb and ftb
  val fauftb_ftb_entry_consistent_counter = RegInit(0.U(FTBCLOSE_THRESHOLD_SZ.W))
  val fauftb_ftb_entry_consistent = s2_fauftb_ftb_entry_dup(0).entryConsistent(s2_ftbBank_dup(0))

  //if close ftb_req, the counter need keep
  when(io.s2_fire(0) && s2_fauftb_ftb_entry_hit_dup(0) && s2_ftb_hit_dup(0) ){
    fauftb_ftb_entry_consistent_counter := Mux(fauftb_ftb_entry_consistent, fauftb_ftb_entry_consistent_counter + 1.U, 0.U)
  } .elsewhen(io.s2_fire(0) && !s2_fauftb_ftb_entry_hit_dup(0) && s2_ftb_hit_dup(0) ){
    fauftb_ftb_entry_consistent_counter := 0.U
  }


  // off
  when((fauftb_ftb_entry_consistent_counter >= FTBCLOSE_THRESHOLD) && io.s0_fire(0)){
    s0_close_ftb_req := true.B
  }

  //Clear counter during false_hit or ifuRedirect
  val ftb_false_hit = WireInit(false.B)
  val needReopen = s0_close_ftb_req && (ftb_false_hit || io.redirectFromIFU)
  ftb_false_hit := io.update.valid && io.update.bits.false_hit
  when(needReopen){
    fauftb_ftb_entry_consistent_counter := 0.U
    s0_close_ftb_req := false.B
  }

  val s2_close_consistent = s2_fauftb_ftb_entry_dup(0).entryConsistent(s2_ftb_entry_dup(0))
  val s2_not_close_consistent = s2_ftbBank_dup(0).entryConsistent(s2_ftb_entry_dup(0))

  when(s2_close_ftb_req && io.s2_fire(0)){
    assert(s2_close_consistent, s"Entry inconsistency after ftb req is closed!")
  }.elsewhen(!s2_close_ftb_req &&  io.s2_fire(0)){
    assert(s2_not_close_consistent, s"Entry inconsistency after ftb req is not closed!")
  }

  val  reopenCounter = !s1_close_ftb_req && s2_close_ftb_req &&  io.s2_fire(0)
  val  falseHitReopenCounter = ftb_false_hit && s1_close_ftb_req
  XSPerfAccumulate("ftb_req_reopen_counter", reopenCounter)
  XSPerfAccumulate("false_hit_reopen_Counter", falseHitReopenCounter)
  XSPerfAccumulate("ifuRedirec_needReopen",s1_close_ftb_req && io.redirectFromIFU)
  XSPerfAccumulate("this_cycle_is_close",s2_close_ftb_req && io.s2_fire(0))
  XSPerfAccumulate("this_cycle_is_open",!s2_close_ftb_req && io.s2_fire(0))

  // io.out.bits.resp := RegEnable(io.in.bits.resp_in(0), 0.U.asTypeOf(new BranchPredictionResp), io.s1_fire)
  io.out := io.in.bits.resp_in(0)

  io.out.s2.full_pred.map {case fp => fp.multiHit := false.B}

  io.out.s2.full_pred.zip(s2_hit_dup).map {case (fp, h) => fp.hit := h}
  for (full_pred & s2_ftb_entry & s2_pc & s1_pc & s1_fire <-
    io.out.s2.full_pred zip s2_ftb_entry_dup zip s2_pc_dup zip s1_pc_dup zip io.s1_fire) {
      full_pred.fromFtbEntry(s2_ftb_entry,
        s2_pc.getAddr(),
        // Previous stage meta for better timing
        Some(s1_pc, s1_fire),
        Some(s1_read_resp, s1_fire)
      )
  }

  io.out.s3.full_pred.zip(s3_hit_dup).map {case (fp, h) => fp.hit := h}
  // io.out.s3.full_pred.zip(s3_mult_hit_dup).map {case (fp, m) => fp.multiHit := m}
  for (full_pred & s3_ftb_entry & s3_pc & s2_pc & s2_fire <-
    io.out.s3.full_pred zip s3_ftb_entry_dup zip s3_pc_dup zip s2_pc_dup zip io.s2_fire)
      full_pred.fromFtbEntry(s3_ftb_entry, s3_pc.getAddr(), Some((s2_pc.getAddr(), s2_fire)))

  io.out.last_stage_ftb_entry := s3_ftb_entry_dup(0)
  io.out.last_stage_meta := RegEnable( s2_ftb_meta.asUInt, io.s2_fire(0))
  io.out.s1_ftbCloseReq := s1_close_ftb_req
  io.out.s1_uftbHit := io.fauftb_entry_hit_in
  val s1_uftbHasIndirect = io.fauftb_entry_in.jmpValid &&
    io.fauftb_entry_in.isJalr && !io.fauftb_entry_in.isRet // uFTB determines that it's real JALR, RET and JAL are excluded
  io.out.s1_uftbHasIndirect := s1_uftbHasIndirect

  // always taken logic
  for (i <- 0 until numBr) {
    for (out_fp & in_fp & s2_hit & s2_ftb_entry <-
      io.out.s2.full_pred zip io.in.bits.resp_in(0).s2.full_pred zip s2_hit_dup zip s2_ftb_entry_dup)
      out_fp.br_taken_mask(i) := in_fp.br_taken_mask(i) || s2_hit && s2_ftb_entry.always_taken(i)
    for (out_fp & in_fp & s3_hit & s3_ftb_entry <-
      io.out.s3.full_pred zip io.in.bits.resp_in(0).s3.full_pred zip s3_hit_dup zip s3_ftb_entry_dup)
      out_fp.br_taken_mask(i) := in_fp.br_taken_mask(i) || s3_hit && s3_ftb_entry.always_taken(i)
  }

  // Update logic
  val update = io.update.bits

  val u_meta = update.meta.asTypeOf(new FTBMeta)
  val u_valid = io.update.valid && !io.update.bits.old_entry
  val u_indir_valid =
    update.is_jalr && !update.is_ret && u_valid && update.ftb_entry.jmpValid &&
    update.jmp_taken && update.cfi_idx.valid && update.cfi_idx.bits === update.ftb_entry.tailSlot.offset
  val updateMisPred = update.mispred_mask(numBr)

  val update_now = u_valid && u_meta.hit
  val update_need_read = u_valid && !u_meta.hit
  // stall one more cycle because we use a whole cycle to do update read tag hit
  io.s1_ready := ftbBanks.map(_.io.req.ready).reduce(_&&_) && !(update_need_read) && !RegNext(update_need_read)


  ftbBanks.map{ t => {
      t.io.u_req.valid := update_need_read
      t.io.u_req.bits.pc := update.pc
      t.io.u_req.bits.ghist := update.ghist
      t.io.u_req.bits.indir_valid := u_indir_valid
    }
  }


  val updateMask      = WireInit(0.U.asTypeOf(Vec(ITTageNTables, Bool())))
  val updateUMask     = WireInit(0.U.asTypeOf(Vec(ITTageNTables, Bool())))
  val updateResetU    = WireInit(false.B)
  val updateCorrect   = Wire(Vec(ITTageNTables, Bool()))
  val updateTarget    = Wire(Vec(ITTageNTables, UInt(VAddrBits.W)))
  val updateOldTarget = Wire(Vec(ITTageNTables, UInt(VAddrBits.W)))
  val updateAlloc     = Wire(Vec(ITTageNTables, Bool()))
  val updateOldCtr    = Wire(Vec(ITTageNTables, UInt(ctrBits.W)))
  val updateU         = Wire(Vec(ITTageNTables, Bool()))
  updateCorrect   := DontCare
  updateTarget  := DontCare
  updateOldTarget  := DontCare
  updateAlloc   := DontCare
  updateOldCtr  := DontCare
  updateU       := DontCare

  val updateRealTarget = update.full_target  
  when(u_indir_valid){
    when(u_meta.provider.valid){
      val provider = u_meta.provider.bits
      val altProvider = u_meta.altProvider.bits
      val usedAltpred = u_meta.altProvider.valid && u_meta.providerCtr === 0.U
      when(usedAltpred && updateMisPred){
        updateMask(altProvider)    := true.B
        updateUMask(altProvider)   := false.B
        updateCorrect(altProvider) := false.B
        updateOldCtr(altProvider)  := u_meta.altProviderCtr
        updateAlloc(altProvider)   := false.B
      }
      updateMask(provider) := true.B     
      updateUMask(provider)  := true.B
      updateU(provider) := Mux(!u_meta.altDiffers, u_meta.providerU, !updateMisPred)
      updateCorrect(provider)  := u_meta.providerTarget === updateRealTarget
      updateTarget(provider) := updateRealTarget
      updateOldTarget(provider) := u_meta.providerTarget
      updateOldCtr(provider) := u_meta.providerCtr
      updateAlloc(provider)  := false.B


      
    }
  }
  XSPerfAccumulate("ftb_provider_target_correct",u_meta.providerTarget === updateRealTarget)

  val providerCorrect = u_meta.provider.valid && u_meta.providerTarget === updateRealTarget
  val providerUnconf = u_meta.providerCtr === 0.U
  when (u_indir_valid && updateMisPred && !(providerCorrect && providerUnconf)) {
    val allocate = u_meta.allocate
    tickCtr := satUpdate(tickCtr, TickWidth, !allocate.valid)
    when (allocate.valid) {
      updateMask(allocate.bits)  := true.B
      updateCorrect(allocate.bits) := true.B // useless for alloc
      updateTarget(allocate.bits) := updateRealTarget
      updateAlloc(allocate.bits) := true.B
      updateUMask(allocate.bits) := true.B
      updateU(allocate.bits) := false.B
    }
  }

  when (tickCtr === ((1 << TickWidth) - 1).U) {
    tickCtr := 0.U
    updateResetU := true.B
  }


  val update_entry = Wire(Vec(ITTageNTables,new FTBEntryWithTag))
  val update_indir_idx = WireInit(0.U.asTypeOf(Vec(ITTageNTables,UInt(log2Ceil(numSets).W))))


  def getUnhashedIdx(pc: UInt): UInt = pc >> instOffsetBits
  val u_unhashed_idx = getUnhashedIdx(update.pc)
  val u_ghist = update.ghist
  val idx_and_tag = ITTageTableInfos.map{
    case (nRows,histLen,tagLen) =>
    val idxFhInfo = (histLen, min(log2Ceil(nRows), histLen))
    val tagFhInfo = (histLen, min(histLen, tagLen))
    val altTagFhInfo = (histLen, min(histLen, tagLen-1))
    val allFhInfos = Seq(idxFhInfo, tagFhInfo, altTagFhInfo)
    def compute_tag_and_hash(unhashed_idx: UInt, allFh: AllFoldedHistories) = {
      if (histLen > 0) {
        val idx_fh = allFh.getHistWithInfo(idxFhInfo).folded_hist
        val tag_fh = allFh.getHistWithInfo(tagFhInfo).folded_hist
        val alt_tag_fh = allFh.getHistWithInfo(altTagFhInfo).folded_hist
        // require(idx_fh.getWidth == log2Ceil(nRows))
        val idx = (unhashed_idx ^ idx_fh)(log2Ceil(nRows)-1, 0)
        val tag = ((unhashed_idx >> log2Ceil(nRows)) ^ tag_fh ^ (alt_tag_fh << 1)) (tagLen - 1, 0)
        (idx, tag)
      }
      else {
        require(tagLen == 0)
        (unhashed_idx(log2Ceil(nRows)-1, 0), 0.U)
      }
    }
    def compute_folded_hist(hist: UInt, l: Int)(histLen: Int) = {
    if (histLen > 0) {
      val nChunks = (histLen + l - 1) / l
      val hist_chunks = (0 until nChunks) map {i =>
        hist(min((i+1)*l, histLen)-1, i*l)
      }
      ParallelXOR(hist_chunks)
    }
    else 0.U
    }
    val update_folded_hist = WireInit(0.U.asTypeOf(new AllFoldedHistories(foldedGHistInfos)))
    update_folded_hist.getHistWithInfo(idxFhInfo).folded_hist := compute_folded_hist(u_ghist, log2Ceil(nRows))(histLen)
    update_folded_hist.getHistWithInfo(tagFhInfo).folded_hist := compute_folded_hist(u_ghist, tagLen)(histLen)
    update_folded_hist.getHistWithInfo(altTagFhInfo).folded_hist := compute_folded_hist(u_ghist, tagLen-1)(histLen)
    dontTouch(update_folded_hist)
    val (update_idx, update_tag) = compute_tag_and_hash(u_unhashed_idx, update_folded_hist)
    (update_idx,update_tag)
  }

  val old_ctr = WireInit(update.ftb_entry.ctr)
  update_entry.zipWithIndex.map{
    case(f,i) =>
      f.entry := update.ftb_entry
      f.tag := Mux(u_indir_valid,idx_and_tag(i)._2,ftbAddr.getTag(update.pc)(tagSize-1, 0))
    when(u_indir_valid && updateMask(i)){
      f.entry.ctr := Mux(updateAlloc(i),2.U,inc_ctr(old_ctr, updateCorrect(i)))
      f.entry.u := updateU(i)
    }
    update_indir_idx(i) := Mux(u_indir_valid,idx_and_tag(i)._1,ftbAddr.getIdx(update.pc))
  }


  val (_, delay2_pc) = DelayNWithValid(update.pc, u_valid, 2)
  val (_,delay2_entry) = DelayNWithValid(update_entry,u_valid, 2)
  // val (_, delay2_ghist) = DelayNWithValid(update.ghist, u_valid, 2)
  val delay2_indir_valid = DelayN(u_indir_valid, 2)
  val (_,delay2_idx) = DelayNWithValid(update_indir_idx,u_valid, 2)
  val (_,delay2_updateMask) = DelayNWithValid(updateMask, u_valid,2)









  // indir
  val u_indir_pc = update.pc(38,1)
  val u_indir_alloc_way = indir_replacer.way
  val u_hit_oh = VecInit(indirEntries.map(_ === u_indir_pc)).asUInt
  val u_indir_alloc_way_oh = Mux(u_meta.indir,u_hit_oh,UIntToOH(u_indir_alloc_way))
  val indir_correct = u_meta.indir === update.is_jalr && u_indir_valid
  val u_indir_ways_write_valid = VecInit((0 until indirEntriesWays).map(w => u_indir_alloc_way_oh(w).asBool && u_indir_valid))

  XSPerfAccumulate("indir_total", u_indir_valid)
  XSPerfAccumulate("indir_pred_correct", indir_correct)
  // XSPerfAccumulate("indir_update_valid", u_indir_valid)

  

  for(w <- 0 until indirEntriesWays){
    when(u_indir_ways_write_valid(w)){
      indirEntries(w) := u_indir_pc
    }
  }


  indir_replacer_touch_ways(1).valid := u_indir_valid
  indir_replacer_touch_ways(1).bits  := OHToUInt(u_indir_alloc_way_oh)

  indir_replacer.access(indir_replacer_touch_ways)



  // val ftb_write = Wire(Vec(ITTageNTables,new FTBEntryWithTag))

  // val old_u = WireInit(update.ftb_entry.u)

  // ftb_write.entry := Mux(update_now, update.ftb_entry, delay2_entry)
  // ftb_write.tag   := ftbAddr.getTag(Mux(update_now, update.pc, delay2_pc))(tagSize-1, 0)
  val ftb_write = Mux(update_now,update_entry,delay2_entry)
  val update_indir_valid = Mux(update_now,u_indir_valid,delay2_indir_valid)
  // val update_indir_valid = u_indir_valid

  val u_hits = VecInit(ftbBanks.map(t => t.io.update_hit))
  val u_hit = u_hits.reduce(_||_)
  val u_hit_way = OHToUInt(u_hits)
  val write_valid = update_now || DelayN(u_valid && !u_meta.hit, 2)
  val write_pc    = Mux(update_now, update.pc, delay2_pc)
  val write_alloc = Mux(update_now,false.B,RegNext(!u_hit))
  val update_write_way = Mux(update_now, u_meta.writeWay, RegNext(u_hit_way))
  val indir_alloc_UInt = OHToUInt(updateAlloc)
  val indir_alloc_idx = Mux(update_now,update_indir_idx(indir_alloc_UInt),delay2_idx(indir_alloc_UInt))
  val alloc_idx = Mux(update_indir_valid,indir_alloc_idx,ftbAddr.getIdx(write_pc))
  val allocWriteWay = allocWay(RegNext(VecInit(ftbBanks.map(t => t.io.ftb_r_entry.valid))).asUInt,alloc_idx )
  val write_way_UInt = Mux(write_alloc,allocWriteWay,update_write_way)
  val write_way_mask = UIntToOH(write_way_UInt)




  for(i <- 0 until ITTageNTables){
    val update_idx = Mux(update_now,update_indir_idx(i),delay2_idx(i))
    val update_mask = Mux(update_now,updateMask(i),delay2_updateMask(i))

    ftbBanks(i).io.update_write_data.valid := Mux(update_indir_valid,update_mask ,write_valid && write_way_mask(i))
    ftbBanks(i).io.update_write_data.bits := ftb_write(i)
    ftbBanks(i).io.update_idx := update_idx
    ftbBanks(i).io.update_access := u_valid && !u_meta.hit
    ftbBanks(i).io.s1_fire := io.s1_fire(0)


    XSPerfAccumulate(f"indir_update_way$i", update_mask)
    XSPerfAccumulate(f"ftb_update_u_0" , update_mask && ftb_write(i).entry.u === 0.U)
    XSPerfAccumulate(f"ftb_update_u_1" , update_mask && ftb_write(i).entry.u === 1.U)
    for(j <- 0 until 4){
      XSPerfAccumulate(f"ftb_update_ctr_$j" , update_mask && ftb_write(i).entry.ctr === j.U)
    }
    
    write_set := update_idx
    write_way.valid := Mux(update_indir_valid,update_mask ,write_valid && write_way_mask(i))
    write_way.bits := Mux(write_alloc, allocWriteWay, update_write_way)

    
  }


  for(i <- 0 until ITTageNTables){
    XSPerfAccumulate(f"ftb_replace_way$i", write_valid && write_alloc && write_way_UInt === i.U)
    XSPerfAccumulate(f"ftb_replace_way${i}_has_empty", write_valid && write_alloc && !ftbBanks.map(t => t.io.ftb_r_entry.valid).reduce(_&&_) && write_way_UInt === i.U)

  }

  // ftb replace

  val updateMask_Muti = VecInit((0 until ITTageNTables).map{
      i => (0 until ITTageNTables).map(j => {
        if(i < j) updateMask(i) && updateMask(j)
        else false.B
      }).reduce(_||_)
    }).reduce(_||_)
  XSPerfAccumulate("updateMask_over_1",updateMask_Muti)

  // write_set := ftbAddr.getIdx(write_pc)
  // write_way.valid := write_valid
  // write_way.bits := Mux(write_alloc, allocWriteWay, update_write_way)




  // val ftb_write_fallThrough = ftb_write(0).entry.getFallThrough(write_pc)
  // when(write_valid){
  //   assert(write_pc + (FetchWidth * 4).U >= ftb_write_fallThrough, s"FTB write_entry fallThrough address error!")
  // }

  XSDebug("req_v=%b, req_pc=%x, ready=%b (resp at next cycle)\n", io.s0_fire(0), s0_pc_dup(0), ftbBanks.map(t=> t.io.req.ready).reduce(_&&_))
  XSDebug("s2_hit=%b, hit_way=%b\n", s2_hit_dup(0), s2_writeWay.asUInt)
  XSDebug("s2_br_taken_mask=%b, s2_real_taken_mask=%b\n",
    io.in.bits.resp_in(0).s2.full_pred(0).br_taken_mask.asUInt, io.out.s2.full_pred(0).real_slot_taken_mask().asUInt)
  XSDebug("s2_target=%x\n", io.out.s2.getTarget(0))

  s2_ftb_entry_dup(0).display(true.B)

  def pred_perf(name: String, cond: Bool)   = XSPerfAccumulate(s"${name}_at_pred", cond && io.s2_fire(3))
  def commit_perf(name: String, cond: Bool) = XSPerfAccumulate(s"${name}_at_commit", cond && u_indir_valid)
  def ftb_perf(name: String, pred_cond: Bool, commit_cond: Bool) = {
    pred_perf(s"ftb_${name}", pred_cond)
    commit_perf(s"ftb_${name}", commit_cond)
  }

  val pred_use_provider = s2_provided && !ctr_null(s2_providerCtr)
  val pred_use_altpred = s2_provided && ctr_null(s2_providerCtr)
  val pred_use_ht_as_altpred = pred_use_altpred && s2_altProvided
  val pred_use_bim_as_altpred = pred_use_altpred && !s2_altProvided
  val pred_use_bim_as_pred = !s2_provided

  val commit_use_provider = u_meta.provider.valid && !ctr_null(u_meta.providerCtr)
  val commit_use_altpred = u_meta.provider.valid && ctr_null(u_meta.providerCtr)
  val commit_use_ht_as_altpred = commit_use_altpred && u_meta.altProvider.valid
  val commit_use_bim_as_altpred = commit_use_altpred && !u_meta.altProvider.valid
  val commit_use_bim_as_pred = !u_meta.provider.valid

  for (i <- 0 until ITTageNTables) {
    val pred_this_is_provider = s2_provider === i.U
    val pred_this_is_altpred  = s2_altProvider === i.U
    val commit_this_is_provider = u_meta.provider.bits === i.U
    val commit_this_is_altpred  = u_meta.altProvider.bits === i.U
    ftb_perf(s"table_${i}_final_provided",
      pred_use_provider && pred_this_is_provider,
      commit_use_provider && commit_this_is_provider
    )
    ftb_perf(s"table_${i}_provided_not_used",
      pred_use_altpred && pred_this_is_provider,
      commit_use_altpred && commit_this_is_provider
    )
    ftb_perf(s"table_${i}_alt_provider_as_final_pred",
      pred_use_ht_as_altpred && pred_this_is_altpred,
      commit_use_ht_as_altpred && commit_this_is_altpred
    )
    ftb_perf(s"table_${i}_alt_provider_not_used",
      pred_use_provider && pred_this_is_altpred,
      commit_use_provider && commit_this_is_altpred
    )
  }
  ftb_perf("provided", s2_provided && s2_indir_valid, u_meta.provider.valid)
  XSPerfAccumulate("ftb_indir_updated", u_indir_valid)


  XSPerfAccumulate("ftb_read_hits", RegNext(io.s0_fire(0)) && s1_hit)
  XSPerfAccumulate("ftb_read_misses", RegNext(io.s0_fire(0)) && !s1_hit)

  XSPerfAccumulate("ftb_commit_hits", io.update.valid && u_meta.hit)
  XSPerfAccumulate("ftb_commit_misses", io.update.valid && !u_meta.hit)

  XSPerfAccumulate("ftb_update_req", io.update.valid)
  XSPerfAccumulate("ftb_update_ignored", io.update.valid && io.update.bits.old_entry)
  XSPerfAccumulate("ftb_updated", u_valid)

  override val perfEvents = Seq(
    ("ftb_commit_hits            ", io.update.valid  &&  u_meta.hit),
    ("ftb_commit_misses          ", io.update.valid  && !u_meta.hit),
  )
  generatePerfEvent()
}
