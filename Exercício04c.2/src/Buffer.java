import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

class Buffer {
    private Queue<Integer> fila = new LinkedList<>();
    private int capacidade;
    private Semaphore mutex = new Semaphore(1);
    private Semaphore itensDisponiveis = new Semaphore(0);
    private Semaphore espacosDisponiveis;

    public Buffer(int capacidade) {
        this.capacidade = capacidade;
        this.espacosDisponiveis = new Semaphore(capacidade);
    }

    public void produzir(int item) throws InterruptedException {
        espacosDisponiveis.acquire();
        mutex.acquire();
        try {
            fila.add(item);
            System.out.println("Produzido: " + item);
        } finally {
            mutex.release();
            itensDisponiveis.release();
        }
    }

    public int consumir() throws InterruptedException {
        itensDisponiveis.acquire();
        mutex.acquire();
        try {
            int item = fila.poll();
            System.out.println("Consumido: " + item);
            return item;
        } finally {
            mutex.release();
            espacosDisponiveis.release();
        }
    }
}