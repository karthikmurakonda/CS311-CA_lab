.data
a:
	20
	.text
main:
    addi %x0, 2, $x3
    load %x0, $a, %x11
    blt %x11, 2, notAnswer
camparision:
    beq %x11, 2, Answer
    beq %x11, 3, Answer
round:
    div %x11, %x3, %x12
check:
    beq %x31, 0, notAnswer
    addi %x3, 1, %x3
    blt %x3, %x4, round
Answer:
    addi %x0, 1, %x10
    end
notAnswer:
    subi %x0, 1, %x10
    end