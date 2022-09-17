package generic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class Simulator {

	static FileInputStream inputcodeStream = null;
	public static Map<Instruction.OperationType, String> mapping = new HashMap<Instruction.OperationType, String>();
	static {
		mapping.put(Instruction.OperationType.add, "00000");
		mapping.put(Instruction.OperationType.addi, "00001");
		mapping.put(Instruction.OperationType.sub, "00010");
		mapping.put(Instruction.OperationType.subi, "00011");
		mapping.put(Instruction.OperationType.mul, "00100");
		mapping.put(Instruction.OperationType.muli, "00101");
		mapping.put(Instruction.OperationType.div, "00110");
		mapping.put(Instruction.OperationType.divi, "00111");
		mapping.put(Instruction.OperationType.and, "01000");
		mapping.put(Instruction.OperationType.andi, "01001");
		mapping.put(Instruction.OperationType.or, "01010");
		mapping.put(Instruction.OperationType.ori, "01011");
		mapping.put(Instruction.OperationType.xor, "01100");
		mapping.put(Instruction.OperationType.xori, "01101");
		mapping.put(Instruction.OperationType.slt, "01110");
		mapping.put(Instruction.OperationType.slti, "01111");
		mapping.put(Instruction.OperationType.sll, "10000");
		mapping.put(Instruction.OperationType.slli, "10001");
		mapping.put(Instruction.OperationType.srl, "10010");
		mapping.put(Instruction.OperationType.srli, "10011");
		mapping.put(Instruction.OperationType.sra, "10100");
		mapping.put(Instruction.OperationType.srai, "10101");
		mapping.put(Instruction.OperationType.load, "10110");
		mapping.put(Instruction.OperationType.end, "11101");
		mapping.put(Instruction.OperationType.beq, "11001");
		mapping.put(Instruction.OperationType.jmp, "11000");
		mapping.put(Instruction.OperationType.bne, "11010");
		mapping.put(Instruction.OperationType.blt, "11011");
		mapping.put(Instruction.OperationType.bgt, "11100");
	}


	public static void setupSimulation(String assemblyProgramFile) {
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}
	
	private static String toBinaryOfSpecificPrecision(int num, int lenOfTargetString) {
		String binary = String.format("%" + lenOfTargetString + "s", Integer.toBinaryString(num)).replace(' ', '0');
		return binary;
	}
	
	private static int toSignedInteger(String binary) {
		int n = 32 - binary.length();
        char[] sign_ext = new char[n];
        Arrays.fill(sign_ext, binary.charAt(0));
        int signedInteger = (int) Long.parseLong(new String(sign_ext) + binary, 2);
        return signedInteger;
	}
	
	private static String toBinaryString(int n) {
		// Remove this conditional statement
		// if (n >= 0) return String.valueOf(n);

		Stack<Integer> bits = new Stack<>();
		do {
			bits.push(n % 2);
			n /= 2;
		} while (n != 0);

		StringBuilder builder = new StringBuilder();
		while (!bits.isEmpty()) {
			builder.append(bits.pop());
		}
		return " " + builder.toString();
	}

	private static String convert(Operand inst, int precision) {
		if (inst == null)
			return toBinaryOfSpecificPrecision(0, precision);

		if (inst.getOperandType() == Operand.OperandType.Label)
			return toBinaryOfSpecificPrecision(ParsedProgram.symtab.get(inst.getLabelValue()), precision);

		// write logic for converting to binary/ hex
		return toBinaryOfSpecificPrecision(inst.getValue(), precision);
		// check if inst is a label, in that case, use its value 
		// return String.valueOf(inst.getValue());
	}

	public static void assemble(String objectProgramFile) {
		FileOutputStream file;
		try {
			//1. open the objectProgramFile in binary mode
			file = new FileOutputStream(objectProgramFile);
			BufferedOutputStream bfile = new BufferedOutputStream(file);

			//2. write the firstCodeAddress to the file
			byte[] addressCode = ByteBuffer.allocate(4).putInt(ParsedProgram.firstCodeAddress).array();
			bfile.write(addressCode);

			//3. write the data to the file
			for (int value: ParsedProgram.data) {
				byte[] dataValue = ByteBuffer.allocate(4).putInt(value).array();
				bfile.write(dataValue);
			}

			//4. assemble one instruction at a time, and write to the file
			for (Instruction inst: ParsedProgram.code) {
				/**
				 * inst.getSourceOperand().getValue() will be passed to a function as f()
				 * that will change decimal to binary and then will return the string
				 * form of the binary. It will also check if the value is a label,
				 * in case it is a label, it would call ParsedProgram.symtab.get()
				 * to get the address corresponding to the label
				 */
				String binaryRep = "";

				// print operation type, use toBinaryString() instead of convert()
				// file.write(mapping.get(inst.getOperationType()));
				binaryRep += mapping.get(inst.getOperationType());
				int opCode = Integer.parseInt(binaryRep, 2);
				// System.out.println(inst.getOperationType() + " " + mapping.get(inst.getOperationType()));
				// System.out.println(mapping);
				// System.out.println(inst.getProgramCounter());
				int pc = inst.getProgramCounter();
				
				if (opCode <= 20 && opCode % 2 == 0) {
					// R3 Type
					binaryRep += convert(inst.getSourceOperand1(), 5);
					binaryRep += convert(inst.getSourceOperand2(), 5);
					binaryRep += convert(inst.getDestinationOperand(), 5);
					binaryRep += toBinaryOfSpecificPrecision(0, 12);
				}
				else if (opCode == 24) {
					// RI Type
					if (inst.destinationOperand.getOperandType() == Operand.OperandType.Register) {
						binaryRep += convert(inst.getDestinationOperand(), 5);
						binaryRep += toBinaryOfSpecificPrecision(0, 22);
					}
					else {						
						binaryRep += toBinaryOfSpecificPrecision(0, 5);
						int value = Integer.parseInt(convert(inst.getDestinationOperand(), 5), 2) - pc;
						String bin = toBinaryOfSpecificPrecision(value, 22);
						binaryRep += bin.substring(bin.length() - 22);
					}
				}
				else if (opCode == 29) {
					binaryRep += toBinaryOfSpecificPrecision(0, 27);
				}
				else {
					// R2I Type
					if (opCode >= 25 && opCode <= 28) {
						int value = Integer.parseInt(convert(inst.getDestinationOperand(), 5), 2) - pc;
						binaryRep += convert(inst.getSourceOperand1(), 5);
						binaryRep += convert(inst.getSourceOperand2(), 5);
						String bin = toBinaryOfSpecificPrecision(value, 17);
						binaryRep += bin.substring(bin.length() - 17);
					}
					else {						
						binaryRep += convert(inst.getSourceOperand1(), 5);
						binaryRep += convert(inst.getDestinationOperand(), 5);
						binaryRep += convert(inst.getSourceOperand2(), 17);
					}
				}
				int instInteger = (int) Long.parseLong(binaryRep, 2);
				byte[] instBinary = ByteBuffer.allocate(4).putInt(instInteger).array();
				bfile.write(instBinary);

				// System.out.println(instInteger);
				// if (inst.getSourceOperand1() != null)
				// 	file.write(convert(inst.getSourceOperand1()));
				// if (inst.getSourceOperand2() != null)
				// 	file.write(convert(inst.getSourceOperand2()));
				// if (inst.getDestinationOperand() != null)
				// 	file.write(convert(inst.getDestinationOperand()));
				// file.write(inst.toString());
			}

			//5. close the file
			bfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}

