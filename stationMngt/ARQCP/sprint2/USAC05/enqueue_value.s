	    .section .text
	    .global enqueue_value

enqueue_value:

	# a0 = buffer
    	# a1 = length
    	# a2 = nelem (ptr)
    	# a3 = tail  (ptr)
    	# a4 = head  (ptr)
    	# a5 = value

    	# Load nelem
    	lw t0, 0(a2)       # t0 = *nelem

    	# Check if buffer is full: (nelem == length)
    	beq t0, a1, buffer_full

buffer_not_full:

    	# Load head
    	lw t1, 0(a4)

    	# Insert value: buffer[head] = value
    	slli t2, t1, 2     # t2 = head * 4
    	add t2, t2, a0
    	sw a5, 0(t2)

    	# head = (head + 1) % length
    	addi t1, t1, 1
    	rem  t1, t1, a1
    	sw t1, 0(a4)

    	# Increment nelem
    	addi t0, t0, 1
    	sw t0, 0(a2)

    	# Return 1 if buffer is full now
    	beq t0, a1, ret_full

    	# Return 0
    	li a0, 0
    	ret

buffer_full:

    	# Load tail
    	lw t3, 0(a3)

    	# Remove oldest → tail = (tail + 1) % length
    	addi t3, t3, 1
    	rem  t3, t3, a1
    	sw t3, 0(a3)

    	# Load head
    	lw t1, 0(a4)

    	# Insert new value
    	slli t2, t1, 2
    	add t2, t2, a0
    	sw a5, 0(t2)

    	# head = (head + 1) % length
    	addi t1, t1, 1
    	rem  t1, t1, a1
    	sw t1, 0(a4)

    	# nelem remains = length
    	li a0, 1
    	ret

ret_full:

    	li a0, 1
    	ret