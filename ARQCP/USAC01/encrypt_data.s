        .text
        .globl encrypt_data

encrypt_data:
        addi sp, sp, -32
        sd ra, 24(sp)
        sd s0, 16(sp)
        sd s1, 8(sp)
        sd s2, 0(sp)

        mv s0, a0
        mv s1, a1
        mv s2, a2

        li t0, 1
        blt s1, t0,fail
        li t0, 26
        bgt s1, t0, fail

loop:
        lbu t1, 0(s0)
        beqz t1, end

        li t2, 'A'
        blt t1, t2, fail
        li t3, 'Z'
        bgt t1, t3, fail

        sub t4, t1, t2
        add t4, t4, s1
        li t5, 26
        remu t4, t4, t5
        add t4, t4, t2
        sb t4, 0(s2)

        addi s0, s0, 1
        addi s2, s2, 1
        j loop

end:
        sb zero, 0(s2)
        li a0, 1
        j restore

fail:
        sb zero, 0(s2)
        li a0, 0

restore:
        ld ra, 24(sp)
        ld s0, 16(sp)
        ld s1, 8(sp)
        ld s2, 0(sp)
        addi sp, sp, 32
        jr ra