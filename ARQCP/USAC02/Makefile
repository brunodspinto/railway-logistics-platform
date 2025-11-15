# Compilador e emulador
CC = riscv64-linux-gnu-gcc
CFLAGS = -Wall -O0
EMU = qemu-riscv64

# Ficheiros
OBJS = main.o decrypt_data.o
TARGET = decrypt_test

# Regras principais
all: $(TARGET)

$(TARGET): $(OBJS)
        $(CC) $(CFLAGS) -static $(OBJS) -o $(TARGET)

main.o: main.c decrypt_data.h
        $(CC) $(CFLAGS) -c main.c -o main.o

decrypt_data.o: decrypt_data.s
        $(CC) $(CFLAGS) -c decrypt_data.s -o decrypt_data.o

run: $(TARGET)
        $(EMU) ./$(TARGET)

clean:
        rm -f $(OBJS) $(TARGET)