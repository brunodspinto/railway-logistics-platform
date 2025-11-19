    .section .text
    .global median
median:
    blez a1, error          #Se a1 <= 0, salta para erro

    addi a1, a1, -1         #a1 passa a ser (length - 1)
    blez a1, after_sort     #Se o array tiver apenas 1 elemento (length - 1 = 0), já está ordenado

    li t0, 0                #t0 = i (contador do ciclo externo)

outer_loop:
    bge t0, a1, after_sort      #Se i >= length - 1, termina com sucesso

    li t1, 0                    #t1 = j (contador do ciclo interno)
    sub t2, a1, t0              #t2 = (length - 1) - i = limite do ciclo interno

inner_loop:
    bge t1, t2, end_inner_loop      #Se j >= limite_interno, sai do ciclo interno

    slli t3, t1, 2                  #t3 = j * 4 - Multiplica por 4 porque cada int tem 4 bytes
    add t3, a0, t3                  #t3 = a0 + deslocamento = endereço de vec[j]

    lw t4, 0(t3)                    #t4 = vec[j]
    lw t5, 4(t3)                    #t5 = vec[j+1]

    ble t4, t5, no_swap             #Se vec[j] <= vec[j+1], está correto, não faz a troca

    #swap
    sw t5, 0(t3)        #guarda vec[j+1] na posição j
    sw t4, 4(t3)        #guarda vec[j] na posição j+1

no_swap:
    addi t1, t1, 1      #j++
    j inner_loop

end_inner_loop:
    addi t0, t0, 1      #i++
    j outer_loop

after_sort:
    addi t0, a1, 1      #t0 = length original

    srli t0, t0, 1      #t0 = length / 2 (índice da mediana)
    slli t0, t0, 2      #t0 = índice * 4 - Multiplica por 4 porque cada int tem 4 bytes
    add t1, a0, t0      #t1 = a0 + deslocamento = endereço de vec[length/2]

    lw t2, 0(t1)        #Carrega o valor da mediana
    sw t2, 0(a2)        #Guarda no apontador *me

    li a0, 1        #Return 1, sucesso
    ret

error:
    li a0, 0        #Return 0, erro
    ret