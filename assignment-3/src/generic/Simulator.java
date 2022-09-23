package generic;

import java.io.FileInputStream;
import java.io.InputStream;

import processor.Clock;
import processor.Processor;

public class Simulator {
		
	static Processor processor;
	static boolean simulationComplete;
	
	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);
		
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile)
	{
		/*
		 * 1. load the program into memory according to the program layout described
		 *    in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 *     x0 = 0
		 *     x1 = 65535
		 *     x2 = 65535
		 */

		try (
			InputStream is = new FileInputStream(assemblyProgramFile);
		){
			int i = 0;
			byte[] line = new byte[4];
			boolean isFirstLine = true;
			while(is.read(line) != -1) {
				int value = 0;
				for(int j = 0; j < 4; j++) {
					value = (value << 8) | (line[j] & 0xff);
				}
				System.out.println(value);
				if(isFirstLine) {
					processor.getRegisterFile().setProgramCounter(value);
					isFirstLine = false;
				}else{
					processor.getMainMemory().setWord(i, value);
					i++;
				}
			}
			processor.getRegisterFile().setValue(0, 0);
			processor.getRegisterFile().setValue(1, 65535);
			processor.getRegisterFile().setValue(2, 65535);

			// Debug
			System.out.println(processor.getRegisterFile().getProgramCounter());
			System.out.println(processor.getMainMemory().getContentsAsString(0, 10));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void simulate()
	{
		while(simulationComplete == false)
		{
			processor.getIFUnit().performIF();
			Clock.incrementClock();
			processor.getOFUnit().performOF();
			Clock.incrementClock();
			processor.getEXUnit().performEX();
			Clock.incrementClock();
			processor.getMAUnit().performMA();
			Clock.incrementClock();
			processor.getRWUnit().performRW();
			Clock.incrementClock();
			Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() + 1);
			Statistics.setNumberOfCycles(Statistics.getNumberOfCycles() + 1);
		}
		
		// TODO
		// set statistics
	}
	
	public static void setSimulationComplete(boolean value)
	{
		simulationComplete = value;
	}
}
