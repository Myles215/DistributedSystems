import java.rmi.*;  
import java.rmi.registry.*;  
import java.rmi.server.UnicastRemoteObject;

public class CalculatorServer
{  

    public static void main(String args[]){  
        try{  
            Calculator server = new CalculatorImplementation();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("Calc", server);
        }
        catch (Exception error) 
        {
            System.out.println(error);
        }  
    }  
}  