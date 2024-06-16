import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


class Roleta implements Runnable {
    private CyclicBarrier limite;
    private int id;
    private int max;
    static Contador cont_geral = new Contador();
    Contador cont = new Contador();

    public Roleta(CyclicBarrier limite, int id, int max) {
        this.limite = limite;
        this.id = id;
        this.max = max;
    }

    public int retonarCont(){
        return  cont_geral.getContagem();
    }

    @Override
    public void run() {
        while (true) {
            try {

                int resultado = girarRoleta();
                if (cont.getContagem() == this.max) {
                    System.out.println("A Roleta " + id + " alcançou " + resultado);
                    System.out.println("Contador Geral: " + cont_geral.getContagem());
                    System.exit(11);
                } else {
                    System.out.println("A Roleta " + id + ":  " + resultado);
                    limite.await();
                }
                Thread.sleep(200);
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
                break;
            }

        }
    }

    private int girarRoleta() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Random gerador = new Random();
        int num = gerador.nextInt(2);
        if (num == 1 && cont.getContagem() < this.max) {
            cont.incrementa();
            cont_geral.incrementa();
        }
        return cont.getContagem();
    }
}