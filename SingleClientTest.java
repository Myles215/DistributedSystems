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
    public void pushAndPopDelay() throws Exception
    {
        client.pushValue(6);

        assertEquals(client.pop(), 6);
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

        client.pushValue(2*curGcd);
        client.pushValue(3*curGcd);
        client.pushValue(6*curGcd);

        client.pushOperation("gcd");

        assertEquals(client.pop(), curGcd);

        curGcd = 8;

        client.pushValue(3*curGcd);
        client.pushValue(1*curGcd);
        client.pushValue(5*curGcd);

        client.pushOperation("gcd");

        assertEquals(client.pop(), curGcd);

    }

    @Test
    public void gcdNegatives() throws Exception
    {
        int curGcd = -3;

        client.pushValue(2*curGcd);
        client.pushValue(3*curGcd);
        client.pushValue(6*curGcd);

        client.pushOperation("gcd");

        assertEquals(client.pop(), Math.abs(curGcd));

        curGcd = -7;

        client.pushValue(3*curGcd);
        client.pushValue(1*curGcd);
        client.pushValue(5*curGcd);

        client.pushOperation("gcd");

        assertEquals(client.pop(), Math.abs(curGcd));
    }

    @Test
    public void gcdPositivesAndNegatives() throws Exception
    {
        int curGcd = -6;

        client.pushValue(2*curGcd);
        client.pushValue(-3*curGcd);
        client.pushValue(-6*curGcd);

        client.pushOperation("gcd");

        assertEquals(client.pop(), Math.abs(curGcd));

        curGcd = 7;

        client.pushValue(3*curGcd);
        client.pushValue(-1*curGcd);
        client.pushValue(5*curGcd);

        client.pushOperation("gcd");

        assertEquals(client.pop(), Math.abs(curGcd));
    }

    @Test
    public void lcmPositives() throws Exception
    {
        int curLcm = 12;

        client.pushValue(curLcm/2);
        client.pushValue(curLcm/3);
        client.pushValue(curLcm/4);

        client.pushOperation("lcm");

        assertEquals(client.pop(), Math.abs(curLcm));

        curLcm = 18;

        client.pushValue(curLcm/3);
        client.pushValue(curLcm/2);
        client.pushValue(curLcm/6);

        client.pushOperation("lcm");

        assertEquals(client.pop(), Math.abs(curLcm));
    }

    @Test
    public void lcmNegatives() throws Exception
    {
        int curLcm = -20;

        client.pushValue(curLcm/5);
        client.pushValue(curLcm/4);
        client.pushValue(curLcm/2);

        client.pushOperation("lcm");

        assertEquals(client.pop(), Math.abs(curLcm));

        curLcm = -30;

        client.pushValue(curLcm/6);
        client.pushValue(curLcm/5);
        client.pushValue(curLcm/3);

        client.pushOperation("lcm");

        assertEquals(client.pop(), Math.abs(curLcm));
    }

    @Test
    public void lcmPositivesAndNegatives() throws Exception
    {
        int curLcm = 12;

        client.pushValue(curLcm/-1);
        client.pushValue(curLcm/-4);
        client.pushValue(curLcm/2);

        client.pushOperation("lcm");

        assertEquals(client.pop(), Math.abs(curLcm));

        curLcm = 15;

        client.pushValue(curLcm/-1);
        client.pushValue(curLcm/5);
        client.pushValue(curLcm/3);

        client.pushOperation("lcm");

        assertEquals(client.pop(), Math.abs(curLcm));
    }

    @Test
    public void maxPositives() throws Exception
    {
        int max = 11;

        client.pushValue(max - 10);
        client.pushValue(max - 2);
        client.pushValue(max);

        client.pushOperation("max");

        assertEquals(client.pop(), max);

        max = 22;

        client.pushValue(max - 5);
        client.pushValue(max - 9);
        client.pushValue(max);

        client.pushOperation("max");

        assertEquals(client.pop(), max);

    }

    @Test
    public void maxNegatives() throws Exception
    {
        int max = -2;

        client.pushValue(max - 5);
        client.pushValue(max - 8);
        client.pushValue(max);

        client.pushOperation("max");

        assertEquals(client.pop(), max);

        max = -30;

        client.pushValue(max - 1);
        client.pushValue(max - 2);
        client.pushValue(max);

        client.pushOperation("max");

        assertEquals(client.pop(), max);

    }

    @Test
    public void minPositives() throws Exception
    {
        int min = 11;

        client.pushValue(min + 10);
        client.pushValue(min + 2);
        client.pushValue(min);

        client.pushOperation("min");

        assertEquals(client.pop(), min);

        min = 22;

        client.pushValue(min + 5);
        client.pushValue(min + 9);
        client.pushValue(min);

        client.pushOperation("min");

        assertEquals(client.pop(), min);

    }

    @Test
    public void minNegatives() throws Exception
    {
        int min = -2;

        client.pushValue(min + 5);
        client.pushValue(min + 8);
        client.pushValue(min);

        client.pushOperation("min");

        assertEquals(client.pop(), min);

        min = -30;

        client.pushValue(min + 1);
        client.pushValue(min + 2);
        client.pushValue(min);

        client.pushOperation("min");

        assertEquals(client.pop(), min);

    }

}