import java.rmi.*;  
import java.rmi.registry.*;  

public class CalculatorServer
{  
    public static void main(String args[]){  
        try{  
            //Simply define and start the server
            Calculator server = new CalculatorImplementation();

            Registry registry = LocateRegistry.createRegistry(1121);

            registry.rebind("Calc", server);
        }
        catch (Exception error) 
        {
            System.out.println(error);
        }  
    }  
}  