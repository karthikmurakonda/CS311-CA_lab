package processor.memorysystem;


public class CacheLine{
    int[] tag = new int[2]; 
    int[] data = new int[2];
    int least_recently_used = 0;

    public CacheLine() {
        this.tag[0] = -1;
        this.tag[1] = -1;
    }

    public void setValue(int tag, int value) {
        if(tag == this.tag[0]) {
            this.data[0] = value;
            this.least_recently_used = 1;
        }
        else if(tag == this.tag[1]) {
            this.data[1] = value;
            this.least_recently_used = 0;
        }
        else {
            this.tag[this.least_recently_used] = tag;
            this.data[this.least_recently_used] = value;
            this.least_recently_used = 1- this.least_recently_used;
        }
	}

}
