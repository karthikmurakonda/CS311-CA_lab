package processor.memorysystem;

import generic.*;
import processor.*;
import configuration.Configuration;

public class Cache implements Element {
    boolean isPresent = true;
    public int latency;
    Processor containingProcessor;
    int cache_size, miss_addr, line_size;
    CacheLine[] cach;
    int[] index;

    public Cache(Processor containingProcessor, int latency, int cacheSize) {
        
        this.latency = latency;
        this.cache_size = cacheSize;
        this.line_size = (int) (Math.log(this.cache_size / 8) / Math.log(2));
        this.containingProcessor = containingProcessor;
        this.cach = new CacheLine[cache_size / 8];
        for (int i = 0; i < cache_size / 8; i++)
            this.cach[i] = new CacheLine();

    }

    private int[] getindextag(int addr){
        String a = Integer.toBinaryString(addr);
        for (int i = 0; i < 32 - a.length(); i++)
            a = "0" + a;
        int add_tag = Integer.parseInt(a.substring(0, a.length() - line_size), 2);
        int temp_ind;
        String ind = "0";
        if (line_size == 0)
            temp_ind = 0;
        else
            for (int i = 0; i < line_size; i++)
                ind = ind + "1";
            temp_ind = addr & Integer.parseInt(ind, 2);
        return new int[]{temp_ind, add_tag};
    }

    public int cacheRead(int address) {
        int index, tag;
        int[] temp = getindextag(address);
        index = temp[0];
        tag = temp[1];

        if (tag == cach[index].tag[0]) {
            cach[index].least_recently_used = 1;
            isPresent = true;
            return cach[index].data[0];
        } else if (tag == cach[index].tag[1]) {
            cach[index].least_recently_used = 0;
            isPresent = true;
            return cach[index].data[1];
        } else {
            isPresent = false;
            return -1;
        }
    }

    public void WritetoCache(int address, int value) {
        int index, tag;
        int[] temp = getindextag(address);
        index = temp[0];
        tag = temp[1];

        cach[index].setValue(tag, value);

    }

    @Override
    public void handleEvent(Event source_event) {

        if (source_event.getEventType() == Event.EventType.MemoryRead) {
            System.out.println("handle event cache memory read");
            MemoryReadEvent handle_event = (MemoryReadEvent) source_event;
            int data = cacheRead(handle_event.getAddressToReadFrom());
            if (isPresent == true) {
                Simulator.getEventQueue().addEvent(
                        new MemoryResponseEvent(
                                Clock.getCurrentTime() + this.latency,
                                this,
                                handle_event.getRequestingElement(),
                                data));
            } else {
                handle_event.setEventTime(Clock.getCurrentTime() + Configuration.mainMemoryLatency + 1);
                this.miss_addr = handle_event.getAddressToReadFrom();
                Simulator.getEventQueue().addEvent(handle_event);
                Simulator.getEventQueue().addEvent(
                new MemoryReadEvent(
                        Clock.getCurrentTime() + Configuration.mainMemoryLatency,
                        this,
                        containingProcessor.getMainMemory(),
                        this.miss_addr)
                );
            }
        }
        else if (source_event.getEventType() == Event.EventType.MemoryWrite) {
            MemoryWriteEvent handle_event = (MemoryWriteEvent) source_event;
            WritetoCache(handle_event.getAddressToWriteTo(), handle_event.getValue());

            containingProcessor.getMainMemory().setWord(handle_event.getAddressToWriteTo(), handle_event.getValue());

            Simulator.getEventQueue().addEvent(
                    new ExecutionCompleteEvent(
                            Clock.getCurrentTime() + Configuration.mainMemoryLatency,
                            containingProcessor.getMainMemory(),
                            handle_event.getRequestingElement()));

        }
        else if (source_event.getEventType() == Event.EventType.MemoryResponse) {
            MemoryResponseEvent handle_event = (MemoryResponseEvent) source_event;
            WritetoCache(this.miss_addr, handle_event.getValue());
        }
    }

}