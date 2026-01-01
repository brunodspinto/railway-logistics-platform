# ============================================
# Makefile - ARQCP Sprint 3
# USAC10 + USAC11 + USAC12 + USAC13 + USAC14 + USAC15
# Arquitetura: RV64IMAFDC
# ============================================

CC = riscv64-linux-gnu-gcc
AS = riscv64-linux-gnu-as
QEMU = qemu-riscv64-static

# CORRIGIDO: Adicionado -Isrc/assembly para encontrar headers assembly
CFLAGS = -Wall -Wextra -g -Iinclude -Isrc/core -Isrc/assembly -march=rv64imafdc -mabi=lp64d -static
ASFLAGS = -march=rv64imafdc -mabi=lp64d

BUILD_DIR = build
OBJ_DIR = $(BUILD_DIR)/obj
BIN_DIR = $(BUILD_DIR)/bin

# ============================================
# Source files
# ============================================

# Core C files
CORE_OBJ = $(OBJ_DIR)/core/config_loader.o \
           $(OBJ_DIR)/core/serial_comm.o \
           $(OBJ_DIR)/core/light_controller.o \
           $(OBJ_DIR)/core/log_manager.o \
           $(OBJ_DIR)/core/sensors_manager.o \
           $(OBJ_DIR)/core/board.o \
           $(OBJ_DIR)/core/track_manager.o

# Assembly files (ADICIONADO USAC14!)
ASM_USAC01 = $(OBJ_DIR)/assembly/usac01.o
ASM_USAC03 = $(OBJ_DIR)/assembly/usac03.o
ASM_USAC04 = $(OBJ_DIR)/assembly/usac04.o
ASM_USAC14 = $(OBJ_DIR)/assembly/usac14.o

# ============================================
# Targets principais
# ============================================

all: info test_usac10 test_usac11 test_usac12 test_usac13 test_usac14 test_usac15

# ============================================
# USAC10 - Test (Protocolo Hardware)
# ============================================

test_usac10: dirs $(ASM_USAC03) $(ASM_USAC04)
	$(CC) $(CFLAGS) tests/test_usac10.c \
	   $(ASM_USAC03) \
	   $(ASM_USAC04) \
	   -o $(BIN_DIR)/test_usac10
	@echo "✓ USAC10 compilado (RV64)"

# ============================================
# USAC11 - Test (Config Loader)
# ============================================

test_usac11: dirs $(OBJ_DIR)/core/config_loader.o
	$(CC) $(CFLAGS) tests/test_usac11.c \
	   $(OBJ_DIR)/core/config_loader.o \
	   -o $(BIN_DIR)/test_usac11
	@echo "✓ USAC11 compilado (RV64)"

# ============================================
# USAC12 - Test (Log Export)
# ============================================

test_usac12: dirs $(OBJ_DIR)/core/config_loader.o $(OBJ_DIR)/core/log_manager.o $(ASM_USAC01)
	$(CC) $(CFLAGS) tests/test_usac12.c \
	   $(OBJ_DIR)/core/config_loader.o \
	   $(OBJ_DIR)/core/log_manager.o \
	   $(ASM_USAC01) \
	   -o $(BIN_DIR)/test_usac12
	@echo "✓ USAC12 compilado (RV64)"

# ============================================
# USAC13 - Test (Read Sensors)
# ============================================

test_usac13: dirs $(OBJ_DIR)/core/sensors_manager.o $(ASM_USAC03)
	$(CC) $(CFLAGS) tests/test_usac13.c \
	   $(OBJ_DIR)/core/sensors_manager.o \
	   $(ASM_USAC03) \
	   -o $(BIN_DIR)/test_usac13
	@echo "✓ USAC13 compilado (RV64)"

# ============================================
# USAC14 - Test (Light Controller)
# ⭐ ATUALIZADO: Adicionado ASM_USAC14!
# ============================================

test_usac14: dirs $(CORE_OBJ) $(ASM_USAC04) $(ASM_USAC14) $(ASM_USAC01)
	$(CC) $(CFLAGS) tests/test_usac14.c \
	   $(CORE_OBJ) \
	   $(ASM_USAC04) \
	   $(ASM_USAC14) \
	   $(ASM_USAC01) \
	   -o $(BIN_DIR)/test_usac14
	@echo "✓ USAC14 compilado (RV64 + Assembly com Struct)"

# ============================================
# USAC14 Hardware - Test
# ⭐ ATUALIZADO: Adicionado ASM_USAC14!
# ============================================

test_usac14_hw: dirs $(CORE_OBJ) $(ASM_USAC04) $(ASM_USAC14) $(ASM_USAC01)
	$(CC) $(CFLAGS) tests/test_usac14_hardware.c \
	   $(CORE_OBJ) \
	   $(ASM_USAC04) \
	   $(ASM_USAC14) \
	   $(ASM_USAC01) \
	   -o $(BIN_DIR)/test_usac14_hw
	@echo "✓ USAC14 Hardware compilado (RV64 + Assembly com Struct)"

# ============================================
# USAC15 - Test (Synoptic Board)
# ============================================

test_usac15: dirs $(OBJ_DIR)/core/board.o
	$(CC) $(CFLAGS) tests/test_usac15.c \
	   $(OBJ_DIR)/core/board.o \
	   -o $(BIN_DIR)/test_usac15
	@echo "✓ USAC15 compilado (RV64)"

# ============================================
# USAC16 - Test (Track Manager Logic)
# ============================================

test_usac16: dirs $(CORE_OBJ) $(ASM_USAC04) $(ASM_USAC14)
	$(CC) $(CFLAGS) tests/test_usac16.c \
	   $(CORE_OBJ) \
	   $(ASM_USAC04) \
	   $(ASM_USAC14) \
	   -o $(BIN_DIR)/test_usac16
	@echo "✓ USAC16 compilado"

# ============================================
# APP PRINCIPAL (Station Controller Integrado)
# ============================================

app: dirs $(CORE_OBJ) $(ASM_USAC01) $(ASM_USAC03) $(ASM_USAC04) $(ASM_USAC14)
	$(CC) $(CFLAGS) src/core/main.c \
	   $(CORE_OBJ) \
	   $(ASM_USAC01) \
	   $(ASM_USAC03) \
	   $(ASM_USAC04) \
	   $(ASM_USAC14) \
	   -o $(BIN_DIR)/station_app
	@echo "✓ Station App (Menu Integrado) compilada com sucesso!"

run_app: app
	$(QEMU) -L /usr/riscv64-linux-gnu ./$(BIN_DIR)/station_app

# ============================================
# Compilação de object files
# ============================================

$(OBJ_DIR)/core/%.o: src/core/%.c
	@mkdir -p $(OBJ_DIR)/core
	$(CC) $(CFLAGS) -c $< -o $@

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
	@mkdir -p config
	@mkdir -p data/logs

# ============================================
# Limpeza
# ============================================

clean:
	rm -rf $(BUILD_DIR)

cleanall: clean
	rm -f data/logs/*.txt
	rm -f config/*.bak

# ============================================
# Executar testes
# ============================================

run_usac10: test_usac10
	$(QEMU) -L /usr/riscv64-linux-gnu ./$(BIN_DIR)/test_usac10

run_usac11: test_usac11
	$(QEMU) -L /usr/riscv64-linux-gnu ./$(BIN_DIR)/test_usac11

run_usac12: test_usac12
	$(QEMU) -L /usr/riscv64-linux-gnu ./$(BIN_DIR)/test_usac12

run_usac13: test_usac13
	$(QEMU) -L /usr/riscv64-linux-gnu ./$(BIN_DIR)/test_usac13

run_usac14: test_usac14
	$(QEMU) -L /usr/riscv64-linux-gnu ./$(BIN_DIR)/test_usac14

run_usac14_hw: test_usac14_hw
	@echo "⚠️  Arduino deve estar ligado!"
	$(QEMU) -L /usr/riscv64-linux-gnu ./$(BIN_DIR)/test_usac14_hw

run_usac15: test_usac15
	$(QEMU) -L /usr/riscv64-linux-gnu ./$(BIN_DIR)/test_usac15

run_usac16: test_usac16
	$(QEMU) -L /usr/riscv64-linux-gnu ./$(BIN_DIR)/test_usac16

# ============================================
# Info
# ============================================

info:
	@echo "=== ARQCP Sprint 3 - Makefile ==="
	@echo ""
	@echo "Targets disponíveis:"
	@echo " make app           → Compilar Aplicação Principal"
	@echo " make all           → Compilar tudo"
	@echo " make test_usac10   → Compilar USAC10"
	@echo " make test_usac11   → Compilar USAC11"
	@echo " make test_usac12   → Compilar USAC12"
	@echo " make test_usac13   → Compilar USAC13"
	@echo " make test_usac14   → Compilar USAC14 (com assembly struct)"
	@echo " make test_usac14_hw→ Compilar USAC14 Hardware"
	@echo " make test_usac15   → Compilar USAC15"
	@echo " make test_usac16   → Compilar USAC16"
	@echo ""
	@echo " make run_app       → Executar Aplicação Principal"
	@echo " make run_usac14    → Executar USAC14 (mock)"
	@echo " make run_usac14_hw → Executar USAC14 (hardware)"
	@echo ""
	@echo " make clean         → Limpar build"
	@echo " make cleanall      → Limpar tudo + logs"

.PHONY: all dirs clean cleanall info \
        test_usac10 test_usac11 test_usac12 test_usac13 test_usac14 test_usac14_hw test_usac15 test_usac16\
        run_usac10 run_usac11 run_usac12 run_usac13 run_usac14 run_usac14_hw run_usac15 run_usac16 \
        app run_app