	.data
n:
	10
	.text
main:
	addi %x0, 65535, %x10
	load %x0, $n, %x11
	addi %x0, 0, %x12
	addi %x0, 0, %x13
	addi %x0, 1, %x14
	addi %x12, 1, %x12
	add %x13, %x14, %x8
	store %x13, $n, %x10
	subi %x10, 1, %x10
	beq %x11, %x12, endl
	addi %x12, 1, %x12
	store %x14, $n, %x10
	subi %x10, 1, %x10
	beq %x11, %x12, endl
forloop:
	store %x8, $n, %x10
	subi %x10, 1, %x10
	addi %x12, 1, %x12
	beq %x11, %x12, endl
	addi %x14, 0, %x13
	addi %x8, 0, %x14
	add %x13, %x14, %x8
	jmp forloop
endl:
	end