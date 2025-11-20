CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: test_extract_data.o extract_data.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) test_extract_data.o extract_data.o -o program.elf

test_extract_data.o: test_extract_data.c extract_data.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c test_extract_data.c

extract_data.o: extract_data.s extract_data.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c extract_data.s

demo.elf: main.o extract_data.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o extract_data.o -o demo.elf

main.o: main.c extract_data.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

clean:
	rm -f *.o *.elf .gdbinit

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf

run-demo: demo.elf
	qemu-riscv32 -L $(LIBRV32) ./demo.elf

debug: program.elf .gdbinit
	@echo "On other terminal window do:"
	@echo "riscv32-linux-gdb --tui ./program.elf"
	@echo "or just paste"
	@echo -n "cd $(CURDIR) ; riscv32-linux-gdb --tui ./program.elf" \
		| xclip -selection clipboard
	qemu-riscv32 -L $(LIBRV32) -g 1234 program.elf -S

define GDBINIT
target extended-remote localhost:1234
set sysroot ~/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot
break extract_data
continue
endef
export GDBINIT

.gdbinit:
	@echo "$$GDBINIT" >.gdbinit

.PHONY: clean run run-demo debug