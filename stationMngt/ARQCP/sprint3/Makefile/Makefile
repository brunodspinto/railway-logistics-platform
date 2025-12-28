# ============================================
# Makefile - ARQCP Sprint 3
# USAC10 + USAC11 + USAC12 + USAC13 + USAC14
# Arquitetura: RV64IMAFDC
# ============================================

CC = riscv64-linux-gnu-gcc
AS = riscv64-linux-gnu-as
QEMU = qemu-riscv64-static

CFLAGS = -Wall -Wextra -g -Iinclude -march=rv64imafdc -mabi=lp64d -static
ASFLAGS = -march=rv64imafdc -mabi=lp64d

BUILD_DIR = build
OBJ_DIR = $(BUILD_DIR)/obj
BIN_DIR = $(BUILD_DIR)/bin

# ============================================
# Source files
# ============================================

# Core C files (TODOS - incluindo log_manager)
CORE_OBJ = $(OBJ_DIR)/core/config_loader.o \
           $(OBJ_DIR)/core/serial_comm.o \
           $(OBJ_DIR)/core/light_controller.o \
           $(OBJ_DIR)/core/log_manager.o

# Assembly files
ASM_USAC03 = $(OBJ_DIR)/assembly/usac03.o
ASM_USAC04 = $(OBJ_DIR)/assembly/usac04.o

# ============================================
# Targets principais
# ============================================

all: info test_usac10 test_usac11 test_usac12 test_usac13 test_usac14

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

test_usac12: dirs $(OBJ_DIR)/core/config_loader.o $(OBJ_DIR)/core/log_manager.o
	$(CC) $(CFLAGS) tests/test_usac12.c \
		$(OBJ_DIR)/core/config_loader.o \
		$(OBJ_DIR)/core/log_manager.o \
		-o $(BIN_DIR)/test_usac12
	@echo "✓ USAC12 compilado (RV64)"

# ============================================
# USAC13 - Test (Read Sensors)
# ============================================

test_usac13: dirs $(OBJ_DIR)/sensors_manager.o $(ASM_USAC03)
	$(CC) $(CFLAGS) tests/test_usac13.c \
		$(OBJ_DIR)/sensors_manager.o \
		$(ASM_USAC03) \
		-o $(BIN_DIR)/test_usac13
	@echo "✓ USAC13 compilado (RV64)"

# ============================================
# USAC14 - Test (Light Controller)
# ============================================

test_usac14: dirs $(CORE_OBJ) $(ASM_USAC04)
	$(CC) $(CFLAGS) tests/test_usac14.c \
		$(CORE_OBJ) \
		$(ASM_USAC04) \
		-o $(BIN_DIR)/test_usac14
	@echo "✓ USAC14 compilado (RV64 + Assembly)"

# ============================================
# USAC14 Hardware - Test
# ============================================

test_usac14_hw: dirs $(CORE_OBJ) $(ASM_USAC04)
	$(CC) $(CFLAGS) tests/test_usac14_hardware.c \
		$(CORE_OBJ) \
		$(ASM_USAC04) \
		-o $(BIN_DIR)/test_usac14_hw
	@echo "✓ USAC14 Hardware compilado (RV64)"

# ============================================
# Compilação de object files
# ============================================

$(OBJ_DIR)/core/%.o: src/core/%.c
	@mkdir -p $(OBJ_DIR)/core
	$(CC) $(CFLAGS) -c $< -o $@

$(OBJ_DIR)/assembly/%.o: src/assembly/%.s
	@mkdir -p $(OBJ_DIR)/assembly
	$(AS) $(ASFLAGS) $< -o $@

# Sensor Manager (para USAC13)
$(OBJ_DIR)/sensors_manager.o: src/sensors_manager.c
	@mkdir -p $(OBJ_DIR)
	$(CC) $(CFLAGS) -c $< -o $@

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

# ============================================
# Info
# ============================================

info:
	@echo "=== Targets ==="
	@echo " make all           → Compilar tudo"
	@echo " make test_usac10   → Compilar USAC10"
	@echo " make test_usac11   → Compilar USAC11"
	@echo " make test_usac12   → Compilar USAC12"
	@echo " make test_usac13   → Compilar USAC13"
	@echo " make test_usac14   → Compilar USAC14"
	@echo " make test_usac14_hw→ Compilar USAC14 (hardware)"
	@echo " make run_usacXX    → Executar teste"
	@echo " make clean         → Limpar build"
	@echo " make cleanall      → Limpar build + logs"

.PHONY: all dirs clean cleanall info \
        test_usac10 test_usac11 test_usac12 test_usac13 test_usac14 test_usac14_hw \
        run_usac10 run_usac11 run_usac12 run_usac13 run_usac14 run_usac14_hw