import java.util.LinkedList;
import java.util.Queue;

class BufferMonitor {
    private Queue<Integer> fila = new LinkedList<>();
    private int capacidade;

    public BufferMonitor(int capacidade) {
        this.capacidade = capacidade;
    }

    public synchronized void produzir(int item) throws InterruptedException {
        while (fila.size() == capacidade) {
            wait();
        }
        fila.add(item);
        System.out.println("Produzido: " + item);
        notifyAll();
    }

    public synchronized int consumir() throws InterruptedException {
        while (fila.isEmpty()) {
            wait();
        }
        int item = fila.poll();
        System.out.println("Consumido: " + item);
        notifyAll();
        return item;
    }
}