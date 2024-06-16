import java.util.LinkedList;
import java.util.Queue;

public class Main  {
    public static void main(String[] args) {
        BufferMonitor buffer = new BufferMonitor(5);
        Produtor produtor = new Produtor(buffer);
        Consumidor consumidor = new Consumidor(buffer);

        produtor.start();
        consumidor.start();
    }
}