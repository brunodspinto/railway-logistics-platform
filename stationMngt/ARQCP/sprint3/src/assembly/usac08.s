    .section .text
    .global sort_array

sort_array:
    blez a1, error          #Se a1 <= 0, salta para erro

    addi a1, a1, -1         #a1 passa a ser (length - 1)
    blez a1, success        #Se o array tiver apenas 1 elemento (length - 1 = 0), já está ordenado

    li t0, 0                #t0 = i (contador do ciclo externo)

outer_loop:
    bge t0, a1, success     #Se i >= length - 1, termina com sucesso

    li t1, 0                #t1 = j (contador do ciclo interno)
    sub t2, a1, t0          #t2 = (length - 1) - i = limite do ciclo interno

inner_loop:
    bge t1, t2, end_inner_loop      #Se j >= limite_interno, sai do ciclo interno

    slli t3, t1, 2                  #t3 = j * 4 - Multiplica por 4 porque cada int tem 4 bytes
    add t3, a0, t3                  #t3 = a0 + deslocamento = endereço de vec[j]

    lw t4, 0(t3)                    #t4 = vec[j]
    lw t5, 4(t3)                    #t5 = vec[j+1]

    beqz a2, descending         #Se ordem == 0, vai para a lógica descendente

ascending:
    ble t4, t5, no_swap         #Se vec[j] <= vec[j+1], está correto, não faz a troca
    j swap                      #Caso contrário, faz a troca

descending:
    bge t4, t5, no_swap         #Se vec[j] >= vec[j+1], está correto, não faz a troca

swap:
    sw t5, 0(t3)        #Guarda vec[j+1] na posição j
    sw t4, 4(t3)        #Guarda vec[j] na posição j+1

no_swap:
    addi t1, t1, 1      #j++
    j inner_loop

end_inner_loop:
    addi t0, t0, 1      #i++
    j outer_loop

error:
    li a0, 0        #Retorna 0 (falha)
    ret

success:
    li a0, 1        #Retorna 1 (sucesso)
    ret