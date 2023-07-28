import java.rmi.*;  
import java.util.Scanner;

public class CalculatorClient 
{  
    public static void main(String args[]){  
        try
        {  
            Calculator calc = (Calculator)Naming.lookup("rmi://localhost:5000/calculatorServer");  

            String input = "";
            Scanner scan = new Scanner(System.in);

            while (input != "end")
            {
                input = scan.nextLine();

                if (input == "pop")
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

                    while (i < input.length()) num += input.indexOf(i++);

                    int n = Integer.parseInt(num);
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
