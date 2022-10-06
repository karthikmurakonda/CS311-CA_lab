package processor.pipeline;

import java.util.Arrays;

import generic.Instruction;
import processor.Processor;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;
import generic.Operand;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	static OperationType[] opTypes = OperationType.values();
	boolean Proceed;
	
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
		Proceed = true;
	}

	public static int twoscompliment(String s) {
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '0') {
				chars[i] = '1';
			} else {
				chars[i] = '0';
			}
		}
		String s1 = new String(chars);
		int num = Integer.parseInt(s1, 2);
		num = num + 1;
		return num;
	}
	
	public void performOF()
	{
		if(IF_OF_Latch.isOF_enable() && Proceed)
		{
			int instruction = IF_OF_Latch.getInstruction();
			Instruction instr = new Instruction();
			String bin_instr = Integer.toBinaryString(instruction);
			if (bin_instr.length() < 32) {
				int diff = 32 - bin_instr.length();
				String zeros = "";
				for (int i = 0; i < diff; i++) {
					zeros += "0";
				}
				bin_instr = zeros + bin_instr;
			}
			instr.setProgramCounter(containingProcessor.getRegisterFile().getProgramCounter());
			int opcode = Integer.parseInt(bin_instr.substring(0, 5), 2);
			instr.setOperationType(opTypes[opcode]);
			
			int R3_type_operators[] = {0,2,4,6,8,10,12,14,16,18,20};
			int R2I_type_operators[] = {1,3,5,7,9,11,13,15,17,19,21,22,23,25,26,27,28};
			int R1I_type_operators[] = {24,29};

			// check if the instruction is of type R3
			if (Arrays.stream(R3_type_operators).anyMatch(x -> x == opcode)) {
				Operand rs1 = new Operand();
				Operand rs2 = new Operand();
				Operand rd = new Operand();
				rs1.setOperandType(Operand.OperandType.Register);
				rs2.setOperandType(Operand.OperandType.Register);
				rd.setOperandType(Operand.OperandType.Register);

				rs1.setValue(Integer.parseInt(bin_instr.substring(5, 10), 2));
				rs2.setValue(Integer.parseInt(bin_instr.substring(10, 15), 2));
				rd.setValue(Integer.parseInt(bin_instr.substring(15, 20), 2));

				int op1 = containingProcessor.getRegisterFile().getValue(rs1.getValue());
				int op2 = containingProcessor.getRegisterFile().getValue(rs2.getValue());
				
				OF_EX_Latch.setInstruction(instr);
				OF_EX_Latch.setOp1(op1);
				OF_EX_Latch.setOp2(op2);
				instr.setDestinationOperand(rd);
				instr.setSourceOperand1(rs1);
				instr.setSourceOperand2(rs2);
			}
			else if (Arrays.stream(R2I_type_operators).anyMatch(x -> x == opcode)) {
				Operand rs1 = new Operand();
				Operand rd = new Operand();
				rs1.setOperandType(Operand.OperandType.Register);
				rd.setOperandType(Operand.OperandType.Register);

				rs1.setValue(Integer.parseInt(bin_instr.substring(5, 10), 2));
				rd.setValue(Integer.parseInt(bin_instr.substring(10, 15), 2));
				// check 15th bit to see if it is negative
				int imm = Integer.parseInt(bin_instr.substring(15, 32), 2);
				if (bin_instr.charAt(15)=='1'){
					imm = -1*twoscompliment(bin_instr.substring(15, 32));
					System.out.println(bin_instr);
				}
				int op1 = containingProcessor.getRegisterFile().getValue(rs1.getValue());
				int op2 = containingProcessor.getRegisterFile().getValue(rd.getValue());
				System.out.println("imm: " + imm);

				OF_EX_Latch.setInstruction(instr);
				OF_EX_Latch.setImm(imm);
				OF_EX_Latch.setOp1(op1);
				OF_EX_Latch.setOp2(op2);

				System.out.println("op1: " + op1);
				System.out.println("op2: " + rd);
				instr.setDestinationOperand(rd);
				instr.setSourceOperand1(rs1);
			}
			else if (Arrays.stream(R1I_type_operators).anyMatch(x -> x == opcode)) {
				if(opcode != 24){
					Operand rd = new Operand();
					rd.setOperandType(Operand.OperandType.Register);
					rd.setValue(Integer.parseInt(bin_instr.substring(5, 10), 2));
	
					instr.setDestinationOperand(rd);
	
					int imm = Integer.parseInt(bin_instr.substring(10, 32), 2);
					if (bin_instr.charAt(10)=='1'){
						imm = -1*twoscompliment(bin_instr.substring(10, 32));
						System.out.println(bin_instr);
					}
					System.out.println("imm: " + imm);
					OF_EX_Latch.setInstruction(instr);
					OF_EX_Latch.setImm(imm);
				}
				else{
					Operand op = new Operand();
					String imm = bin_instr.substring(10, 32);
					int imm_val = Integer.parseInt(imm, 2);
					if (imm.charAt(0) == '1'){
						imm_val = -1*twoscompliment(imm);
					}
					if (imm_val != 0){
						op.setOperandType(OperandType.Immediate);
						op.setValue(imm_val);
						instr.setSourceOperand1(op);
					}
					else{
						op.setOperandType(OperandType.Register);
						op.setValue(Integer.parseInt(bin_instr.substring(5, 10), 2));
						instr.setSourceOperand1(op);
					}
					OF_EX_Latch.setInstruction(instr);
					OF_EX_Latch.setImm(imm_val);
				}
			}

			OF_EX_Latch.setEX_enable(true);
		}
		else if (!Proceed) {
			Proceed = true;
		}
	}

	public void setProceed(boolean proceed) {
		Proceed = proceed;
		if (!Proceed) {
			OF_EX_Latch.setEX_enable(false);
		}
	}

}
