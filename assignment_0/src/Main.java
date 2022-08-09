class Main {
    static int maxtime = 10000000;
    static boolean isInfinite = false;
    public static double average(int[] timeTaken) {
        int sum = 0;
        for (double currentTime : timeTaken)
            sum += currentTime;
        return (double)sum / timeTaken.length;
    }

    public static void main(String[] args) {

        int numOfIterations = 50;
        int[] timeTaken = new int[numOfIterations];

        double probability = Double.parseDouble(args[0]);
        int width = Integer.parseInt(args[1]);
        for (int i = 0; i < numOfIterations; i++) {

            Cell cellUp = new Cell(probability);
            Cell cellRightUp = new Cell(probability);
            Cell cellLeftUp = new Cell(probability);
            Cell cellPresent = new Cell(probability);

            Infiltrator infiltrator = new Infiltrator();
            Clock clock = new Clock();
            cellUp.setSensorState();
            cellRightUp.setSensorState();
            cellLeftUp.setSensorState();
            while(cellLeftUp.getSensorState() && cellRightUp.getSensorState() && cellUp.getSensorState()) {
                cellLeftUp.setSensorState();
                cellRightUp.setSensorState();
                cellUp.setSensorState();
                clock.tick();
            }

            infiltrator.proceedForward();
            clock.tick();

            while (clock.getTime() < 1000000 && infiltrator.getLocation() < width) {
                cellUp.setSensorState();
                cellRightUp.setSensorState();
                cellLeftUp.setSensorState();
                cellPresent.setSensorState();
                if (!cellPresent.getSensorState()&&(!cellLeftUp.getSensorState()||!cellRightUp.getSensorState()||!cellUp.getSensorState())) {
                    infiltrator.proceedForward();
                }
                if (infiltrator.getLocation() == width) {
                    cellPresent.setSensorState();
                    while (cellPresent.getSensorState()) {
                        clock.tick();
                        cellPresent.setSensorState();
                    }
                    infiltrator.proceedForward();
                    break;
                }
                clock.tick();
            }
            if(clock.getTime() >= maxtime) {
                isInfinite = true;
            }
            timeTaken[i] = clock.getTime();
        }
        System.out.println(average(timeTaken));
    }
}