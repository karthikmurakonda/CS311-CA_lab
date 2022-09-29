package processor.pipeline;

import generic.Simulator;
import generic.Instruction;
import generic.Instruction.OperationType;
import processor.Processor;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public void performRW()
	{
		if(MA_RW_Latch.isRW_enable())
		{
			Instruction instruction = MA_RW_Latch.getInstruction();
			OperationType op_type = instruction.getOperationType();
			int alu_result = MA_RW_Latch.getALU_result();

			if (op_type==OperationType.load)
			{
				int load_result = MA_RW_Latch.getLoad_result();
				int rd = instruction.getDestinationOperand().getValue();
				containingProcessor.getRegisterFile().setValue(rd, load_result);
			}
			else if (op_type==OperationType.end)
			{
				Simulator.setSimulationComplete(true);
			}
			else
			{
				if (op_type!=OperationType.store && op_type!= OperationType.jmp && op_type!= OperationType.beq && op_type!=OperationType.bne && op_type!=OperationType.blt && op_type!=OperationType.bgt)
				{
					int rd = instruction.getDestinationOperand().getValue();
					rd = instruction.getDestinationOperand().getValue();
					containingProcessor.getRegisterFile().setValue(rd, alu_result);
				}
			}
			MA_RW_Latch.setRW_enable(false);
			IF_EnableLatch.setIF_enable(true);
		}
	}

}
