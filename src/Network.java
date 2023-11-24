import java.util.*;

public class Network {
    static Router router = new Router();
    static int nConnections;
    static int TC;
    public static ArrayList<Device> TCLines;
    public static void main(String[] args) {
        System.out.println("What is the number of WI-FI Connections?");
        Scanner in = new Scanner(System.in);
        nConnections = in.nextInt();
        System.out.println("What is the number of devices Clients want to connect?\n");
        TC = in.nextInt();
        TCLines = new ArrayList<>(TC);
        for(int i = 0; i < TC; i++)
        {
            TCLines.add(new Device());
            TCLines.get(i).setdName(in.next());
            TCLines.get(i).setdType(in.next());
        }
        router.setMaxSize(nConnections);
        for (Device device: TCLines)
        {
            router.occupy(device);
        }
    }

}

class Router {
    private int maxSize;
    private int size;
    private int inptr;
    private int outptr;
    public ArrayList<Device> connections;
    public Router()
    {
        this.size = 0;
        connections = new ArrayList<>(maxSize);
        inptr = 0;
        outptr = 0;
    }

    public void occupy(Device device)
    {
       if(size < maxSize)
       {
           size++;
           connections.add(inptr, device);
           inptr = (inptr + 1) % maxSize;
           System.out.println(device.getdName() + " (" + device.getdType() + ")  arrived");
           device.connect();
       }
       else {
           System.out.println(device.getdName() + " (" + device.getdType() + ")  arrived and waiting");
           release(device);
       }
    }
    public void release(Device device)
    {
        connections.get(outptr).logOut();
        outptr = (outptr + 1) % maxSize;
        size--;
        occupy(device);
    }
    public void setMaxSize(int s){this.maxSize = s;}
}
class Semaphore {
    protected int value;
    protected Semaphore(int initial)
    {
        value = initial ;
    }
    public synchronized void P(int pos) {
        value-- ;
        if (value < 0){
            try {
                wait();
            }
            catch( InterruptedException e )
            {
                System.out.print(" ");
            }
        }
        System.out.println("Connection " + pos + ": " + Network.router.connections.get(pos).getdName() +" occupied");
    }
    public synchronized void V(int pos) {
        value++;
        if (value <= 0)
        {
            notify();
            System.out.println("Connection " + pos + ": " + Network.router.connections.get(pos).getdName() +" logged out");
        }
    }
}
class Device extends Thread{
    Semaphore semaphore = new Semaphore(Network.nConnections);
    private String dName;
    private String dType;
    public void connect()
    {
        int pos = 0;
        for(int i = 0; i < Network.router.connections.size(); i++)
        {
            if(Objects.equals(Network.router.connections.get(i).getdName(), this.dName))
                pos = i;
        }
        semaphore.P(pos);
        performOnlineActivity();
    }
    public void performOnlineActivity()
    {
        int pos = 0;
        for(int i = 0; i < Network.router.connections.size(); i++)
        {
            if(Objects.equals(Network.router.connections.get(i).getdName(), this.dName))
                pos = i;
        }
        System.out.println("Connection " + pos + ": " + this.dName + " perform online activity");
    }
    public void logOut()
    {
        int pos = 0;
        for(int i = 0; i < Network.router.connections.size(); i++)
        {
            if(Objects.equals(Network.router.connections.get(i).getdName(), this.dName))
                pos = i;
        }

        semaphore.V(pos);
    }
    public void setdName(String name){this.dName = name;}
    public void setdType(String type){this.dType = type;}
    public String getdName(){return dName;}
    public String getdType(){return dType;}

}
