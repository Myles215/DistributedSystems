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

            System.out.println("Wooorking");

            String input = "";

            BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));

            while (input != "end")
            {
                System.out.println("Input:");
                input = reader.readLine();

                if (input.contains("pop"))
                {
                    System.out.println(calc.pop());
                }
                else if (input == "isEmpty")
                {
                    System.out.println(calc.isEmpty());
                }
                else if (input.contains("push"))
                {
                    int i = 5;
                    String num = "";

                    while (i < input.length()) num += input.charAt(i++);

                    Integer n = Integer.parseInt(num);

                    System.out.println("Pushing integer: " + n);

                    calc.pushValue(n);
                }
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
        }  
    }  

}  
