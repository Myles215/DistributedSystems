import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.rmi.*;  
import java.rmi.registry.*;  
import java.rmi.server.UnicastRemoteObject;

public class SingleClientTest {

    static Calculator server;
    static Calculator client;

    @BeforeClass
    static public void setUp() throws Exception 
    {

        server = new CalculatorImplementation();
        Registry serverRegistry = LocateRegistry.createRegistry(1099);
        serverRegistry.rebind("Calc", server);    

        Registry clientRegistry = LocateRegistry.getRegistry();
        client = (Calculator) clientRegistry.lookup("Calc");
    }

    //This destroys the server
    @AfterClass
    static public void Exit() throws Exception
    {
        //System.exit(0);
    }

    @Test
    public void pushAndPop() throws RemoteException 
    {
        client.pushValue(5);
        client.pushValue(6);
        client.pushValue(7);

        assertEquals(client.pop(), 7);
        assertEquals(client.pop(), 6);
        assertEquals(client.pop(), 5);
    }

    @Test
    public void pushAndPop2() throws RemoteException 
    {
        client.pushValue(5);
        client.pushValue(6);

        assertEquals(client.pop(), 6);

        client.pushValue(7);

        assertEquals(client.pop(), 7);

        client.pushValue(-7);

        assertEquals(client.pop(), -7);
        assertEquals(client.pop(), 5);
    }

    @Test
    public void emptiness() throws RemoteException
    {
        assertEquals(client.isEmpty(), true);

        client.pushValue(0);

        assertEquals(client.isEmpty(), false);

        client.pop();

        assertEquals(client.isEmpty(), true);
    }

    @Test 
    public void gcdPositives() throws Exception
    {

        int curGcd = 4;

        client.pushValue(2*4);
        client.pushValue(3*4);
        client.pushValue(6*4);

        client.pushOperation("gcd");

        assertEquals(client.pop(), curGcd);

    }
}