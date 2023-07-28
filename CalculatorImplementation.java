import java.util.ArrayList;
import java.rmi.*;  
import java.rmi.server.*;  

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator
{

    CalculatorImplementation() throws RemoteException{  
        super();  
    }  

    public void pushValue(int val) 
    {
        stack.add(val);
    }

    public void pushOperation(String operator) 
    {

    }

    private int gcd(ArrayList<Integer> a)
    {
        return 0;
    }

    public int pop() 
    {
        int index = stack.size()-1;
        int ret = stack.get(index);
        stack.remove(index);

        return ret;
    }

    public boolean isEmpty() 
    {
        return stack.size() == 0;
    }

    public int delayPop(int millis) {
        return 0;
    }
}