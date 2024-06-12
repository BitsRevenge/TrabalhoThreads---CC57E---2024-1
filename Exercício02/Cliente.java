package Exercicio02;


class Cliente implements Runnable {
    private final BarbeiroDorminhoco barbeiro;
    private final int id;

    public Cliente(BarbeiroDorminhoco barbeiro, int id) {
        this.barbeiro = barbeiro;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            barbeiro.clienteChega(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}