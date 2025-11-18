CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: test_format_command.o format_command.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) test_format_command.o format_command.o -o program.elf

test_format_command.o: test_format_command.c format_command.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c test_format_command.c

format_command.o: format_command.s format_command.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c format_command.s

demo.elf: main.o format_command.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o format_command.o -o demo.elf

main.o: main.c format_command.h
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
break format_command
continue
endef
export GDBINIT

.gdbinit:
	@echo "$$GDBINIT" >.gdbinit

.PHONY: clean run run-demo debug