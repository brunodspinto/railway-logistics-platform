.section .text
.globl format_command

# ============================================================================
# int format_command(char* op, int n, char *cmd)
# ============================================================================

format_command:
    # Save registers
    addi sp, sp, -32
    sw ra, 28(sp)
    sw s0, 24(sp)
    sw s1, 20(sp)
    sw s2, 16(sp)
    sw s3, 12(sp)
    sw s4, 8(sp)
    sw s5, 4(sp)
    sw s6, 0(sp)

    # Save arguments
    mv s0, a0       # s0 = op
    mv s1, a1       # s1 = n
    mv s2, a2       # s2 = cmd (output)

    # Validate NULL pointers
    beqz s0, error_null     # op is NULL
    beqz s2, error_null     # cmd is NULL

    # Step 1: Trim and capitalize op into a temporary buffer
    addi s3, sp, -10    # s3 = temp buffer on stack (max 10 chars)
    addi sp, sp, -10

    mv a0, s0
    mv a1, s3
    call trim_and_capitalize

    # Step 2: Identify command type
    mv a0, s3
    call identify_command

    mv s4, a0       # s4 = command type

    beqz s4, cleanup_and_error  # Invalid command

    # Step 3: Format output based on command type
    li t0, 5
    beq s4, t0, format_gth      # If GTH, no number needed

    # For RE, YE, GE, RB: validate n range [0, 99]
    bltz s1, cleanup_and_error  # n < 0
    li t0, 99
    bgt s1, t0, cleanup_and_error  # n > 99

    # Format as "CMD,xx"
    mv a0, s3       # trimmed command
    mv a1, s1       # number
    mv a2, s2       # output buffer
    call format_with_number

    j cleanup_success

format_gth:
    # Format as "GTH" (just copy)
    mv a0, s3
    mv a1, s2
    call copy_string

    j cleanup_success

cleanup_and_error:
    addi sp, sp, 10     # Restore temp buffer space
    j error

cleanup_success:
    addi sp, sp, 10     # Restore temp buffer space
    li a0, 1
    j cleanup

error:
    # s2 is valid here (we checked at the start)
    sb zero, 0(s2)      # cmd[0] = '\0'
    li a0, 0
    j cleanup

error_null:
    # One or both pointers are NULL
    # DON'T try to write to cmd if it's NULL
    li a0, 0
    # Just return, don't access s2

cleanup:
    lw s6, 0(sp)
    lw s5, 4(sp)
    lw s4, 8(sp)
    lw s3, 12(sp)
    lw s2, 16(sp)
    lw s1, 20(sp)
    lw s0, 24(sp)
    lw ra, 28(sp)
    addi sp, sp, 32
    ret


# ============================================================================
# trim_and_capitalize - REMOVE TODOS OS ESPAÇOS
# ============================================================================
trim_and_capitalize:
    mv t0, a0           # input pointer
    mv t1, a1           # output pointer
    li t2, 0            # output index

trim_loop:
    lbu t3, 0(t0)
    beqz t3, trim_done  # end of string

    # Skip spaces (ASCII 32)
    li t4, 32
    beq t3, t4, trim_skip

    # Convert to uppercase if lowercase
    li t4, 97           # 'a'
    blt t3, t4, not_lower
    li t4, 122          # 'z'
    bgt t3, t4, not_lower

    # Convert to uppercase
    addi t3, t3, -32

not_lower:
    # Store character
    add t5, t1, t2
    sb t3, 0(t5)
    addi t2, t2, 1

    # Safety check - don't overflow temp buffer
    li t4, 9
    bge t2, t4, trim_done

trim_skip:
    addi t0, t0, 1
    j trim_loop

trim_done:
    # Null-terminate
    add t5, t1, t2
    sb zero, 0(t5)
    ret

# ============================================================================
# identify_command
# ============================================================================
identify_command:
    # Check length first
    mv t0, a0
    li t1, 0            # length counter

count_len:
    lbu t2, 0(t0)
    beqz t2, check_cmd
    addi t0, t0, 1
    addi t1, t1, 1
    j count_len

check_cmd:
    # Length in t1, string in a0

    # Check for 2-char commands (RE, YE, GE, RB)
    li t2, 2
    beq t1, t2, check_2char

    # Check for 3-char command (GTH)
    li t2, 3
    beq t1, t2, check_gth

    # Invalid length
    li a0, 0
    ret

check_2char:
    lbu t0, 0(a0)       # First char
    lbu t1, 1(a0)       # Second char

    # Check RE
    li t2, 82           # 'R'
    bne t0, t2, check_ye
    li t2, 69           # 'E'
    bne t1, t2, check_ye
    li a0, 1            # RE
    ret

check_ye:
    lbu t0, 0(a0)
    lbu t1, 1(a0)
    li t2, 89           # 'Y'
    bne t0, t2, check_ge
    li t2, 69           # 'E'
    bne t1, t2, check_ge
    li a0, 2            # YE
    ret

check_ge:
    lbu t0, 0(a0)
    lbu t1, 1(a0)
    li t2, 71           # 'G'
    bne t0, t2, check_rb
    li t2, 69           # 'E'
    bne t1, t2, check_rb
    li a0, 3            # GE
    ret

check_rb:
    lbu t0, 0(a0)
    lbu t1, 1(a0)
    li t2, 82           # 'R'
    bne t0, t2, invalid_cmd
    li t2, 66           # 'B'
    bne t1, t2, invalid_cmd
    li a0, 4            # RB
    ret

check_gth:
    lbu t0, 0(a0)       # 'G'
    lbu t1, 1(a0)       # 'T'
    lbu t2, 2(a0)       # 'H'

    li t3, 71           # 'G'
    bne t0, t3, invalid_cmd
    li t3, 84           # 'T'
    bne t1, t3, invalid_cmd
    li t3, 72           # 'H'
    bne t2, t3, invalid_cmd

    li a0, 5            # GTH
    ret

invalid_cmd:
    li a0, 0
    ret


# ============================================================================
# format_with_number
# ============================================================================
format_with_number:
    addi sp, sp, -4
    sw ra, 0(sp)

    # Copy command
    mv t0, a0           # source
    mv t1, a2           # dest

copy_cmd:
    lbu t2, 0(t0)
    beqz t2, add_comma
    sb t2, 0(t1)
    addi t0, t0, 1
    addi t1, t1, 1
    j copy_cmd

add_comma:
    li t2, 44           # ','
    sb t2, 0(t1)
    addi t1, t1, 1

    # Convert number to 2-digit string
    mv t0, a1           # number

    # Tens digit
    li t2, 10
    div t3, t0, t2      # t3 = n / 10
    rem t4, t0, t2      # t4 = n % 10

    # Store tens
    addi t3, t3, 48     # + '0'
    sb t3, 0(t1)
    addi t1, t1, 1

    # Store units
    addi t4, t4, 48     # + '0'
    sb t4, 0(t1)
    addi t1, t1, 1

    # Null-terminate
    sb zero, 0(t1)

    lw ra, 0(sp)
    addi sp, sp, 4
    ret


# ============================================================================
# copy_string
# ============================================================================
copy_string:
    li t0, 0

copy_loop_str:
    add t1, a0, t0
    lbu t2, 0(t1)

    add t3, a1, t0
    sb t2, 0(t3)

    beqz t2, copy_done
    addi t0, t0, 1
    j copy_loop_str

copy_done:
    ret