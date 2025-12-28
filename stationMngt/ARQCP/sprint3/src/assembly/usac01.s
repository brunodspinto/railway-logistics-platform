.text
	.globl encrypt_data

# encrypt_data(char* src, int shift, char* dest)

encrypt_data:
	addi sp, sp, -32          # reservar espaço na stack
	sd ra, 24(sp)             # <--- ALTERADO: sw -> sd (Store Double 64-bit)
	sd s0, 16(sp)             # <--- ALTERADO: sw -> sd
	sd s1, 8(sp)              # <--- ALTERADO: sw -> sd
	sd s2, 0(sp)              # <--- ALTERADO: sw -> sd

	mv s0, a0                 # s0 = src
	mv s1, a1                 # s1 = shift
	mv s2, a2                 # s2 = dest

	li t0, 1                  # mínimo shift = 1
	blt s1, t0, fail          # se shift < 1 → erro
	li t0, 26                 # máximo shift = 26
	bgt s1, t0, fail          # se shift > 26 → erro

loop:
	lbu t1, 0(s0)             # carregar char atual
	beqz t1, end              # se '\0', termina

	li t2, 'A'
	blt t1, t2, fail          # char fora do intervalo
	li t3, 'Z'
	bgt t1, t3, fail          # char fora do intervalo

	sub t4, t1, t2            # converter A–Z para 0–25
	add t4, t4, s1            # aplicar shift
	li t5, 26
	remu t4, t4, t5           # módulo 26
	add t4, t4, t2            # voltar ao intervalo ASCII
	sb t4, 0(s2)              # guardar char cifrado

	addi s0, s0, 1            # avançar src
	addi s2, s2, 1            # avançar dest
	j loop

end:
	sb zero, 0(s2)            # terminar string destino
	li a0, 1                  # return 1 (sucesso)
	j restore

fail:
	sb zero, 0(s2)            # string destino vazia
	li a0, 0                  # return 0 (erro)

restore:
	ld ra, 24(sp)             # <--- ALTERADO: lw -> ld (Load Double 64-bit)
	ld s0, 16(sp)             # <--- ALTERADO: lw -> ld
	ld s1, 8(sp)              # <--- ALTERADO: lw -> ld
	ld s2, 0(sp)              # <--- ALTERADO: lw -> ld
	addi sp, sp, 32
	jr ra