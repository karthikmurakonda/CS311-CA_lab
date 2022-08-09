public class Cell {
    private double probability;
    private boolean cellState;
    
    Cell(double probability) {
        this.probability = probability;
    }

    public void setSensorState() {
        double random = Math.random();
        if (random <= probability) {
            cellState = true;
        } else {
            cellState = false;
        }
    }

    public boolean getSensorState() {
        return cellState;
    }
}
