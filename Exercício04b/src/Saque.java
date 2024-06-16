import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Saque implements Runnable {
    private ContaBancaria conta;
    private double quantia;

    public Saque(ContaBancaria conta, double quantia) {
        this.conta = conta;
        this.quantia = quantia;
    }

    @Override
    public void run() {
        conta.sacar(quantia);
    }
}