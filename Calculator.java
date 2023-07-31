
import java.util.ArrayList;
import java.rmi.*; 

//Remote is an RMI class
interface Calculator extends Remote
{
    //Our global stack
    ArrayList<Integer> stack = new ArrayList<Integer>();

    //Each of our interface methods
    void pushValue(int val) throws RemoteException;  
    void pushOperation(String operator) throws RemoteException;  
    int pop() throws RemoteException;  
    boolean isEmpty() throws RemoteException;  
    int delayPop(int millis) throws RemoteException;  
}