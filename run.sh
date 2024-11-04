./build/emu -i ./ready-to-run/coremark-2-iteration.bin  --diff ./ready-to-run/riscv64-spike-so 2>&1 | tee > coremark-2-test.log
# ./build/emu -i ./ready-to-run/linux.bin --diff ./ready-to-run/riscv64-spike-so 2>&1 | tee > linux-test.log

