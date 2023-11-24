import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        System.out.println("What is the number of devices Clients want to connect?");
        TC = in.nextInt();
        router.setMaxSize(nConnections);
        TCLines = new ArrayList<>(TC);
        for(int i = 0; i < TC; i++)
        {
            TCLines.add(new Device(router));
            TCLines.get(i).setdName(in.next());
            TCLines.get(i).setdType(in.next());
        }
        for (Device device: TCLines)
        {
            device.start();
        }
    }

}
class Router {
    private int maxSize;
    private int inptr;
    private int outptr;
    public Boolean connections[];
    Semaphore semaphore;
    public Router()
    {
    }
    public int occupy(Device device) throws IOException {
        semaphore.P(device);
        int temp = 0;
        for(int i = 0; i < maxSize; i++)
        {
            if(!connections[i])
            {
                connections[i] = true;
                temp = i;
                break;
            }
        }
        String out = "- Connection " + (temp+1) + ": " + device.getdName() +" Occupied";
        FileWriter output = new FileWriter(new File("Output.txt"), true);
        output.write(out + '\n');
        output.close();
        return temp;
    }
    public void release(int i)
    {
        connections[i]= false;
        semaphore.V();
    }
    public void setMaxSize(int s)
    {
        this.maxSize = s;
        connections = new Boolean[maxSize];
        Arrays.fill(connections, Boolean.FALSE);
        semaphore = new Semaphore(maxSize);
    }
}
class Semaphore {
    protected int value;
    protected Semaphore(int initial)
    {
        value = initial ;
    }
    public synchronized void P(Device device) throws IOException {
        value-- ;
        if (value < 0){
            try {
                String out = device.getdName() + " (" + device.getdType() + ")  arrived and waiting";
                File outfile = new File("Output.txt");
                FileWriter output = new FileWriter(outfile, true);
                output.write(out + '\n');
                output.close();
                wait();
            }
            catch( InterruptedException e )
            {

            }
        }
        else
        {
            String out = device.getdName() + " (" + device.getdType() + ")  arrived";
            FileWriter output = new FileWriter(new File("Output.txt"), true);
            output.write(out + '\n');
            output.close();

        }
    }
    public synchronized void V() {
        value++;
        if (value <= 0)
        {
            notify();
        }
    }
}
class Device extends Thread{
    Semaphore semaphore = new Semaphore(Network.nConnections);
    Router router;
    private String dName;
    private String dType;
    public Device(Router router)
    {
        this.router = router;
    }
    public void login(int pos) throws IOException {
        String out = "- Connection " + pos + ": " + dName + " login";
        FileWriter output = new FileWriter(new File("Output.txt"), true);
        output.write(out + '\n');
        output.close();
    }
    public void performOnlineActivity(int pos) throws IOException {
        String out = "- Connection " + pos + ": " + this.dName + " perform online activity";
        FileWriter output = new FileWriter(new File("Output.txt"), true);
        output.write(out + '\n');
        output.close();
    }
    public void logOut(int pos) throws IOException {
        String out = "- Connection " + pos + ": " + this.dName + " logout";
        FileWriter output = new FileWriter(new File("Output.txt"), true);
        output.write(out + '\n');
        output.close();
    }
    public void setdName(String name){this.dName = name;}
    public void setdType(String type){this.dType = type;}
    public String getdName(){return dName;}
    public String getdType(){return dType;}
    public void run()
    {
        File output = new File("Output.txt");
        output.delete();
        int position = 0;
        try {
            position = router.occupy(this);
            sleep(1000);
            login(position+1 );
            performOnlineActivity(position + 1);
            logOut(position + 1);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        router.release(position);
    }
}
