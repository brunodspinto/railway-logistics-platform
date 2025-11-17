    .section .text
    .global move_n_to_array

move_n_to_array:
    lw t0, 0(a2)        #t0 = valor atual de *nelem
    blt t0, a5, erro    #Se *nelem < n, salta para erro (retorna 0)

    lw t1, 0(a3)    #t1 = valor atual de *tail (índice)
    li t2, 0        #t2 = contador do loop (i = 0)

loop:
    bge t2, a5, fim_loop    #Se i >= n, termina o loop

    slli t3, t1, 2      #t3 = tail * 4 (shift left logical immediate)
    add t3, a0, t3      #t3 = endereço absoluto do elemento
    lw t4, 0(t3)        #t4 = buffer[tail] (carrega o valor)

    sw t4, 0(a6)        #array[atual] = valor
    addi a6, a6, 4      #Incrementa o ponteiro do array de destino (próxima posição)

    addi t1, t1, 1          #tail++
    bne t1, a1, increment   #Se tail != length, continua
    li t1, 0                #Se tail == length, volta a 0 (wrap around)

increment:
    addi t2, t2, 1      #i++
    j loop              #Volta ao início do loop

fim_loop:
    sw t1, 0(a3)        #Atualiza o valor de *tail na memória

    sub t0, t0, a5      #Novo nelem = nelem_antigo - n
    sw t0, 0(a2)        #Atualiza o valor de *nelem na memória

    li a0, 1            #Return 1 (Sucesso)
    ret

erro:
    li a0, 0            #Return 0 (Falha)
    ret