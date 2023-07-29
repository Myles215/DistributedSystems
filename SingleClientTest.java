import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.AfterClass;
import org.junit.Test;

import java.rmi.*;  
import java.rmi.registry.*;  

public class SingleClientTest {

    Calculator server;
    Calculator client;
    boolean setupDone = false;

    @Before
    public void setUp() throws Exception 
    {

        if (setupDone) return;

        setupDone = true;

        server = new CalculatorImplementation();
        Registry serverRegistry = LocateRegistry.createRegistry(1099);
        serverRegistry.rebind("Calc", server);    

        Registry clientRegistry = LocateRegistry.getRegistry();
        client = (Calculator) clientRegistry.lookup("Calc");
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

    //This destroys the server
    @AfterClass
    static public void Exit()
    {
        System.exit(0);
    }
}