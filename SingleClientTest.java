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
    static public int id;

    @BeforeClass
    static public void setUp() throws Exception 
    {
        server = new CalculatorImplementation();
        Registry serverRegistry = LocateRegistry.createRegistry(1099);
        serverRegistry.rebind("Calc", server);    

        Registry clientRegistry = LocateRegistry.getRegistry();
        client = (Calculator) clientRegistry.lookup("Calc");

        id = client.onConnect();
    }

    @Test
    public void pushAndPop() throws RemoteException 
    {
        client.pushValue(5, id);
        client.pushValue(6, id);
        client.pushValue(7, id);

        assertEquals(client.pop(id), 7);
        assertEquals(client.pop(id), 6);
        assertEquals(client.pop(id), 5);
    }

    @Test
    public void pushAndPop2() throws RemoteException 
    {
        client.pushValue(5, id);
        client.pushValue(6, id);

        assertEquals(client.pop(id), 6);

        client.pushValue(7, id);

        assertEquals(client.pop(id), 7);

        client.pushValue(-7, id);

        assertEquals(client.pop(id), -7);
        assertEquals(client.pop(id), 5);
    }

    @Test
    public void pushAndPopDelay() throws Exception
    {
        client.pushValue(6, id);

        assertEquals(client.pop(id), 6);
    }

    @Test
    public void emptiness() throws RemoteException
    {
        assertEquals(client.isEmpty(id), true);

        client.pushValue(0, id);

        assertEquals(client.isEmpty(id), false);

        client.pop(id);

        assertEquals(client.isEmpty(id), true);
    }

    @Test 
    public void gcdPositives() throws Exception
    {

        int curGcd = 4;

        client.pushValue(2*curGcd, id);
        client.pushValue(3*curGcd, id);
        client.pushValue(6*curGcd, id);

        client.pushOperation("gcd", id);

        assertEquals(client.pop(id), curGcd);

        curGcd = 8;

        client.pushValue(3*curGcd, id);
        client.pushValue(1*curGcd, id);
        client.pushValue(5*curGcd, id);

        client.pushOperation("gcd", id);

        assertEquals(client.pop(id), curGcd);

    }

    @Test
    public void gcdNegatives() throws Exception
    {
        int curGcd = -3;

        client.pushValue(2*curGcd, id);
        client.pushValue(3*curGcd, id);
        client.pushValue(6*curGcd, id);

        client.pushOperation("gcd", id);

        assertEquals(client.pop(id), Math.abs(curGcd));

        curGcd = -7;

        client.pushValue(3*curGcd, id);
        client.pushValue(1*curGcd, id);
        client.pushValue(5*curGcd, id);

        client.pushOperation("gcd", id);

        assertEquals(client.pop(id), Math.abs(curGcd));
    }

    @Test
    public void gcdPositivesAndNegatives() throws Exception
    {
        int curGcd = -6;

        client.pushValue(2*curGcd, id);
        client.pushValue(-3*curGcd, id);
        client.pushValue(-6*curGcd, id);

        client.pushOperation("gcd", id);

        assertEquals(client.pop(id), Math.abs(curGcd));

        curGcd = 7;

        client.pushValue(3*curGcd, id);
        client.pushValue(-1*curGcd, id);
        client.pushValue(5*curGcd, id);

        client.pushOperation("gcd", id);

        assertEquals(client.pop(id), Math.abs(curGcd));
    }

    @Test
    public void lcmPositives() throws Exception
    {
        int curLcm = 12;

        client.pushValue(curLcm/2, id);
        client.pushValue(curLcm/3, id);
        client.pushValue(curLcm/4, id);

        client.pushOperation("lcm", id);

        assertEquals(client.pop(id), Math.abs(curLcm));

        curLcm = 18;

        client.pushValue(curLcm/3, id);
        client.pushValue(curLcm/2, id);
        client.pushValue(curLcm/6, id);

        client.pushOperation("lcm", id);

        assertEquals(client.pop(id), Math.abs(curLcm));
    }

    @Test
    public void lcmNegatives() throws Exception
    {
        int curLcm = -20;

        client.pushValue(curLcm/5, id);
        client.pushValue(curLcm/4, id);
        client.pushValue(curLcm/2, id);

        client.pushOperation("lcm", id);

        assertEquals(client.pop(id), Math.abs(curLcm));

        curLcm = -30;

        client.pushValue(curLcm/6, id);
        client.pushValue(curLcm/5, id);
        client.pushValue(curLcm/3, id);

        client.pushOperation("lcm", id);

        assertEquals(client.pop(id), Math.abs(curLcm));
    }

    @Test
    public void lcmPositivesAndNegatives() throws Exception
    {
        int curLcm = 12;

        client.pushValue(curLcm/-1, id);
        client.pushValue(curLcm/-4, id);
        client.pushValue(curLcm/2, id);

        client.pushOperation("lcm", id);

        assertEquals(client.pop(id), Math.abs(curLcm));

        curLcm = 15;

        client.pushValue(curLcm/-1, id);
        client.pushValue(curLcm/5, id);
        client.pushValue(curLcm/3, id);

        client.pushOperation("lcm", id);

        assertEquals(client.pop(id), Math.abs(curLcm));
    }

    @Test
    public void maxPositives() throws Exception
    {
        int max = 11;

        client.pushValue(max - 10, id);
        client.pushValue(max - 2, id);
        client.pushValue(max, id);

        client.pushOperation("max", id);

        assertEquals(client.pop(id), max);

        max = 22;

        client.pushValue(max - 5, id);
        client.pushValue(max - 9, id);
        client.pushValue(max, id);

        client.pushOperation("max", id);

        assertEquals(client.pop(id), max);

    }

    @Test
    public void maxNegatives() throws Exception
    {
        int max = -2;

        client.pushValue(max - 5, id);
        client.pushValue(max - 8, id);
        client.pushValue(max, id);

        client.pushOperation("max", id);

        assertEquals(client.pop(id), max);

        max = -30;

        client.pushValue(max - 1, id);
        client.pushValue(max - 2, id);
        client.pushValue(max, id);

        client.pushOperation("max", id);

        assertEquals(client.pop(id), max);

    }

    @Test
    public void minPositives() throws Exception
    {
        int min = 11;

        client.pushValue(min + 10, id);
        client.pushValue(min + 2, id);
        client.pushValue(min, id);

        client.pushOperation("min", id);

        assertEquals(client.pop(id), min);

        min = 22;

        client.pushValue(min + 5, id);
        client.pushValue(min + 9, id);
        client.pushValue(min, id);

        client.pushOperation("min", id);

        assertEquals(client.pop(id), min);

    }

    @Test
    public void minNegatives() throws Exception
    {
        int min = -2;

        client.pushValue(min + 5, id);
        client.pushValue(min + 8, id);
        client.pushValue(min, id);

        client.pushOperation("min", id);

        assertEquals(client.pop(id), min);

        min = -30;

        client.pushValue(min + 1, id);
        client.pushValue(min + 2, id);
        client.pushValue(min, id);

        client.pushOperation("min", id);

        assertEquals(client.pop(id), min);

    }

}