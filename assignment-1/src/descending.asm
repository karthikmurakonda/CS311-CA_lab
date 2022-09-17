	.data
a:
	70
	80
	40
	20
	10
	30
	50
	60
n:
	8
	.text
main:
    load %x0, $n, %x3
    load %x0, $n, %x9
init:
    subi %x9, 1, %x9
    addi %x0, 0, %x4
loopi:
    addi %x0, 0, %x5
loopj:
    addi %x5, 1, %x6
    load %x6, $a, %x8
    load %x5, $a, %x7
    addi %x5, 1, %x5
check:
    beq %x5, %x3, loopout
    blt %x8, %x7, loopj
    subi %x5, 1, %x30
    addi %x30, 1, %x29
    store %x8, $a, %x30
    store %x7, $a, %x29
    jmp loopj
loopout:
    addi %x4, 1, %x4
    beq %x4, %x9, escape
    jmp loopi
escape:
    end