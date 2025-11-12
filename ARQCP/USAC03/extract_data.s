.section .text
.globl extract_data

# ============================================================================
# int extract_data(char* str, char* token, char* unit, int* value)
# ============================================================================

extract_data:
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
    mv s0, a0       # s0 = str
    mv s1, a1       # s1 = token
    mv s2, a2       # s2 = unit (output)
    mv s3, a3       # s3 = value (output pointer)

    # Validate NULL pointers
    beqz s0, error
    beqz s1, error
    beqz s2, error
    beqz s3, error

    # Find token in str
    mv s4, s0       # s4 = current position in str

find_token_in_str:
    lbu t0, 0(s4)
    beqz t0, error      # End of string, token not found

    # Compare token at current position
    mv a0, s4
    mv a1, s1
    call check_token_match

    beqz a0, next_char_token    # Not a match, try next position

    # Token found! Now s4 points to start of token
    # Skip the token
    mv a0, s1
    call get_strlen
    add s4, s4, a0      # s4 now points after the token

    j find_unit

next_char_token:
    addi s4, s4, 1
    j find_token_in_str

find_unit:
    # Find "&unit:" starting from s4 (SEM ESPAÇO!)
    lbu t0, 0(s4)
    beqz t0, error

    # Check if this is "&unit:" (6 caracteres)
    lbu t0, 0(s4)
    li t1, 38           # '&'
    bne t0, t1, skip_one_find_unit

    lbu t0, 1(s4)
    li t1, 117          # 'u'
    bne t0, t1, skip_one_find_unit

    lbu t0, 2(s4)
    li t1, 110          # 'n'
    bne t0, t1, skip_one_find_unit

    lbu t0, 3(s4)
    li t1, 105          # 'i'
    bne t0, t1, skip_one_find_unit

    lbu t0, 4(s4)
    li t1, 116          # 't'
    bne t0, t1, skip_one_find_unit

    lbu t0, 5(s4)
    li t1, 58           # ':' (UM SÓ!)
    bne t0, t1, skip_one_find_unit

    # Found "&unit:", skip it (6 caracteres)
    addi s4, s4, 6
    j extract_unit_string

skip_one_find_unit:
    addi s4, s4, 1
    j find_unit

extract_unit_string:
    # Extract unit name into s2 buffer
    li t0, 0            # index

extract_unit_loop:
    add t1, s4, t0
    lbu t2, 0(t1)

    # Check for terminators
    beqz t2, unit_done
    li t3, 38           # '&'
    beq t2, t3, unit_done
    li t3, 35           # '#'
    beq t2, t3, unit_done

    # Copy character
    add t3, s2, t0
    sb t2, 0(t3)

    addi t0, t0, 1
    j extract_unit_loop

unit_done:
    # Null-terminate unit
    add t3, s2, t0
    sb zero, 0(t3)

    # Update s4
    add s4, s4, t0

find_value:
    # Find "&value:" starting from s4 (SEM ESPAÇO!)
    lbu t0, 0(s4)
    beqz t0, error

    # Check if this is "&value:" (7 caracteres)
    lbu t0, 0(s4)
    li t1, 38           # '&'
    bne t0, t1, skip_one_find_value

    lbu t0, 1(s4)
    li t1, 118          # 'v'
    bne t0, t1, skip_one_find_value

    lbu t0, 2(s4)
    li t1, 97           # 'a'
    bne t0, t1, skip_one_find_value

    lbu t0, 3(s4)
    li t1, 108          # 'l'
    bne t0, t1, skip_one_find_value

    lbu t0, 4(s4)
    li t1, 117          # 'u'
    bne t0, t1, skip_one_find_value

    lbu t0, 5(s4)
    li t1, 101          # 'e'
    bne t0, t1, skip_one_find_value

    lbu t0, 6(s4)
    li t1, 58           # ':' (UM SÓ!)
    bne t0, t1, skip_one_find_value

    # Found "&value:", skip it (7 caracteres)
    addi s4, s4, 7
    j extract_value_int

skip_one_find_value:
    addi s4, s4, 1
    j find_value

extract_value_int:
    # Convert string to int
    li t0, 0            # result = 0

extract_value_loop:
    lbu t1, 0(s4)

    # Check for terminators
    beqz t1, value_done
    li t2, 35           # '#'
    beq t1, t2, value_done

    # Check if digit
    li t2, 48           # '0'
    blt t1, t2, value_done
    li t2, 57           # '9'
    bgt t1, t2, value_done

    # result = result * 10 + (digit - '0')
    li t2, 10
    mul t0, t0, t2

    li t2, 48
    sub t1, t1, t2
    add t0, t0, t1

    addi s4, s4, 1
    j extract_value_loop

value_done:
    # Store result
    sw t0, 0(s3)

    # Return success
    li a0, 1
    j cleanup

error:
    # Clear outputs
    sb zero, 0(s2)
    sw zero, 0(s3)
    li a0, 0

cleanup:
    # Restore registers
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
# check_token_match - Check if token matches at current position
# Arguments: a0 = str position, a1 = token
# Returns: a0 = 1 if match, 0 if no match
# ============================================================================
check_token_match:
    li t0, 0            # index

check_loop:
    add t1, a1, t0      # token[index]
    lbu t2, 0(t1)

    beqz t2, check_match    # End of token = match

    add t3, a0, t0      # str[index]
    lbu t4, 0(t3)

    bne t2, t4, check_no_match

    addi t0, t0, 1
    j check_loop

check_match:
    li a0, 1
    ret

check_no_match:
    li a0, 0
    ret


# ============================================================================
# get_strlen - Get string length
# Arguments: a0 = string
# Returns: a0 = length
# ============================================================================
get_strlen:
    li t0, 0

strlen_loop:
    add t1, a0, t0
    lbu t2, 0(t1)
    beqz t2, strlen_done
    addi t0, t0, 1
    j strlen_loop

strlen_done:
    mv a0, t0
    ret