	.data
a:
	1777721
	.text
main:
    load %x0, $a, %x3
init:
    addi %x3, 0, %x11
    addi %x3, 0, %x17
    addi %x0, 1, %x15
    addi %x0, 0, %x16
forloop:
    divi %x3, 10, %x3
    muli %x15, 10, %x15
    beq %x3, 0, end1
    jmp forloop
end1:
    divi %x15, 10, %x15
forloop1:
    divi %x11, 10, %x11
    mul %x31, %x15, %x3
    add %x16, %x3, %x16
    divi %x15, 10, %x15
    beq %x11, 0, end2
    jmp forloop1
end2:
    beq %x16, %x17, write1
    subi %x10, 1, %x10
    end
write1:
    addi %x10, 1, %x10
    end