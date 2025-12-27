# ============================================
# Makefile - ARQCP Sprint 3
# USAC11 + USAC14
# ============================================

CC = gcc
AS = as
CFLAGS = -Wall -Wextra -g -Iinclude
ASFLAGS = -g

BUILD_DIR = build
OBJ_DIR = $(BUILD_DIR)/obj
BIN_DIR = $(BUILD_DIR)/bin

# ============================================
# Source files (código partilhado)
# ============================================

# Core C files
CORE_SRC = src/core/config_loader.c \
           src/core/serial_comm.c \
           src/core/light_controller.c

CORE_OBJ = $(OBJ_DIR)/core/config_loader.o \
           $(OBJ_DIR)/core/serial_comm.o \
           $(OBJ_DIR)/core/light_controller.o

# Assembly files (Sprint 2)
ASM_SRC = src/assembly/usac01.s \
          src/assembly/usac02.s \
          src/assembly/usac03.s \
          src/assembly/usac04.s \
          src/assembly/usac05.s \
          src/assembly/usac06.s \
          src/assembly/usac07.s \
          src/assembly/usac08.s \
          src/assembly/usac09.s

ASM_OBJ = $(ASM_SRC:src/assembly/%.s=$(OBJ_DIR)/assembly/%.o)

# ============================================
# Targets principais
# ============================================

all: test_usac11 test_usac14

# ============================================
# USAC11 - Test
# ============================================

test_usac11: dirs $(OBJ_DIR)/core/config_loader.o
	$(CC) $(CFLAGS) tests/test_usac11.c \
		$(OBJ_DIR)/core/config_loader.o \
		-o $(BIN_DIR)/test_usac11
	@echo "✓ USAC11 compilado: $(BIN_DIR)/test_usac11"

# ============================================
# USAC14 - Test
# ============================================

test_usac14: dirs $(CORE_OBJ) $(OBJ_DIR)/assembly/usac04.o
	$(CC) $(CFLAGS) tests/test_usac14.c \
		$(OBJ_DIR)/core/config_loader.o \
		$(OBJ_DIR)/core/serial_comm.o \
		$(OBJ_DIR)/core/light_controller.o \
		$(OBJ_DIR)/assembly/usac04.o \
		-o $(BIN_DIR)/test_usac14
	@echo "✓ USAC14 compilado: $(BIN_DIR)/test_usac14"

# ============================================
# Compilação de object files
# ============================================

# Core C
$(OBJ_DIR)/core/%.o: src/core/%.c
	@mkdir -p $(OBJ_DIR)/core
	$(CC) $(CFLAGS) -c $< -o $@

# Assembly
$(OBJ_DIR)/assembly/%.o: src/assembly/%.s
	@mkdir -p $(OBJ_DIR)/assembly
	$(AS) $(ASFLAGS) $< -o $@

# ============================================
# Diretórios
# ============================================

dirs:
	@mkdir -p $(BIN_DIR)
	@mkdir -p $(OBJ_DIR)/core
	@mkdir -p $(OBJ_DIR)/assembly
	@mkdir -p data/logs

# ============================================
# Limpeza
# ============================================

clean:
	rm -rf $(BUILD_DIR)

cleanall: clean
	rm -f data/logs/*.txt

# ============================================
# Executar testes
# ============================================

run_usac11: test_usac11
	@echo "=== Executando USAC11 ==="
	./$(BIN_DIR)/test_usac11

run_usac14: test_usac14
	@echo "=== Executando USAC14 ==="
	./$(BIN_DIR)/test_usac14

# ============================================
# Info
# ============================================

info:
	@echo "=== Targets disponíveis ==="
	@echo "  make all           - Compilar tudo"
	@echo "  make test_usac11   - Compilar USAC11"
	@echo "  make test_usac14   - Compilar USAC14"
	@echo "  make run_usac11    - Executar USAC11"
	@echo "  make run_usac14    - Executar USAC14"
	@echo "  make clean         - Limpar build"
	@echo "  make info          - Esta mensagem"

.PHONY: all dirs clean cleanall run_usac11 run_usac14 info