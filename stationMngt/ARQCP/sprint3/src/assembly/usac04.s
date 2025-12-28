.section .data
    # Comandos válidos (2 chars)
    cmd_re: .string "RE"
    cmd_rb: .string "RB"
    cmd_ye: .string "YE"
    cmd_ge: .string "GE"
    # Comando especial (3 chars)
    cmd_gth: .string "GTH"
    comma: .string ","

.section .text
.global format_command

# ========================================
# int format_command(char* op, int n, char* cmd)
# ========================================

format_command:
    # Prólogo (64-bit stack frame)
    addi sp, sp, -64
    sd ra, 56(sp)
    sd s0, 48(sp)
    sd s1, 40(sp)
    sd s2, 32(sp)
    sd s3, 24(sp)
    sd s4, 16(sp)

    # Guardar argumentos
    mv s0, a0          # s0 = op
    mv s1, a1          # s1 = n
    mv s2, a2          # s2 = cmd

    # Validar ponteiros NULL
    beqz s0, error
    beqz s2, error

    # Trim e uppercase de op (usar stack buffer)
    addi a0, sp, 8     # buffer temporário no stack
    mv a1, s0          # op original
    call trim_and_uppercase

    # Calcular comprimento da string processada
    addi a0, sp, 8
    call string_length
    mv s3, a0          # s3 = comprimento

    # ========== Verificar GTH (3 caracteres) ==========
    li t0, 3
    bne s3, t0, check_2char

    # Comparar com "GTH"
    addi a0, sp, 8
    la a1, cmd_gth
    call string_compare
    bnez a0, not_gth   # se diferente, não é GTH

    # É GTH - copiar para output
    mv a0, s2
    la a1, cmd_gth
    call string_copy
    li a0, 1           # sucesso
    j end

not_gth:
    j error

    # ========== Verificar comandos de 2 caracteres ==========
check_2char:
    li t0, 2
    bne s3, t0, error  # se não tem 2 chars, erro

    # Comparar com RE, RB, YE, GE
    addi a0, sp, 8
    la a1, cmd_re
    call string_compare
    beqz a0, valid_cmd

    addi a0, sp, 8
    la a1, cmd_rb
    call string_compare
    beqz a0, valid_cmd

    addi a0, sp, 8
    la a1, cmd_ye
    call string_compare
    beqz a0, valid_cmd

    addi a0, sp, 8
    la a1, cmd_ge
    call string_compare
    beqz a0, valid_cmd

    # Nenhum comando válido
    j error

valid_cmd:
    # Validar n (0-99)
    bltz s1, error     # n < 0?
    li t0, 99
    bgt s1, t0, error  # n > 99?

    # Copiar comando (2 chars) para output
    mv a0, s2
    addi a1, sp, 8
    call string_copy

    # Adicionar vírgula
    mv a0, s2
    call string_length
    add a0, s2, a0     # posição após comando

    li t0, ','         # Carregar vírgula
    sb t0, 0(a0)       # Escrever vírgula
    addi a0, a0, 1     # Avançar ponteiro

    # --- CORREÇÃO AQUI: Adicionar ESPAÇO ---
    li t0, ' '         # Carregar espaço
    sb t0, 0(a0)       # Escrever espaço
    addi a0, a0, 1     # Avançar ponteiro
    # ---------------------------------------

    # Converter n para 2 dígitos ASCII
    mv a1, s1
    call int_to_2digits

    li a0, 1           # sucesso
    j end

error:
    # Retornar string vazia em cmd se ponteiro for válido
    beqz s2, skip_clear
    sb zero, 0(s2)
skip_clear:
    li a0, 0           # erro

end:
    # Epílogo
    ld ra, 56(sp)
    ld s0, 48(sp)
    ld s1, 40(sp)
    ld s2, 32(sp)
    ld s3, 24(sp)
    ld s4, 16(sp)
    addi sp, sp, 64
    ret

# ========================================
# Funções Auxiliares (Iguais ao que tinhas)
# ========================================

trim_and_uppercase:
    mv t0, a0          # dest
    mv t1, a1          # src
    li t2, 0           # índice dest
trim_loop:
    lb t3, 0(t1)
    beqz t3, trim_end
    li t4, ' '
    beq t3, t4, trim_skip
    li t4, 'a'
    blt t3, t4, not_lower
    li t4, 'z'
    bgt t3, t4, not_lower
    addi t3, t3, -32   # 'a' - 'A' = 32
not_lower:
    add t5, t0, t2
    sb t3, 0(t5)
    addi t2, t2, 1
trim_skip:
    addi t1, t1, 1
    j trim_loop
trim_end:
    add t5, t0, t2
    sb zero, 0(t5)
    ret

string_length:
    li t0, 0
strlen_loop:
    lb t1, 0(a0)
    beqz t1, strlen_end
    addi t0, t0, 1
    addi a0, a0, 1
    j strlen_loop
strlen_end:
    mv a0, t0
    ret

string_compare:
    mv t0, a0
    mv t1, a1
strcmp_loop:
    lb t2, 0(t0)
    lb t3, 0(t1)
    bne t2, t3, strcmp_diff
    beqz t2, strcmp_equal
    addi t0, t0, 1
    addi t1, t1, 1
    j strcmp_loop
strcmp_diff:
    sub a0, t2, t3
    ret
strcmp_equal:
    li a0, 0
    ret

string_copy:
    mv t0, a0
    mv t1, a1
strcpy_loop:
    lb t2, 0(t1)
    sb t2, 0(t0)
    beqz t2, strcpy_end
    addi t0, t0, 1
    addi t1, t1, 1
    j strcpy_loop
strcpy_end:
    ret

int_to_2digits:
    mv t0, a0          # dest
    mv t1, a1          # n
    li t2, 10
    div t3, t1, t2
    addi t3, t3, '0'
    sb t3, 0(t0)
    rem t3, t1, t2
    addi t3, t3, '0'
    sb t3, 1(t0)
    sb zero, 2(t0)
    ret