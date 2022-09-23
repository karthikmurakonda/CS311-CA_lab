package processor.pipeline;
import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Simulator;
import generic.Operand.OperandType;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}
	
	public void performEX()
	{
		//TODO
		if(OF_EX_Latch.isEX_enable())
		{
			int op1 = OF_EX_Latch.getOp1();
			int op2 = OF_EX_Latch.getOp2();
			int imm = OF_EX_Latch.getImm();
			Instruction instruction = OF_EX_Latch.getInstruction();
			int cur_pc = containingProcessor.getRegisterFile().getProgramCounter();
			int alu_result = 0;
			OperationType alu_op = OF_EX_Latch.getInstruction().getOperationType();
			switch(alu_op)
			{
				case add: alu_result = op1 + op2; break;
				case addi: alu_result = op1 + imm; break;
				case sub: alu_result = op1 - op2; break;
				case subi: alu_result = op1 - imm; break;
				case mul: alu_result = op1 * op2; break;
				case muli: alu_result = op1 * imm; break;
				case div: alu_result = op1 / op2; break;
				case divi: alu_result = op1 / imm; break;
				case and: alu_result = op1 & op2; break;
				case andi: alu_result = op1 & imm; break;
				case or: alu_result = op1 | op2; break;
				case ori: alu_result = op1 | imm; break;
				case xor: alu_result = op1 ^ op2; break;
				case xori: alu_result = op1 ^ imm; break;
				case slt: alu_result= (op1 < op2) ? 1 : 0; break;
				case slti: alu_result= (op1 < imm) ? 1 : 0; break;
				case sll: alu_result = op1 << op2; break;
				case slli: alu_result = op1 << imm; break;
				case srl: alu_result = op1 >>> op2; break;
				case srli: alu_result = op1 >>> imm; break;
				case sra: alu_result = op1 >> op2; break;
				case srai: alu_result = op1 >> imm; break;
				case jmp:
				{
					OperandType optype = instruction.getDestinationOperand().getOperandType();
					if (optype == OperandType.Register){
						imm = containingProcessor.getRegisterFile().getValue(
							instruction.getDestinationOperand().getValue());
						}
					else{
						imm = OF_EX_Latch.getImm();
						}
					alu_result = cur_pc + imm;
					EX_IF_Latch.setIF_enable(true);
					EX_IF_Latch.setPC(alu_result);
				}
				break;
				case beq:
				{
					if(op1 == op2)
					{
						alu_result = cur_pc + imm;
						EX_IF_Latch.setIF_enable(true);
						EX_IF_Latch.setPC(alu_result);
					}
				}
				break;
				case bne:
				{
					if(op1 != op2)
					{
						alu_result = cur_pc + imm;
						EX_IF_Latch.setIF_enable(true);
						EX_IF_Latch.setPC(alu_result);
					}
				}
				break;
				case blt:
				{
					if(op1 < op2)
					{
						alu_result = cur_pc + imm;
						EX_IF_Latch.setIF_enable(true);
						EX_IF_Latch.setPC(alu_result);
					}
				}
				break;
				case bgt:
				{
					if(op1 > op2)
					{
						alu_result = cur_pc + imm;
						EX_IF_Latch.setIF_enable(true);
						EX_IF_Latch.setPC(alu_result);
					}
				}
				break;
				case end:
				{
					Simulator.setSimulationComplete(true);
				}
				default:
					break;
				
			}
			EX_MA_Latch.setALUResult(alu_result);
			EX_MA_Latch.setInstruction(OF_EX_Latch.getInstruction());
			EX_MA_Latch.setMA_enable(true);
			OF_EX_Latch.setEX_enable(false);
		}
	}
}
