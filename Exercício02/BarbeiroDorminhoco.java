package Exercicio02;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.*;

class BarbeiroDorminhoco {
    private final int capacidadeCadeiras;
    private final Queue<Integer> filaClientes;
    private boolean barbeiroDormindo;

    private final Lock lock;
    private final Condition barbeiroAcordado;
    private final Condition clienteAtendido;

    public BarbeiroDorminhoco(int capacidadeCadeiras) {
        this.capacidadeCadeiras = capacidadeCadeiras;
        this.filaClientes = new LinkedList<>();
        this.barbeiroDormindo = true;
        this.lock = new ReentrantLock();
        this.barbeiroAcordado = lock.newCondition();
        this.clienteAtendido = lock.newCondition();
    }

    public void cortarCabelo() throws InterruptedException {
        lock.lock();
        try {
            while (filaClientes.isEmpty()) {
                System.out.println("\n Barbeiro dormindo... \n");
                barbeiroDormindo = true;
                barbeiroAcordado.await();
            }
            int clienteAtual = filaClientes.poll();
            System.out.println("Barbeiro cortando cabelo de cliente " + clienteAtual + "...");
            Thread.sleep(2000); // Simulando o corte de cabelo
            System.out.println("Corte de cabelo do cliente " + clienteAtual + " finalizado.");
            clienteAtendido.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void clienteChega(int id) throws InterruptedException {
        lock.lock();
        try {
            if (filaClientes.size() == capacidadeCadeiras) {
                System.out.println("Barbearia cheia, cliente " + id + " saindo...");
                return;
            }
            filaClientes.add(id);
            if (barbeiroDormindo) {
                System.out.println("Barbeiro acordado pelo cliente " + id + "...");
                barbeiroDormindo = false;
                barbeiroAcordado.signal();
            } else {
                System.out.println("Cliente " + id + " sentou...");
            }
            clienteAtendido.await();
        } finally {
            lock.unlock();
        }
    }

    public void trabalhar() {
        while (true) {
            try {
                cortarCabelo();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
