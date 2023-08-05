import java.util.ArrayList;
import java.util.Collections;

import java.rmi.*;  
import java.rmi.server.*;  

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator
{

    //using volatile makes this variable threadsafe
    static volatile int idCount = 0;

    CalculatorImplementation() throws RemoteException
    {  
        super();  
    }  

    //On connect we register a new client ID
    public int onConnect()
    {
        stack.add(new ArrayList<Integer>());
        return idCount++;
    }

    //Push a vlue onto this clients stack
    public void pushValue(int val, int id) 
    {
        stack.get(id).add(val);
    }

    //Push an operation onto this clients stack
    public void pushOperation(String operator, int id) 
    {
        int top;

        if (operator.equals("min"))
        {
            top = Collections.min(stack.get(id));
            stack.get(id).clear();
            pushValue(top, id);
        }
        else if (operator.equals("max"))
        {
            top = Collections.max(stack.get(id));
            stack.get(id).clear();
            pushValue(top, id);
        }
        else if (operator.equals("gcd"))
        {
            top = gcd(stack.get(id));
            stack.get(id).clear();
            pushValue(top, id);
        }
        else if (operator.equals("lcm"))
        {
            top = lcm(stack.get(id));
            stack.get(id).clear();
            pushValue(top, id);
        }
    }

    private int gcd(ArrayList<Integer> a)
    {
        int start = Math.abs(a.get(0));

        //Start with the smallest |element| as it is guaranteed GCD must be smaller than or equal to this
        for (int i = 1;i<a.size();i++)
        {
            start = Math.min(start, Math.abs(a.get(i)));
        }

        //Iterator from smallest element to 1
        for (int i = start; i > 1 ; i--)
        {
            boolean newDenominator = true;

            for (int j = 0;j<a.size();j++)
            {
                if (a.get(j) % i != 0)
                {
                    newDenominator = false;
                    break;
                }
            }

            if (newDenominator)
            {
                return i;
            }
        }

        //If we don't find a higher valid gcd, return 1
        return 1;
    }

    private int lcm(ArrayList<Integer> a)
    {

        int lcm = -1;
        int start = 0;

        //LCM is guaranteed to be greater than or equal to the largest element
        for (int i = 0;i<a.size();i++)
        {
            start = start == 0 ? Math.abs(a.get(i)) : Math.max(start, Math.abs(a.get(i)));
        }

        int count = 0;

        //Different to gcd we can't just return 1 if we fail, we must return something
        while (lcm == -1)
        {
            boolean newLcm = true;
            count++;

            for (int j = 0;j<a.size();j++)
            {
                if ((start*count) % a.get(j) != 0)
                {
                    newLcm = false;
                    break;
                }
            }

            if (newLcm)
            {
                lcm = start*count;
            }
        }

        return lcm;
    }

    //Return top element then remove it
    public int pop(int id) 
    {

        //if (isEmpty()) return -1;

        int index = stack.get(id).size()-1;
        int ret = stack.get(id).get(index);
        stack.get(id).remove(index);

        return ret;
    }

    //Check if stack is empty
    public boolean isEmpty(int id) 
    {
        return stack.get(id).size() == 0;
    }

    //Busy wait until we can pop
    public int delayPop(int millis,int id) {
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() < start + millis);

        return pop(id);

    }
}