import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class CreditoJuros implements Runnable {
    private ContaBancaria conta;
    private double taxa;

    public CreditoJuros(ContaBancaria conta, double taxa) {
        this.conta = conta;
        this.taxa = taxa;
    }

    @Override
    public void run() {
        conta.creditarJuros(taxa);
    }
}
