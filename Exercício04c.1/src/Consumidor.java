import java.util.LinkedList;
import java.util.Queue;

class Consumidor extends Thread {
    private BufferMonitor buffer;

    public Consumidor(BufferMonitor buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                buffer.consumir();
                sleep(150); // Simular tempo de consumo
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
