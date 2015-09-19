package sa.iff.minatentlocator.GraphUtil;

/**
 * Created by Mukhtar on 9/19/2015.
 */
public class Semaphore {

    private int value;

    public Semaphore (int init) {
        if (init < 0)
            init = 0;
        value = init;
    }

    public synchronized void down() {
        while (value == 0)
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        value--;
    }

    public synchronized void up() {
        value++;
        notify();
    }
}
