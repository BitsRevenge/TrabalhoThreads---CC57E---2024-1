import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) {
        int quantRoletas = 15;
        Runnable runable = new Runnable() {

            @Override
            public void run() {
                System.out.println("");
            }
        };

        CyclicBarrier limite = new CyclicBarrier(quantRoletas, runable);

        for (int i = 0; i < quantRoletas; i++) {
            new Thread(new Roleta(limite, i + 1, 2)).start();
        }

    }
}