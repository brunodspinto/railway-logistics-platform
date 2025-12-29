	.text
	.globl decrypt_data

# decrypt_data(char* src, int shift, char* dest)

decrypt_data:
	addi sp, sp, -32          # reservar stack
	sw ra, 24(sp)             # guardar ra
	sw s0, 16(sp)             # guardar s0
	sw s1, 8(sp)              # guardar s1
	sw s2, 0(sp)              # guardar s2

	mv s0, a0                 # s0 = src
	mv s1, a1                 # s1 = shift
	mv s2, a2                 # s2 = dest

	li t0, 1
	blt s1, t0, fail          # shift < 1 → erro
	li t0, 26
	bgt s1, t0, fail          # shift > 26 → erro

	li t6, 26                 # constante 26
	sub t5, t6, s1            # t5 = 26 - shift (shift inverso)

loop:
	lbu t1, 0(s0)             # char atual
	beqz t1, end              # se '\0', termina

	li t2, 'A'
	blt t1, t2, fail          # inválido se < 'A'
	li t3, 'Z'
	bgt t1, t3, fail          # inválido se > 'Z'

	sub t4, t1, t2            # normalizar A–Z → 0–25
	add t4, t4, t5            # aplicar shift inverso
	remu t4, t4, t6           # módulo 26
	add t4, t4, t2            # voltar ao intervalo ASCII
	sb t4, 0(s2)              # guardar descifrado

	addi s0, s0, 1            # avançar src
	addi s2, s2, 1            # avançar dest
	j loop

end:
	sb zero, 0(s2)            # terminar string
	li a0, 1                  # return 1 (sucesso)
	j restore

fail:
	sb zero, 0(s2)            # string destino vazia
	li a0, 0                  # return 0

restore:
	lw ra, 24(sp)
	lw s0, 16(sp)
	lw s1, 8(sp)
	lw s2, 0(sp)
	addi sp, sp, 32
	jr ra