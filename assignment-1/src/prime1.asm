    .data
n:x5x5x5 
    59
    .text
main:
    load %x0, $n, %x3
    addi %x4, 2, %x4
forloop:
    div %x3, %x4, %x6
    beq %x31, %x7, nonprime
    addi %x4, 1, %x4
    blt %x4, %x3, forloop
prime:
    addi %x5, 1, %x5
    end
nonprime:
    subi %x5, 1, %x5
    end