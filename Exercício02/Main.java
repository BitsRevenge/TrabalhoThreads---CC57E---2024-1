package Exercicio02;

public class Main {
    public static void main(String[] args) {
        BarbeiroDorminhoco barbeiro = new BarbeiroDorminhoco(3); // 3 cadeiras na barbearia
        Thread barbeiroThread = new Thread(() -> {
            barbeiro.trabalhar();
        });
        barbeiroThread.start();

        //Start para cada um dos clientes, seu número definido por nClientes
        int nClientes = 10;
        for (int i = 0; i < nClientes; i++) {
            Thread clienteThread = new Thread(new Cliente(barbeiro, i + 1));
            clienteThread.start();
        }
    }
}