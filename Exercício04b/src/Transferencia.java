import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Transferencia implements Runnable {
    private ContaBancaria origem;
    private ContaBancaria destino;
    private double quantia;

    public Transferencia(ContaBancaria origem, ContaBancaria destino, double quantia) {
        this.origem = origem;
        this.destino = destino;
        this.quantia = quantia;
    }

    @Override
    public void run() {
        origem.transferir(destino, quantia);
    }
}