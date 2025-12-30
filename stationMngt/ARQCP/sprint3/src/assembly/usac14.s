# ============================================
# USAC14 - generate_command_from_track
#
# Função Assembly que recebe struct Track*
# e gera o comando de controle de LEDs
#
# Arquitetura: RV64IMAFDC
# Convenção: RISC-V LP64D
# ============================================
#
# int generate_command_from_track(Track* track, char* cmd)
#
# PARÂMETROS:
#   a0 = Track* track (ponteiro para struct)
#   a1 = char* cmd (buffer de saída, min 20 bytes)
#
# RETORNO:
#   a0 = 1 (sucesso) ou 0 (erro)
#
# STRUCT TRACK (offsets em bytes):
#   +0  = id (int, 4 bytes)
#   +4  = state (enum, 4 bytes)
#   +8  = assigned_train_id (int, 4 bytes)
#
# ESTADOS (TrackState enum):
#   0 = TRACK_FREE        → comando "GE"
#   1 = TRACK_ASSIGNED    → comando "YE"
#   2 = TRACK_BUSY        → comando "RE"
#   3 = TRACK_INOPERATIVE → comando "RB"
# ============================================

.section .data

# Strings de comandos para cada estado
cmd_ge: .string "GE"
cmd_ye: .string "YE"
cmd_re: .string "RE"
cmd_rb: .string "RB"

.section .text
.global generate_command_from_track

# ============================================
# Declaração externa da função USAC04
# ============================================
.extern format_command

# ============================================
# FUNÇÃO PRINCIPAL
# ============================================

generate_command_from_track:
    # Prólogo - Salvar registos na pilha
    addi sp, sp, -32
    sd ra, 24(sp)        # Return address
    sd s0, 16(sp)        # s0 = Track*
    sd s1, 8(sp)         # s1 = cmd buffer
    sd s2, 0(sp)         # s2 = uso geral

    # ========================================
    # 1. Guardar argumentos
    # ========================================
    mv s0, a0            # s0 = Track* track
    mv s1, a1            # s1 = char* cmd

    # ========================================
    # 2. Validar ponteiros (NULL check)
    # ========================================
    beqz s0, error       # Se track == NULL → erro
    beqz s1, error       # Se cmd == NULL → erro

    # ========================================
    # 3. LER track->state (offset +4)
    # ========================================
    lw t0, 4(s0)         # t0 = track->state (4 bytes no offset +4)

    # ========================================
    # 4. Validar estado (0-3)
    # ========================================
    li t1, 0
    blt t0, t1, error    # Se state < 0 → erro

    li t1, 4
    bge t0, t1, error    # Se state >= 4 → erro

    # ========================================
    # 5. SWITCH baseado no estado
    # ========================================

    # Estado 0: TRACK_FREE → "GE"
    beqz t0, state_free

    # Estado 1: TRACK_ASSIGNED → "YE"
    li t1, 1
    beq t0, t1, state_assigned

    # Estado 2: TRACK_BUSY → "RE"
    li t1, 2
    beq t0, t1, state_busy

    # Estado 3: TRACK_INOPERATIVE → "RB"
    li t1, 3
    beq t0, t1, state_inop

    # Caso impossível (já validado)
    j error

# ========================================
# HANDLERS DE CADA ESTADO
# ========================================

state_free:
    la a0, cmd_ge        # a0 = "GE"
    j call_format

state_assigned:
    la a0, cmd_ye        # a0 = "YE"
    j call_format

state_busy:
    la a0, cmd_re        # a0 = "RE"
    j call_format

state_inop:
    la a0, cmd_rb        # a0 = "RB"
    j call_format

# ========================================
# CHAMAR format_command (USAC04)
# ========================================

call_format:
    # a0 já contém o comando (GE, YE, RE ou RB)

    # ========================================
    # 6. LER track->id (offset +0)
    # ========================================
    lw a1, 0(s0)         # a1 = track->id (4 bytes no offset +0)

    # ========================================
    # 7. Preparar buffer de saída
    # ========================================
    mv a2, s1            # a2 = cmd buffer (destino)

    # ========================================
    # 8. CHAMAR format_command(op, id, cmd)
    # ========================================
    call format_command

    # ========================================
    # 9. Verificar retorno de format_command
    # ========================================
    beqz a0, error       # Se retornou 0 → erro

    # Sucesso!
    li a0, 1             # Retornar 1 (sucesso)
    j end

# ========================================
# HANDLER DE ERRO
# ========================================

error:
    li a0, 0             # Retornar 0 (erro)
    j end

# ========================================
# EPÍLOGO - Restaurar e retornar
# ========================================

end:
    # Restaurar registos salvos
    ld ra, 24(sp)
    ld s0, 16(sp)
    ld s1, 8(sp)
    ld s2, 0(sp)
    addi sp, sp, 32

    # Retornar ao chamador
    ret

# ============================================
# FIM DA FUNÇÃO
# ============================================