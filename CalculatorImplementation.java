import java.util.ArrayList;
import java.util.Collections;

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
        for (int i = Collections.min(a); i > 1 ; i--)
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
        int add;
        int check = add = Collections.max(a);

        while (lcm == -1)
        {
            boolean newLcm = true;

            for (int j = 0;j<a.size();j++)
            {
                if (check % a.get(j) != 0)
                {
                    newLcm = false;
                    break;
                }
            }

            if (newLcm)
            {
                lcm = check;
            }

            check += add;
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
        return 0;
    }
}