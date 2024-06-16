import java.util.LinkedList;
import java.util.Queue;

class Produtor extends Thread {
    private BufferMonitor buffer;

    public Produtor(BufferMonitor buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                buffer.produzir(i);
                sleep(100); // Simular tempo de produção
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}