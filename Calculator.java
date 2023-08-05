
import java.util.ArrayList;
import java.rmi.*; 

//Remote is an RMI class
interface Calculator extends Remote
{
    //Our stacks
    //Due to each client having it's own stack this is threadsafe
    ArrayList<ArrayList<Integer>> stack = new ArrayList<ArrayList<Integer>>();

    //Inidivual stack operator
    int onConnect() throws RemoteException;

    //Each of our interface methods
    //All take ID so that each client can have a different stack
    void pushValue(int val, int id) throws RemoteException;  
    void pushOperation(String operator, int id) throws RemoteException;  
    int pop(int id) throws RemoteException;  
    boolean isEmpty(int id) throws RemoteException;  
    int delayPop(int millis, int id) throws RemoteException, InterruptedException;  
}