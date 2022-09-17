	.data
a:
	11
	.text
main:
	load 0, $a, %x3
	divi %x3, 2, %x3
	beq %x31, 0, even
	addi %x0, 1, %x10
	end
even:
	subi %x0, 1, %x10
	end