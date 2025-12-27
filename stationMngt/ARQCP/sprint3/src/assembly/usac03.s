.section .text
.global extract_data


# int extract_data(char* str, char* token, char* unit, int* value)

extract_data:
    addi sp, sp, -32
    sw ra, 28(sp)
    sw s0, 24(sp)
    sw s1, 20(sp)
    sw s2, 16(sp)
    sw s3, 12(sp)
    sw s4, 8(sp)

    mv s0, a0      # str
    mv s1, a1      # token
    mv s2, a2      # unit
    mv s3, a3      # value ptr

    # Validate pointers
    beqz s0, error
    beqz s1, error
    beqz s2, error
    beqz s3, error

    # Find token in string
    mv s4, s0      # s4 = current position

find_token:
    lbu t0, 0(s4)
    beqz t0, error

    # Token can start at beginning OR after '#'
    beq s4, s0, try_match       # Start of string

    lbu t0, -1(s4)              # Previous char
    li t1, '#'
    bne t0, t1, skip_pos        # Not after '#', skip

try_match:
    mv a0, s4
    mv a1, s1
    call match_token
    bnez a0, found_token

skip_pos:
    addi s4, s4, 1
    j find_token

found_token:
    # Skip token
    mv a0, s1
    call strlen
    add s4, s4, a0

    # Find "&unit:"
find_unit:
    lbu t0, 0(s4)
    beqz t0, error

    # Check for "&unit:" pattern
    li t1, '&'
    bne t0, t1, skip_unit

    lbu t0, 1(s4)
    li t1, 'u'
    bne t0, t1, skip_unit

    lbu t0, 2(s4)
    li t1, 'n'
    bne t0, t1, skip_unit

    lbu t0, 3(s4)
    li t1, 'i'
    bne t0, t1, skip_unit

    lbu t0, 4(s4)
    li t1, 't'
    bne t0, t1, skip_unit

    lbu t0, 5(s4)
    li t1, ':'
    bne t0, t1, skip_unit

    addi s4, s4, 6
    j extract_unit

skip_unit:
    addi s4, s4, 1
    j find_unit

extract_unit:
    li t0, 0        # index

copy_unit:
    lbu t1, 0(s4)

    # Stop on terminators
    beqz t1, unit_done
    li t2, '&'
    beq t1, t2, unit_done
    li t2, '#'
    beq t1, t2, unit_done

    # Copy char
    sb t1, 0(s2)
    addi s4, s4, 1
    addi s2, s2, 1
    addi t0, t0, 1

    # Limit to 90 chars
    li t2, 90
    blt t0, t2, copy_unit

unit_done:
    sb zero, 0(s2)

    # Find "&value:"
find_value:
    lbu t0, 0(s4)
    beqz t0, error

    # Check for "&value:" pattern
    li t1, '&'
    bne t0, t1, skip_value

    lbu t0, 1(s4)
    li t1, 'v'
    bne t0, t1, skip_value

    lbu t0, 2(s4)
    li t1, 'a'
    bne t0, t1, skip_value

    lbu t0, 3(s4)
    li t1, 'l'
    bne t0, t1, skip_value

    lbu t0, 4(s4)
    li t1, 'u'
    bne t0, t1, skip_value

    lbu t0, 5(s4)
    li t1, 'e'
    bne t0, t1, skip_value

    lbu t0, 6(s4)
    li t1, ':'
    bne t0, t1, skip_value

    addi s4, s4, 7
    j extract_value

skip_value:
    addi s4, s4, 1
    j find_value

extract_value:
    li t0, 0        # result

parse_digit:
    lbu t1, 0(s4)

    # Stop on terminators
    beqz t1, value_done
    li t2, '#'
    beq t1, t2, value_done

    # Check if digit
    li t2, '0'
    blt t1, t2, value_done
    li t2, '9'
    bgt t1, t2, value_done

    # result = result * 10 + (char - '0')
    li t2, 10
    mul t0, t0, t2
    addi t1, t1, -48    # '0' = 48
    add t0, t0, t1

    addi s4, s4, 1
    j parse_digit

value_done:
    sw t0, 0(s3)
    li a0, 1
    j cleanup

error:
    sb zero, 0(s2)
    sw zero, 0(s3)
    li a0, 0

cleanup:
    lw s4, 8(sp)
    lw s3, 12(sp)
    lw s2, 16(sp)
    lw s1, 20(sp)
    lw s0, 24(sp)
    lw ra, 28(sp)
    addi sp, sp, 32
    ret


# match_token - Check if token matches at position
# a0 = str position, a1 = token
# Returns: 1 if match, 0 otherwise

match_token:
    li t0, 0

compare_loop:
    lbu t1, 0(a1)       # token[i]
    beqz t1, check_boundary

    lbu t2, 0(a0)       # str[i]
    bne t1, t2, no_match

    addi a0, a0, 1
    addi a1, a1, 1
    j compare_loop

check_boundary:
    # After token must be '&', '#', or '\0'
    lbu t0, 0(a0)
    beqz t0, yes_match
    li t1, '&'
    beq t0, t1, yes_match
    li t1, '#'
    beq t0, t1, yes_match

no_match:
    li a0, 0
    ret

yes_match:
    li a0, 1
    ret


# strlen - Get string length

strlen:
    li t0, 0

len_loop:
    lbu t1, 0(a0)
    beqz t1, len_done
    addi a0, a0, 1
    addi t0, t0, 1
    j len_loop

len_done:
    mv a0, t0
    ret