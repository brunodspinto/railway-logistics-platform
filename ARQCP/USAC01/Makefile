# Compilador e emulador
CC = riscv64-linux-gnu-gcc
CFLAGS = -Wall -O0
EMU = qemu-riscv64

# Ficheiros
OBJS = main.o encrypt_data.o
TARGET = encrypt_test

# Regras principais
all: $(TARGET)

$(TARGET): $(OBJS)
        $(CC) $(CFLAGS) -static $(OBJS) -o $(TARGET)

main.o: main.c encrypt_data.h
        $(CC) $(CFLAGS) -c main.c -o main.o

encrypt_data.o: encrypt_data.s
        $(CC) $(CFLAGS) -c encrypt_data.s -o encrypt_data.o

run: $(TARGET)
        $(EMU) ./$(TARGET)

clean:
        rm -f $(OBJS) $(TARGET)