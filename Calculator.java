
import java.util.ArrayList;
import java.rmi.*; 

interface Calculator extends Remote
{
    ArrayList<Integer> stack = new ArrayList<Integer>();

    void pushValue(int val) throws RemoteException;  
    void pushOperation(String operator) throws RemoteException;  
    int pop() throws RemoteException;  
    boolean isEmpty() throws RemoteException;  
    int delayPop(int millis) throws RemoteException;  
}