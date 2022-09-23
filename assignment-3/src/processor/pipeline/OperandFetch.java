package processor.pipeline;

import java.util.Arrays;

import generic.Instruction;
import processor.Processor;
import generic.Instruction.OperationType;
import generic.Operand;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	static OperationType[] opTypes = OperationType.values();
	
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}
	
	public void performOF()
	{
		if(IF_OF_Latch.isOF_enable())
		{
			int instruction = IF_OF_Latch.getInstruction();
			Instruction instr = new Instruction();
			String bin_instr = Integer.toBinaryString(instruction);
			int opcode = Integer.parseInt(bin_instr.substring(0, 5), 2);
			instr.setOperationType(opTypes[opcode]);
			
			int R3_type_operators[] = {0,2,4,6,8,10,12,14,16,18,20};
			int R2I_type_operators[] = {1,3,5,7,9,11,13,15,17,19,21,22,23,25,26,27,28};
			int R1I_type_operators[] = {24,29};

			// if (bin_instr.length() < 32) {	// TODO: check if this is correct
			// 	int diff = 32 - bin_instr.length();
			// 	String zeros = "";
			// 	for (int i = 0; i < diff; i++) {
			// 		zeros += "0";
			// 	}
			// 	bin_instr = zeros + bin_instr;
			// }

			if (Arrays.asList(R3_type_operators).contains(opcode)){
				Operand rs1 = new Operand();
				Operand rs2 = new Operand();
				Operand rd = new Operand();
				rs1.setOperandType(Operand.OperandType.Register);
				rs2.setOperandType(Operand.OperandType.Register);
				rd.setOperandType(Operand.OperandType.Register);

				rs1.setValue(Integer.parseInt(bin_instr.substring(5, 10), 2));
				rs2.setValue(Integer.parseInt(bin_instr.substring(9, 14), 2));
				rd.setValue(Integer.parseInt(bin_instr.substring(14, 19), 2));

				int op1 = containingProcessor.getRegisterFile().getValue(rs1.getValue());
				int op2 = containingProcessor.getRegisterFile().getValue(rs2.getValue());
				
				OF_EX_Latch.setInstruction(instr);
				OF_EX_Latch.setOp1(op1);
				OF_EX_Latch.setOp2(op2);
				instr.setDestinationOperand(rd);
				instr.setSourceOperand1(rs1);
				instr.setSourceOperand2(rs2);
			}
			else if (Arrays.asList(R2I_type_operators).contains(opcode)){
				Operand rs1 = new Operand();
				Operand rd = new Operand();
				rs1.setOperandType(Operand.OperandType.Register);
				rd.setOperandType(Operand.OperandType.Register);

				rs1.setValue(Integer.parseInt(bin_instr.substring(5, 10), 2));
				rd.setValue(Integer.parseInt(bin_instr.substring(14, 19), 2));

				int imm = Integer.parseInt(bin_instr.substring(19, 32), 2); // TODO: 2's complement

				int op1 = containingProcessor.getRegisterFile().getValue(rs1.getValue());

				OF_EX_Latch.setInstruction(instr);
				OF_EX_Latch.setImm(imm);
				OF_EX_Latch.setOp1(op1);

				instr.setDestinationOperand(rd);
				instr.setSourceOperand1(rs1);
			}
			else if (Arrays.asList(R1I_type_operators).contains(opcode)){
				Operand rd = new Operand();
				rd.setOperandType(Operand.OperandType.Register);
				rd.setValue(Integer.parseInt(bin_instr.substring(5, 10), 2));

				instr.setDestinationOperand(rd);

				int imm = Integer.parseInt(bin_instr.substring(10, 32), 2); // TODO: 2's complement

				OF_EX_Latch.setInstruction(instr);
				OF_EX_Latch.setImm(imm);
			}

			IF_OF_Latch.setOF_enable(false);
			OF_EX_Latch.setEX_enable(true);
		}
	}

}
