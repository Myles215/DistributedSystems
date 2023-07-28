import java.rmi.Naming;  
import java.rmi.registry.*;  

public class calculatorServer{  

    public static void main(String args[]){  
        try{  
            CalculatorImplementation calc = new CalculatorImplementation();  
            Naming.rebind("rmi://localhost:5000/calculatorServer", calc);  
        }
        catch (Exception e) 
        {
            System.out.println(error);
        }  
    }  
}  