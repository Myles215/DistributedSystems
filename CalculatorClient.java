import java.rmi.*;  
import java.util.Scanner;

public class MyClient{  
    public static void main(String args[]){  
        try
        {  
            CalculatorImplementation calc = (CalculatorImplementation)Naming.lookup("rmi://localhost:5000/calculatorServer");  

            String input = "";
            Scanner scan = new Scanner(System.in);

            while (input != "end")
            {
                input = scan.nextLine();

                if (input == "pop")
                {
                    System.println(calc.pop());
                }
                else if (input == "isEmpty")
                {
                    System.println(calc.isEmpty());
                }
                else if (input.contains("push"))
                {
                    int i = 5;
                    string num = "";

                    while (i < input.length) num += input[i++];

                    int n = integer.parseInt(num);
                    calc.push(n);
                }
            }

        }
        catch (Exception e)
        {
            System.println(e);
        }  
    }  

}  
