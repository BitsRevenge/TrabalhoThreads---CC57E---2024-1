import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {
    static List<Thread> filaProcessos = new ArrayList<Thread>();

    public static void main(String[] args) {
        ContaBancaria conta1 = new ContaBancaria(1000);
        ContaBancaria conta2 = new ContaBancaria(500);

        Thread transferencia = new Thread(new Transferencia(conta1, conta2, 200), "Transferencia");
        Thread deposito = new Thread(new Deposito(conta1, 300), "Deposito");
        Thread saque = new Thread(new Saque(conta1, 100), "Saque");
        Thread creditoJuros = new Thread(new CreditoJuros(conta1, 0.05), "CreditoJuros");

        transferencia.start();
        deposito.start();
        saque.start();
        creditoJuros.start();

        try {
            transferencia.join();
            deposito.join();
            saque.join();
            creditoJuros.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Saldo final conta1: " + conta1);
        System.out.println("Saldo final conta2: " + conta2);
    }
}
