package processor.pipeline;

import configuration.Configuration;
import generic.Element;
import generic.Event;
import generic.ExecutionCompleteEvent;
import generic.Instruction;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.Simulator;
import processor.Clock;
import processor.Processor;
import generic.Instruction.OperationType;

public class MemoryAccess implements Element {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
	}
	
	public void performMA()
	{
		if(EX_MA_Latch.isMA_enable()&& !EX_MA_Latch.isMA_busy())
		{
			Instruction instruction = EX_MA_Latch.getInstruction();
			int alu_result = EX_MA_Latch.getALUResult();
			MA_RW_Latch.setALU_result(alu_result);
			OperationType op_type = instruction.getOperationType();
			MA_RW_Latch.setInstruction(instruction);
			MA_RW_Latch.setRW_enable(true);
			if (op_type==OperationType.store)
			{
				int val_store = containingProcessor.getRegisterFile().getValue(
				instruction.getSourceOperand1().getValue());
				// containingProcessor.getMainMemory().setWord(alu_result, val_store);
				Simulator.getEventQueue().addEvent(
					new MemoryWriteEvent(
						Clock.getCurrentTime()+Configuration.mainMemoryLatency,
						this,
						containingProcessor.getMainMemory(),
						alu_result,
						val_store
					)
				);
				EX_MA_Latch.setMA_busy(true);
				MA_RW_Latch.setRW_enable(false);
			}
			else if (op_type==OperationType.load)
			{
				// int load_result = containingProcessor.getMainMemory().getWord(alu_result);
				Simulator.getEventQueue().addEvent(
					new MemoryReadEvent(
						Clock.getCurrentTime()+Configuration.mainMemoryLatency,
						this,
						containingProcessor.getMainMemory(),
						alu_result
						)
				);
				EX_MA_Latch.setMA_busy(true);
				MA_RW_Latch.setRW_enable(false);
			}

		}
	}

	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		if(e instanceof ExecutionCompleteEvent) {
			// MemoryResponseEvent event = (MemoryResponseEvent) e;
			// MA_RW_Latch.setLoad_result(event.getValue());
			EX_MA_Latch.setMA_busy(false);
			MA_RW_Latch.setRW_enable(true);
			return;
		}
		MemoryResponseEvent event=(MemoryResponseEvent) e;
		MA_RW_Latch.setLoad_result(event.getValue());
		EX_MA_Latch.setMA_busy(false);
		MA_RW_Latch.setRW_enable(true);
	}

}
