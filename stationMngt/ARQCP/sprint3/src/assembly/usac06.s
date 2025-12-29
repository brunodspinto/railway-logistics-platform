    	.section .text
    	.global dequeue_value

dequeue_value:

    	# a0 = buffer
    	# a1 = length
    	# a2 = nelem (ptr)
    	# a3 = tail  (ptr)
    	# a4 = head  (ptr)
    	# a5 = value (ptr)

    	# Load nelem
    	lw t0, 0(a2)      # t0 = *nelem

    	# If nelem == 0 → buffer empty → return 0
    	beqz t0, empty_buffer

    	# Load tail index
    	lw t1, 0(a3)

    	# Compute address of buffer[tail]
    	slli t2, t1, 2    # t2 = tail * 4
    	add t2, t2, a0    # &buffer[tail]

    	# Load the oldest element
    	lw t3, 0(t2)

    	# Store value into *value
    	sw t3, 0(a5)

    	# Advance tail = (tail + 1) % length
    	addi t1, t1, 1
    	rem  t1, t1, a1
    	sw t1, 0(a3)

    	# Decrement nelem
    	addi t0, t0, -1
    	sw t0, 0(a2)

    	# Success → return 1
    	li a0, 1
    	ret

empty_buffer:

    	# Failure → return 0
    	li a0, 0
    	ret