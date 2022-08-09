
public class Clock {
    private int time = 0;
    public int getTime() {
        return time;
    }

    public void tick() {
        time += 10;
    }
}
