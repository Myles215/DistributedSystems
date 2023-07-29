import java.util.ArrayList;
import java.util.Collections;

import java.rmi.*;  
import java.rmi.server.*;  

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator
{

    CalculatorImplementation() throws RemoteException
    {  
        super();  
    }  

    public void pushValue(int val) 
    {
        stack.add(val);
    }

    public void pushOperation(String operator) 
    {
        ArrayList<Integer> pass = stack;
        stack.clear();

        if (operator == "min")
        {
            pushValue(Collections.min(pass));
        }
        else if (operator == "max")
        {
            pushValue(Collections.max(pass));
        }
        else if (operator == "gcd")
        {
            pushValue(gcd(pass));
        }
        else if (operator == "lcm")
        {
            pushValue(lcm(pass));
        }
    }


    private int gcd(ArrayList<Integer> a)
    {

        int start = 0;

        for (int i = 0;i<a.size();i++)
        {
            start = start == 0 ? Math.abs(a.get(i)) : Math.min(start, Math.abs(a.get(i)));
        }

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

        return 1;
    }

    private int lcm(ArrayList<Integer> a)
    {

        int lcm = -1;
        int start = 0;

        for (int i = 0;i<a.size();i++)
        {
            start = start == 0 ? Math.abs(a.get(i)) : Math.max(start, Math.abs(a.get(i)));
        }

        int count = 0;

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
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() < start + millis);

        return pop();

    }
}