import java.rmi.*;  
import java.rmi.registry.*;  

public class CalculatorServer{  

    public static void main(String args[]){  
        try{  
            Calculator calc = new CalculatorImplementation();  
            Naming.rebind("rmi://localhost:5000/calculatorServer", calc);  
        }
        catch (Exception error) 
        {
            System.out.println(error);
        }  
    }  
}  