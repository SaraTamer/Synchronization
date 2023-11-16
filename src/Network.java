import java.util.*;

public class Network {
    static Router router = new Router();
    static int nConnections;
    static int TC;
    public static Map<String,String> TCLines;
    public static void main(String[] args) {
        System.out.println("What is the number of WI-FI Connections?");
        Scanner in = new Scanner(System.in);
        nConnections = in.nextInt();
        System.out.println("What is the number of devices Clients want to connect?\n");
        TC = in.nextInt();
        TCLines = new HashMap<>(TC);
        for(int i = 0; i < TC; i++)
        {
            TCLines.put(in.next(), in.next());
        }
    }
}


class Router {
    private int size;
    private ArrayList<Object> connections;
    private int inptr = 0;
    private int outptr = 0;
    private Semaphore empty;
    private Semaphore connected;
    public Router(){};
    public Router(int size){
        this.size= size;
        connections = new ArrayList<>(size);
        empty = new Semaphore(size);
        connected = new Semaphore(0);
    }
    public void occupy(Object value)
    {
        empty.P();
        connections.set(inptr, value);
        inptr = (inptr + 1) % size;
        connected.V();
    }
    public Object release()
    {
        Object value;
        connected.P();
        value = connections.get(outptr);
        outptr = (outptr + 1) % size;
        empty.V();
        return value;
    }
    public int getOutptr()
    {
        return outptr;
    }
}
class Semaphore {
    protected int value;
    protected Semaphore()
    {
        value = 0 ;
    }
    protected Semaphore(int initial)
    {
        value = initial ;
    }
    public synchronized void P() {

        value-- ;
        if (value < 0){
            try {
                wait() ;
            }
            catch( InterruptedException e )
            {
                System.out.println("Waiting");
            }
        }
        System.out.println("Occupied");
    }
    public synchronized void V() {
        value++;
        if (value <= 0)
        {
            notify();
            System.out.println("logout");
        }

    }
}
class Device extends Thread{
    Router router;
    public void connect()
    {
        for(int i = 0; i < Network.TCLines.size(); i++)
        {
            router.occupy(Network.TCLines.get(i));
        }
    }
    public void performOnlineActivity()
    {

    }
    public void logOut()
    {

    }
}

class Producer extends Thread {
    private Router router;
    public  Producer(Router router){this.router = router;}
    public void run()
    {
        for(int i = 0; i < Network.TCLines.size(); i++)
        {
            router.occupy(Network.TCLines.get(i));
        }
    }
}
class Consumer extends Thread{
    private Router router;
    public Consumer(Router router){this.router = router;}
    public void run()
    {
        for (int i = 0; i < Network.TCLines.size(); i++)
        {
            System.out.println(router.getOutptr());
            System.out.println(router.release());
        }
    }
}