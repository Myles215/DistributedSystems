import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.rmi.*;  
import java.rmi.registry.*;  
import java.rmi.server.UnicastRemoteObject;

public class TripleClientTest {

    static Calculator server;

    static Calculator client1;
    static Calculator client2;
    static Calculator client3;

    @BeforeClass
    static public void setUp() throws Exception 
    {

        server = new CalculatorImplementation();
        Registry serverRegistry = LocateRegistry.createRegistry(9055);
        serverRegistry.rebind("Calc", server);    

        Registry clientRegistry = LocateRegistry.getRegistry(9055);
        client1 = (Calculator) clientRegistry.lookup("Calc");
        client2 = (Calculator) clientRegistry.lookup("Calc");
        client3 = (Calculator) clientRegistry.lookup("Calc");
    }

    @Test
    public void pushAndPop() throws RemoteException 
    {
        client1.pushValue(5);
        client2.pushValue(6);
        client3.pushValue(7);

        assertEquals(client1.pop(), 7);
        assertEquals(client2.pop(), 6);
        assertEquals(client3.pop(), 5);
    }

    @Test
    public void pushAndPop2() throws RemoteException 
    {
        client1.pushValue(5);
        client2.pushValue(6);

        assertEquals(client1.pop(), 6);

        client3.pushValue(9);
        client2.pushValue(7);

        assertEquals(client1.pop(), 7);

        client3.pushValue(-7);

        assertEquals(client2.pop(), -7);
        assertEquals(client2.pop(), 9);
        assertEquals(client3.pop(), 5);
    }

    @Test
    public void emptiness() throws RemoteException
    {
        assertEquals(client1.isEmpty(), true);
        assertEquals(client2.isEmpty(), true);
        assertEquals(client3.isEmpty(), true);

        client2.pushValue(0);

        assertEquals(client1.isEmpty(), false);
        assertEquals(client2.isEmpty(), false);
        assertEquals(client3.isEmpty(), false);

        client3.pop();

        assertEquals(client1.isEmpty(), true);
        assertEquals(client2.isEmpty(), true);
        assertEquals(client3.isEmpty(), true);
    }

    @Test 
    public void gcdPositives() throws Exception
    {

        int curGcd = 4;

        client1.pushValue(2*curGcd);
        client2.pushValue(3*curGcd);
        client3.pushValue(6*curGcd);

        client2.pushOperation("gcd");

        assertEquals(client3.pop(), curGcd);

        curGcd = 8;

        client1.pushValue(3*curGcd);
        client2.pushValue(1*curGcd);
        client3.pushValue(5*curGcd);

        client3.pushOperation("gcd");

        assertEquals(client1.pop(), curGcd);

    }

    @Test
    public void gcdNegatives() throws Exception
    {
        int curGcd = -3;

        client1.pushValue(2*curGcd);
        client2.pushValue(3*curGcd);
        client3.pushValue(6*curGcd);

        client1.pushOperation("gcd");

        assertEquals(client2.pop(), Math.abs(curGcd));

        curGcd = -7;

        client1.pushValue(3*curGcd);
        client2.pushValue(1*curGcd);
        client3.pushValue(5*curGcd);

        client2.pushOperation("gcd");

        assertEquals(client3.pop(), Math.abs(curGcd));
    }

    @Test
    public void gcdPositivesAndNegatives() throws Exception
    {
        int curGcd = -6;

        client1.pushValue(2*curGcd);
        client2.pushValue(-3*curGcd);
        client3.pushValue(-6*curGcd);

        client2.pushOperation("gcd");

        assertEquals(client1.pop(), Math.abs(curGcd));

        curGcd = 7;

        client1.pushValue(3*curGcd);
        client2.pushValue(-1*curGcd);
        client3.pushValue(5*curGcd);

        client2.pushOperation("gcd");

        assertEquals(client1.pop(), Math.abs(curGcd));
    }

    @Test
    public void lcmPositives() throws Exception
    {
        int curLcm = 12;

        client1.pushValue(curLcm/2);
        client2.pushValue(curLcm/3);
        client3.pushValue(curLcm/4);

        client3.pushOperation("lcm");

        assertEquals(client2.pop(), Math.abs(curLcm));

        curLcm = 18;

        client1.pushValue(curLcm/3);
        client2.pushValue(curLcm/2);
        client3.pushValue(curLcm/6);

        client2.pushOperation("lcm");

        assertEquals(client1.pop(), Math.abs(curLcm));
    }

    @Test
    public void lcmNegatives() throws Exception
    {
        int curLcm = -20;

        client1.pushValue(curLcm/5);
        client2.pushValue(curLcm/4);
        client3.pushValue(curLcm/2);

        client3.pushOperation("lcm");

        assertEquals(client1.pop(), Math.abs(curLcm));

        curLcm = -30;

        client1.pushValue(curLcm/6);
        client2.pushValue(curLcm/5);
        client3.pushValue(curLcm/3);

        client3.pushOperation("lcm");

        assertEquals(client2.pop(), Math.abs(curLcm));
    }

    @Test
    public void lcmPositivesAndNegatives() throws Exception
    {
        int curLcm = 12;

        client1.pushValue(curLcm/-1);
        client2.pushValue(curLcm/-4);
        client3.pushValue(curLcm/2);

        client2.pushOperation("lcm");

        assertEquals(client1.pop(), Math.abs(curLcm));

        curLcm = 15;

        client1.pushValue(curLcm/-1);
        client2.pushValue(curLcm/5);
        client3.pushValue(curLcm/3);

        client2.pushOperation("lcm");

        assertEquals(client3.pop(), Math.abs(curLcm));
    }

    @Test
    public void maxPositives() throws Exception
    {
        int max = 11;

        client1.pushValue(max - 10);
        client2.pushValue(max - 2);
        client3.pushValue(max);

        client1.pushOperation("max");

        assertEquals(client2.pop(), max);

        max = 22;

        client1.pushValue(max - 5);
        client2.pushValue(max - 9);
        client3.pushValue(max);

        client3.pushOperation("max");

        assertEquals(client2.pop(), max);

    }

    @Test
    public void maxNegatives() throws Exception
    {
        int max = -2;

        client1.pushValue(max - 5);
        client2.pushValue(max - 8);
        client3.pushValue(max);

        client2.pushOperation("max");

        assertEquals(client1.pop(), max);

        max = -30;

        client1.pushValue(max - 1);
        client2.pushValue(max - 2);
        client3.pushValue(max);

        client1.pushOperation("max");

        assertEquals(client2.pop(), max);

    }

    @Test
    public void minPositives() throws Exception
    {
        int min = 11;

        client1.pushValue(min + 10);
        client2.pushValue(min + 2);
        client3.pushValue(min);

        client2.pushOperation("min");

        assertEquals(client3.pop(), min);

        min = 22;

        client1.pushValue(min + 5);
        client2.pushValue(min + 9);
        client3.pushValue(min);

        client2.pushOperation("min");

        assertEquals(client3.pop(), min);

    }

    @Test
    public void minNegatives() throws Exception
    {
        int min = -2;

        client1.pushValue(min + 5);
        client2.pushValue(min + 8);
        client3.pushValue(min);

        client1.pushOperation("min");

        assertEquals(client2.pop(), min);

        min = -30;

        client1.pushValue(min + 1);
        client2.pushValue(min + 2);
        client3.pushValue(min);

        client3.pushOperation("min");

        assertEquals(client1.pop(), min);

    }

}