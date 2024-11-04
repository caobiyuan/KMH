debImport "./build/2024-10-29@11:19:17.vcd.fsdb"
wvCreateWindow
wvSetPosition -win $_nWave2 {("G1" 0)}
wvOpenFile -win $_nWave2 \
           {/nfs/home/caobiyuan/KMH/build/2024-10-29@11:19:17.vcd.fsdb}
verdiWindowResize -win $_Verdi_1 "933" "427" "1253" "799"
wvSelectGroup -win $_nWave2 {G1}
verdiWindowResize -win $_Verdi_1 "933" "427" "1253" "799"
verdiSetActWin -dock widgetDock_MTB_SOURCE_TAB_1
wvSelectGroup -win $_nWave2 {G1}
wvGetSignalOpen -win $_nWave2
wvGetSignalSetScope -win $_nWave2 "/TOP"
verdiSetActWin -win $_nWave2
wvGetSignalSetScope -win $_nWave2 "/TOP/SimTop/l_soc"
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb"
wvSetPosition -win $_nWave2 {("G1" 1)}
wvSetPosition -win $_nWave2 {("G1" 1)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 1 )} 
wvSetPosition -win $_nWave2 {("G1" 1)}
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvGetSignalSetSignalFilter -win $_nWave2 "*req*"
wvSetPosition -win $_nWave2 {("G1" 1)}
wvSetPosition -win $_nWave2 {("G1" 1)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 1 )} 
wvSetPosition -win $_nWave2 {("G1" 1)}
wvGetSignalSetSignalFilter -win $_nWave2 "*"
wvSetPosition -win $_nWave2 {("G1" 1)}
wvSetPosition -win $_nWave2 {("G1" 1)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 1 )} 
wvSetPosition -win $_nWave2 {("G1" 1)}
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0"
wvSetPosition -win $_nWave2 {("G1" 2)}
wvSetPosition -win $_nWave2 {("G1" 2)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 2 )} 
wvSetPosition -win $_nWave2 {("G1" 2)}
wvSetCursor -win $_nWave2 5406.998861 -snap {("G2" 0)}
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvSetCursor -win $_nWave2 271.063267 -snap {("G1" 2)}
wvSetCursor -win $_nWave2 99.865414 -snap {("G1" 2)}
wvSetCursor -win $_nWave2 71.332439 -snap {("G1" 2)}
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoom -win $_nWave2 7075.860111 7358.234302
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomIn -win $_nWave2
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb"
wvGetSignalSetSignalFilter -win $_nWave2 "*pc*"
wvSetPosition -win $_nWave2 {("G1" 2)}
wvSetPosition -win $_nWave2 {("G1" 2)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 2 )} 
wvSetPosition -win $_nWave2 {("G1" 2)}
wvSetPosition -win $_nWave2 {("G1" 3)}
wvSetPosition -win $_nWave2 {("G1" 3)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 3 )} 
wvSetPosition -win $_nWave2 {("G1" 3)}
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors"
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq"
wvGetSignalSetSignalFilter -win $_nWave2 "*new*"
wvSetPosition -win $_nWave2 {("G1" 3)}
wvSetPosition -win $_nWave2 {("G1" 3)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 3 )} 
wvSetPosition -win $_nWave2 {("G1" 3)}
wvSetPosition -win $_nWave2 {("G1" 4)}
wvSetPosition -win $_nWave2 {("G1" 4)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 4 )} 
wvSetPosition -win $_nWave2 {("G1" 4)}
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomIn -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvGetSignalSetSignalFilter -win $_nWave2 "*valid*"
wvSetPosition -win $_nWave2 {("G1" 4)}
wvSetPosition -win $_nWave2 {("G1" 4)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 4 )} 
wvSetPosition -win $_nWave2 {("G1" 4)}
wvGetSignalSetSignalFilter -win $_nWave2 "*validEntry*"
wvSetPosition -win $_nWave2 {("G1" 4)}
wvSetPosition -win $_nWave2 {("G1" 4)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 4 )} 
wvSetPosition -win $_nWave2 {("G1" 4)}
wvGetSignalSetSignalFilter -win $_nWave2 "*validEntries*"
wvSetPosition -win $_nWave2 {("G1" 4)}
wvSetPosition -win $_nWave2 {("G1" 4)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 4 )} 
wvSetPosition -win $_nWave2 {("G1" 4)}
wvSetPosition -win $_nWave2 {("G1" 5)}
wvSetPosition -win $_nWave2 {("G1" 5)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 5 )} 
wvSetPosition -win $_nWave2 {("G1" 5)}
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvSelectSignal -win $_nWave2 {( "G1" 4 )} 
wvSetCursor -win $_nWave2 4408.344718 -snap {("G2" 0)}
wvSetCursor -win $_nWave2 4365.545254 -snap {("G1" 5)}
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvGetSignalSetSignalFilter -win $_nWave2 "*cancommit*"
wvSetPosition -win $_nWave2 {("G1" 5)}
wvSetPosition -win $_nWave2 {("G1" 5)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 5 )} 
wvSetPosition -win $_nWave2 {("G1" 5)}
wvSetPosition -win $_nWave2 {("G1" 6)}
wvSetPosition -win $_nWave2 {("G1" 6)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 6 )} 
wvSetPosition -win $_nWave2 {("G1" 6)}
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvZoomOut -win $_nWave2
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb"
wvGetSignalSetSignalFilter -win $_nWave2 "*"
wvSetPosition -win $_nWave2 {("G1" 6)}
wvSetPosition -win $_nWave2 {("G1" 6)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 6 )} 
wvSetPosition -win $_nWave2 {("G1" 6)}
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0"
wvGetSignalSetSignalFilter -win $_nWave2 "*req*"
wvSetPosition -win $_nWave2 {("G1" 6)}
wvSetPosition -win $_nWave2 {("G1" 6)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 6 )} 
wvSetPosition -win $_nWave2 {("G1" 6)}
wvSetPosition -win $_nWave2 {("G1" 7)}
wvSetPosition -win $_nWave2 {("G1" 7)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 7 )} 
wvSetPosition -win $_nWave2 {("G1" 7)}
wvSetCursor -win $_nWave2 142.664878 -snap {("G1" 4)}
wvSetPosition -win $_nWave2 {("G1" 8)}
wvSetPosition -win $_nWave2 {("G1" 8)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 8 )} 
wvSetPosition -win $_nWave2 {("G1" 8)}
wvScrollDown -win $_nWave2 0
wvScrollDown -win $_nWave2 0
wvScrollDown -win $_nWave2 0
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb"
wvSetPosition -win $_nWave2 {("G1" 9)}
wvSetPosition -win $_nWave2 {("G1" 9)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 9 )} 
wvSetPosition -win $_nWave2 {("G1" 9)}
wvGetSignalSetSignalFilter -win $_nWave2 "*s0_fire*"
wvSetPosition -win $_nWave2 {("G1" 9)}
wvSetPosition -win $_nWave2 {("G1" 9)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 9 )} 
wvSetPosition -win $_nWave2 {("G1" 9)}
wvSetPosition -win $_nWave2 {("G1" 10)}
wvSetPosition -win $_nWave2 {("G1" 10)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 10 )} 
wvSetPosition -win $_nWave2 {("G1" 10)}
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors"
wvSetPosition -win $_nWave2 {("G1" 11)}
wvSetPosition -win $_nWave2 {("G1" 11)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 11 )} 
wvSetPosition -win $_nWave2 {("G1" 11)}
wvGetSignalSetSignalFilter -win $_nWave2 "*s1_ready*"
wvSetPosition -win $_nWave2 {("G1" 11)}
wvSetPosition -win $_nWave2 {("G1" 11)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 11 )} 
wvSetPosition -win $_nWave2 {("G1" 11)}
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu"
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors"
wvSetPosition -win $_nWave2 {("G1" 12)}
wvSetPosition -win $_nWave2 {("G1" 12)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s1_ready} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 12 )} 
wvSetPosition -win $_nWave2 {("G1" 12)}
wvScrollDown -win $_nWave2 0
wvScrollDown -win $_nWave2 0
wvScrollDown -win $_nWave2 0
wvScrollUp -win $_nWave2 1
wvScrollDown -win $_nWave2 1
wvScrollDown -win $_nWave2 0
wvScrollDown -win $_nWave2 0
wvGetSignalSetSignalFilter -win $_nWave2 "*s1_valid*"
wvSetPosition -win $_nWave2 {("G1" 12)}
wvSetPosition -win $_nWave2 {("G1" 12)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s1_ready} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 12 )} 
wvSetPosition -win $_nWave2 {("G1" 12)}
wvGetSignalSetSignalFilter -win $_nWave2 "*valid*"
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu"
wvSetPosition -win $_nWave2 {("G1" 13)}
wvSetPosition -win $_nWave2 {("G1" 13)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s1_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_valid_dup_3} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 13 )} 
wvSetPosition -win $_nWave2 {("G1" 13)}
wvGetSignalSetSignalFilter -win $_nWave2 "*fire*"
wvSetPosition -win $_nWave2 {("G1" 13)}
wvSetPosition -win $_nWave2 {("G1" 13)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s1_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_valid_dup_3} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 13 )} 
wvSetPosition -win $_nWave2 {("G1" 13)}
wvSetPosition -win $_nWave2 {("G1" 14)}
wvSetPosition -win $_nWave2 {("G1" 14)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s1_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_valid_dup_3} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s0_fire_dup_0} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 14 )} 
wvSetPosition -win $_nWave2 {("G1" 14)}
wvSetPosition -win $_nWave2 {("G1" 15)}
wvSetPosition -win $_nWave2 {("G1" 15)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s1_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_valid_dup_3} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s0_fire_dup_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_fire_dup_0} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 15 )} 
wvSetPosition -win $_nWave2 {("G1" 15)}
wvSelectSignal -win $_nWave2 {( "G1" 11 )} 
wvGetSignalOpen -win $_nWave2
wvGetSignalSetScope -win $_nWave2 \
           "/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb"
wvGetSignalSetSignalFilter -win $_nWave2 "*allocwarite*"
wvSetPosition -win $_nWave2 {("G1" 15)}
wvSetPosition -win $_nWave2 {("G1" 15)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s1_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_valid_dup_3} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s0_fire_dup_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_fire_dup_0} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSetPosition -win $_nWave2 {("G1" 15)}
wvGetSignalSetSignalFilter -win $_nWave2 "*allocwrite*"
wvSetPosition -win $_nWave2 {("G1" 15)}
wvSetPosition -win $_nWave2 {("G1" 15)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s1_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_valid_dup_3} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s0_fire_dup_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_fire_dup_0} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSetPosition -win $_nWave2 {("G1" 15)}
wvSetPosition -win $_nWave2 {("G1" 19)}
wvSetPosition -win $_nWave2 {("G1" 19)}
wvAddSignal -win $_nWave2 -clear
wvAddSignal -win $_nWave2 -group {"G1" \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/u_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_read_idx\[8:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s1_pc_dup_0\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/new_entry_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/validEntries\[6:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/ftq/canCommit} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_bits_pc\[49:0\]} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/ftbBanks_0/io_req_valid} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/s0_close_ftb_req} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s0_fire_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/io_s1_ready} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_valid_dup_3} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s0_fire_dup_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/s1_fire_dup_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/allocWriteWay_REG_0} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/allocWriteWay_REG_1} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/allocWriteWay_REG_2} \
{/TOP/SimTop/l_soc/core_with_l2/core/frontend/bpu/predictors/ftb/allocWriteWay_REG_3} \
}
wvAddSignal -win $_nWave2 -group {"G2" \
}
wvSelectSignal -win $_nWave2 {( "G1" 16 17 18 19 )} 
wvSetPosition -win $_nWave2 {("G1" 19)}
