import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Deposito implements Runnable {
    private ContaBancaria conta;
    private double quantia;

    public Deposito(ContaBancaria conta, double quantia) {
        this.conta = conta;
        this.quantia = quantia;
    }

    @Override
    public void run() {
        conta.depositar(quantia);
    }
}