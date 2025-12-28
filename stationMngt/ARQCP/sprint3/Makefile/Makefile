# ============================================
# Makefile - ARQCP Sprint 3
# USAC10 + USAC11 + USAC12 + USAC14
# ============================================

CC = gcc
AS = as
CFLAGS = -Wall -Wextra -g -Iinclude
ASFLAGS = -g

BUILD_DIR = build
OBJ_DIR = $(BUILD_DIR)/obj
BIN_DIR = $(BUILD_DIR)/bin

# ============================================
# Source files
# ============================================

# Core C files
# Adicionei log_manager.c aqui para a USAC12
CORE_SRC = src/core/config_loader.c \
           src/core/serial_comm.c \
           src/core/light_controller.c \
           src/core/log_manager.c

CORE_OBJ = $(OBJ_DIR)/core/config_loader.o \
           $(OBJ_DIR)/core/serial_comm.o \
           $(OBJ_DIR)/core/light_controller.o \
           $(OBJ_DIR)/core/log_manager.o

# Assembly files (Sprint 2)
# Nota: O Makefile gera a lista de objectos, mas nos targets
# abaixo ligamos apenas o necessário para cada teste.
ASM_SRC = src/assembly/usac01.s \
          src/assembly/usac02.s \
          src/assembly/usac03.s \
          src/assembly/usac04.s \
          src/assembly/usac05.s \
          src/assembly/usac06.s \
          src/assembly/usac07.s \
          src/assembly/usac08.s \
          src/assembly/usac09.s

# ============================================
# Targets principais
# ============================================

all: test_usac10 test_usac11 test_usac12 test_usac14

# ============================================
# USAC10 - Test (Protocolo Hardware)
# ============================================

# Depende do usac03 (extract_data) e usac04 (format_command)
test_usac10: dirs $(OBJ_DIR)/assembly/usac03.o $(OBJ_DIR)/assembly/usac04.o
	$(CC) $(CFLAGS) tests/test_usac10.c \
	   $(OBJ_DIR)/assembly/usac03.o \
	   $(OBJ_DIR)/assembly/usac04.o \
	   -o $(BIN_DIR)/test_usac10
	@echo "✓ USAC10 compilado: $(BIN_DIR)/test_usac10"

# ============================================
# USAC11 - Test (Config Loader)
# ============================================

test_usac11: dirs $(OBJ_DIR)/core/config_loader.o
	$(CC) $(CFLAGS) tests/test_usac11.c \
	   $(OBJ_DIR)/core/config_loader.o \
	   -o $(BIN_DIR)/test_usac11
	@echo "✓ USAC11 compilado: $(BIN_DIR)/test_usac11"

# ============================================
# USAC12 - Test (Log Export)
# ============================================

# Depende de todos os objetos CORE (incluindo log_manager.o)
test_usac12: dirs $(CORE_OBJ)
	$(CC) $(CFLAGS) tests/test_usac12.c \
	   $(CORE_OBJ) \
	   -o $(BIN_DIR)/test_usac12
	@echo "✓ USAC12 compilado: $(BIN_DIR)/test_usac12"

# ============================================
# USAC14 - Test (Light Controller)
# ============================================

test_usac14: dirs $(CORE_OBJ) $(OBJ_DIR)/assembly/usac04.o
	$(CC) $(CFLAGS) tests/test_usac14.c \
	   $(CORE_OBJ) \
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

run_usac10: test_usac10
	@echo "=== Executando USAC10 ==="
	./$(BIN_DIR)/test_usac10

run_usac11: test_usac11
	@echo "=== Executando USAC11 ==="
	./$(BIN_DIR)/test_usac11

run_usac12: test_usac12
	@echo "=== Executando USAC12 ==="
	./$(BIN_DIR)/test_usac12

run_usac14: test_usac14
	@echo "=== Executando USAC14 ==="
	./$(BIN_DIR)/test_usac14

# ============================================
# Info
# ============================================

info:
	@echo "=== Targets disponíveis ==="
	@echo "  make all           - Compilar tudo"
	@echo "  make test_usac10   - Compilar USAC10 (Protocolo HW)"
	@echo "  make test_usac11   - Compilar USAC11 (Config Loader)"
	@echo "  make test_usac12   - Compilar USAC12 (Log Export)"
	@echo "  make test_usac14   - Compilar USAC14 (Lights Controller)"
	@echo "  make run_usacXX    - Executar teste XX"
	@echo "  make clean         - Limpar build"