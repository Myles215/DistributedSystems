import java.rmi.*;  
import java.rmi.registry.*;  
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CalculatorClient 
{  

    public static void main(String args[]){  
        try
        {  

            Registry registry = LocateRegistry.getRegistry();
            Calculator calc = (Calculator) registry.lookup("Calc");

            int id = calc.onConnect();

            //Input instructions
            //Assignment delaration states we don't need to handle bad input
            System.out.println("Input 'end' to exit");
            System.out.println("Commands");
            System.out.println("'isEmpty' - Check if stack is empty");
            System.out.println("'pushValue x' - push x onto stack");
            System.out.println("'pop' - pop top value from stack, throws error if no items are in stack");
            System.out.println("'pushOperation [min, max, gcd, lcm]' - push operation and have operator ouput placed onto stack");
            System.out.println("'end' - quit");

            String input = "";

            BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));

            //While the user doesn't type 'end' keep taking input
            while (input != "end")
            {
                System.out.println("Input:");
                input = reader.readLine();

                if (input.equals("pop"))
                {
                    System.out.println("Popped: " + calc.pop(id));
                }
                else if (input.equals("isEmpty"))
                {
                    System.out.println(calc.isEmpty(id));
                }
                else if (input.contains("pushValue"))
                {
                    int i = 10;
                    String num = "";

                    while (i < input.length()) num += input.charAt(i++);

                    Integer n = Integer.parseInt(num);

                    System.out.println("Pushing integer: " + n);

                    calc.pushValue(n, id);
                }
                else if (input.contains("pushOperation"))
                {
                    int i = 14;
                    String op = "";

                    while (i < input.length()) op += input.charAt(i++);

                    System.out.println("Pushing operator: " + op);

                    calc.pushOperation(op, id);
                }
                else 
                {
                    System.out.println("Invalid input");
                }

            }

            System.exit(0);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }  
    }  

}  
