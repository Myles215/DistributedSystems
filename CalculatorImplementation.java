import java.util.ArrayList;

public class CalculatorImplementation extends Calculator
{

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
        int ret = stack.indexOf(index);
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