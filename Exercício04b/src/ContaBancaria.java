import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ContaBancaria {
    private double saldo;
    private Lock lock = new ReentrantLock();

    public ContaBancaria(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    public void depositar(double quantia) {
        // *----------- Parte Crítica: Lock -----------*
        lock.lock();
        try {
            saldo += quantia;
            System.out.println(Thread.currentThread().getName() + " depositou: " + quantia + " | Saldo atual: " + saldo);
        } finally {
            lock.unlock();
        // *----------- Fim da parte Crítica: Lock -----------*
        }
    }

    public void sacar(double quantia) {
        lock.lock();
        try {
            if (saldo >= quantia) {
                saldo -= quantia;
                System.out.println(Thread.currentThread().getName() + " sacou: " + quantia + " | Saldo atual: " + saldo);
            } else {
                System.out.println(Thread.currentThread().getName() + " tentou sacar: " + quantia + " | Saldo insuficiente.");
            }
        } finally {
            lock.unlock();
        }
    }

    public void transferir(ContaBancaria contaDestino, double quantia) {
        lock.lock();
        try {
            if (saldo >= quantia) {
                this.saldo -= quantia;
                contaDestino.depositar(quantia);
                System.out.println(Thread.currentThread().getName() + " transferiu: " + quantia + " para " + contaDestino);
            } else {
                System.out.println(Thread.currentThread().getName() + " tentou transferir: " + quantia + " | Saldo insuficiente.");
            }
        } finally {
            lock.unlock();
        }
    }

    public void creditarJuros(double taxa) {
        lock.lock();
        try {
            saldo += saldo * taxa;
            System.out.println(Thread.currentThread().getName() + " creditou juros: " + (saldo * taxa) + " | Saldo atual: " + saldo);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "ContaBancaria com saldo: " + saldo;
    }
}