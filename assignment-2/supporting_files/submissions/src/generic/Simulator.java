package generic;

import java.io.FileInputStream;
import generic.Operand.OperandType;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class Simulator {
		
	static FileInputStream inputcodeStream = null;

	public static Map<String, String> map = new HashMap<>() {
		{
			put("add", "00000");
			put("sub", "00010");
			put("mul", "00100");
			put("div", "00110");
			put("and", "01000");
			put("or", "01010");
			put("xor", "01100");
			put("slt", "01110");
			put("sll", "10000");
			put("srl", "10010");
			put("sra", "10100");
			put("beq", "11001");
			put("bne", "11010");
			put("blt", "11011");
			put("bgt", "11100");
			put("load", "10110");
			put("store", "10111");
			put("jmp", "11000");
			put("addi", "00001");
			put("subi", "00011");
			put("muli", "00101");
			put("divi", "00111");
			put("andi", "01001");
			put("ori", "01011");
			put("xori", "01101");
			put("slti", "01111");
			put("slli", "10001");
			put("srli", "10011");
			put("srai", "10101");
			put("end", "11101");

		}
	};
	private static String toBinaryOfSpecificPrecision(int num, int lenOfTargetString) {
		String binary = String.format("%" + lenOfTargetString + "s", Integer.toBinaryString(num)).replace(' ', '0');
		return binary;
	}
	private static String convert_1(Operand inst, int precision) {
		
		return toBinaryOfSpecificPrecision(inst.getValue(), precision);
		
	}
	private static String convert(Operand inst, int precision) {
		if (inst == null)
			return toBinaryOfSpecificPrecision(0, precision);

		if (inst.getOperandType() == Operand.OperandType.Label)
			return toBinaryOfSpecificPrecision(ParsedProgram.symtab.get(inst.getLabelValue()), precision);

		return toBinaryOfSpecificPrecision(inst.getValue(), precision);
		
	}
	public static void setupSimulation(String assemblyProgramFile)
	{	
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}
	
	public static void assemble(String objectProgramFile)
	{
		
		FileOutputStream file;
		try {
			file = new FileOutputStream(objectProgramFile);
			//write the firstCodeAddress to the file
			file.write(ByteBuffer.allocate(4).putInt(ParsedProgram.firstCodeAddress).array());
			//write the data to the file
			for (var value : ParsedProgram.data) {
				byte[] dataValue = ByteBuffer.allocate(4).putInt(value).array();
				file.write(dataValue);
			}
			//assemble one instruction at a time, and write to the file
			for (var instruction : ParsedProgram.code) {
				String line = "";
				String optype=(instruction.operationType).toString();
				line += map.get(optype);
				// int op = Integer.parseInt(line, 2);
				int op=Integer.parseInt(map.get(optype),2);
				int pc = instruction.getProgramCounter();
				
				if (op <= 20 && op % 2 == 0) {
					// R3 Type
					line += convert_1(instruction.getSourceOperand1(), 5);
					line += convert_1(instruction.getSourceOperand2(), 5);
					line += convert_1(instruction.getDestinationOperand(), 5);
					line += toBinaryOfSpecificPrecision(0, 12);
					
				}
				else if (op == 29) {
					line += toBinaryOfSpecificPrecision(0, 27);
				}
				else if (op == 24) {
					// RI Type
					if (instruction.destinationOperand.getOperandType() == Operand.OperandType.Register) {
						line += convert(instruction.getDestinationOperand(), 5);
						line += toBinaryOfSpecificPrecision(0, 22);
					} else {
						line += toBinaryOfSpecificPrecision(0, 5);
						int value = Integer.parseInt(convert(instruction.getDestinationOperand(), 5), 2) - pc;
						String bin = toBinaryOfSpecificPrecision(value, 22);
						line += bin.substring(bin.length() - 22);
					}
				}
				else {
					// R2I Type
					if (op >= 25 && op <= 28) {
						int value = Integer.parseInt(convert(instruction.getDestinationOperand(), 5), 2) - pc;
						line += convert(instruction.getSourceOperand1(), 5);
						line += convert(instruction.getSourceOperand2(), 5);
						String bin = toBinaryOfSpecificPrecision(value, 17);
						line += bin.substring(bin.length() - 17);
					} else {
						line += convert(instruction.getSourceOperand1(), 5);
						line += convert(instruction.getDestinationOperand(), 5);
						line += convert(instruction.getSourceOperand2(), 17);
					}
				}
				int instInteger = (int) Long.parseLong(line, 2);
				byte[] instBinary = ByteBuffer.allocate(4).putInt(instInteger).array();
				file.write(instBinary);
			}
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
