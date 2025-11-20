.section .text
.global format_command


# int format_command(char* op, int n, char *cmd)

format_command:
    addi sp, sp, -32
    sw ra, 28(sp)
    sw s0, 24(sp)
    sw s1, 20(sp)
    sw s2, 16(sp)

    mv s0, a0       # op
    mv s1, a1       # n
    mv s2, a2       # cmd

    # Validate pointers
    beqz s0, error
    beqz s2, error

    # Trim and uppercase directly into cmd buffer
    mv a0, s0
    mv a1, s2
    call trim_and_uppercase

    # Check length: must be 2 or 3
    mv a0, s2
    call string_length
    mv t6, a0       # t6 = length

    li t0, 2
    beq t6, t0, check_2char
    li t0, 3
    beq t6, t0, check_gth
    j error         # Invalid length

check_gth:
    # Must be "GTH"
    lbu t0, 0(s2)
    li t3, 'G'
    bne t0, t3, error

    lbu t1, 1(s2)
    li t3, 'T'
    bne t1, t3, error

    lbu t2, 2(s2)
    li t3, 'H'
    bne t2, t3, error

    # Valid GTH - already in cmd
    j success

check_2char:
    # Validate n (0-99)
    bltz s1, error
    li t3, 99
    bgt s1, t3, error

    # Check valid 2-char commands
    lbu t0, 0(s2)
    lbu t1, 1(s2)

    # Check RE
    li t3, 'R'
    bne t0, t3, check_ye
    li t3, 'E'
    beq t1, t3, valid_cmd

    # Check RB
    li t3, 'B'
    beq t1, t3, valid_cmd
    j error

check_ye:
    li t3, 'Y'
    bne t0, t3, check_ge
    li t3, 'E'
    beq t1, t3, valid_cmd
    j error

check_ge:
    li t3, 'G'
    bne t0, t3, error
    li t3, 'E'
    bne t1, t3, error

valid_cmd:
    # Append ",NN" to cmd
    li t0, ','
    sb t0, 2(s2)        # cmd[2] = ','

    # Convert n to 2 digits
    li t0, 10
    div t1, s1, t0      # tens
    rem t2, s1, t0      # units

    addi t1, t1, '0'
    sb t1, 3(s2)        # cmd[3] = tens

    addi t2, t2, '0'
    sb t2, 4(s2)        # cmd[4] = units

    sb zero, 5(s2)      # null terminator

success:
    li a0, 1
    j cleanup

error:
    sb zero, 0(s2)
    li a0, 0

cleanup:
    lw ra, 28(sp)
    lw s0, 24(sp)
    lw s1, 20(sp)
    lw s2, 16(sp)
    addi sp, sp, 32
    ret


# trim_and_uppercase(char* src, char* dest)
# Remove ALL spaces and convert to uppercase

trim_and_uppercase:
    li t0, 0            # src index
    li t1, 0            # dest index

trim_loop:
    add t2, a0, t0
    lbu t3, 0(t2)
    beqz t3, trim_done

    # Skip ALL spaces (not just leading/trailing)
    li t4, ' '
    beq t3, t4, trim_skip

    # Uppercase if lowercase
    li t4, 'a'
    blt t3, t4, trim_copy
    li t4, 'z'
    bgt t3, t4, trim_copy
    addi t3, t3, -32    # to uppercase

trim_copy:
    add t4, a1, t1
    sb t3, 0(t4)
    addi t1, t1, 1

trim_skip:
    addi t0, t0, 1
    j trim_loop

trim_done:
    add t4, a1, t1
    sb zero, 0(t4)
    ret


# string_length(char* str)
# Returns length in a0

string_length:
    li t0, 0

len_loop:
    add t1, a0, t0
    lbu t2, 0(t1)
    beqz t2, len_done
    addi t0, t0, 1
    j len_loop

len_done:
    mv a0, t0
    ret