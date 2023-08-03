import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import java.rmi.*;  
import java.rmi.registry.*;  
import java.rmi.server.UnicastRemoteObject;

public class FourClientTest {

    static Calculator server;

    static Calculator client1;
    static int client1id;
    static Calculator client2;
    static int client2id;
    static Calculator client3;
    static int client3id;
    static Calculator client4;
    static int client4id;

    static int c = 0;

    //Starts all clients and server
    @BeforeClass
    static public void setUp() throws Exception 
    {

        server = new CalculatorImplementation();
        Registry serverRegistry = LocateRegistry.createRegistry(9055);
        serverRegistry.rebind("Calc", server);    

        Registry clientRegistry = LocateRegistry.getRegistry(9055);
        client1 = (Calculator) clientRegistry.lookup("Calc");
        client1id = client1.onConnect();

        client2 = (Calculator) clientRegistry.lookup("Calc");
        client2id = client2.onConnect();

        client3 = (Calculator) clientRegistry.lookup("Calc");
        client3id = client3.onConnect();

        client4 = (Calculator) clientRegistry.lookup("Calc");
        client4id = client4.onConnect();
    }

    @Before
    public void check()
    {
        System.out.println(c++);
    }

    @Test
    public void pushAndPop() throws RemoteException 
    {
        client1.pushValue(5, client1id);
        client2.pushValue(6, client2id);
        client3.pushValue(7, client3id);
        client4.pushValue(9, client4id);

        assertEquals(client1.pop(client1id), 5);
        assertEquals(client2.pop(client2id), 6);
        assertEquals(client3.pop(client3id), 7);
        assertEquals(client4.pop(client4id), 9);
        
    }

    @Test
    public void pushAndPop2() throws RemoteException 
    {
        client1.pushValue(5, client1id);
        client2.pushValue(6, client2id);
        client4.pushValue(0, client4id);

        assertEquals(client1.pop(client1id), 5);

        client3.pushValue(9, client3id);
        client2.pushValue(7, client2id);

        assertEquals(client2.pop(client2id), 7);

        client3.pushValue(-7, client3id);

        assertEquals(client2.pop(client2id), 6);
        assertEquals(client3.pop(client3id), -7);
        assertEquals(client3.pop(client3id), 9);
        assertEquals(client4.pop(client4id), 0);
    }

    @Test
    public void emptiness() throws RemoteException
    {
        assertEquals(client1.isEmpty(client1id), true);
        assertEquals(client2.isEmpty(client2id), true);
        assertEquals(client3.isEmpty(client3id), true);
        assertEquals(client4.isEmpty(client4id), true);

        client1.pushValue(0, client1id);
        client3.pushValue(0, client3id);

        assertEquals(client1.isEmpty(client1id), false);
        assertEquals(client2.isEmpty(client2id), true);
        assertEquals(client3.isEmpty(client3id), false);
        assertEquals(client4.isEmpty(client4id), true);

        client1.pop(client1id);
        client3.pop(client3id);

        assertEquals(client1.isEmpty(client1id), true);
        assertEquals(client2.isEmpty(client2id), true);
        assertEquals(client3.isEmpty(client3id), true);
        assertEquals(client4.isEmpty(client4id), true);
    }

    @Test 
    public void gcdPositives() throws Exception
    {

        int curGcd = 4;
        int mixGcd = 3;

        client1.pushValue(2*mixGcd, client1id);
        client2.pushValue(3*curGcd, client2id);
        client2.pushValue(2*curGcd, client2id);
        client3.pushValue(3*mixGcd, client3id);
        client4.pushValue(5*mixGcd, client4id);

        client2.pushOperation("gcd", client2id);

        assertEquals(client2.pop(client2id), curGcd);

        client1.pop(client1id);
        client3.pop(client3id);
        client4.pop(client4id);

        curGcd = 8;
        mixGcd = 7;

        client1.pushValue(2*mixGcd, client1id);
        client2.pushValue(3*mixGcd, client2id);
        client3.pushValue(2*curGcd, client3id);
        client3.pushValue(3*curGcd, client3id);
        client4.pushValue(5*mixGcd, client4id);

        client3.pushOperation("gcd", client3id);

        assertEquals(client3.pop(client3id), curGcd);

        client1.pop(client1id);
        client2.pop(client2id);
        client4.pop(client4id);

    }

    @Test
    public void gcdNegatives() throws Exception
    {
        int curGcd = -4;
        int mixGcd = -3;

        client1.pushValue(2*mixGcd, client1id);
        client2.pushValue(3*mixGcd, client2id);
        client3.pushValue(6*mixGcd, client3id);
        client4.pushValue(2*curGcd, client4id);
        client4.pushValue(5*curGcd, client4id);

        client4.pushOperation("gcd", client4id);

        assertEquals(client4.pop(client4id), Math.abs(curGcd));

        client1.pop(client1id);
        client2.pop(client2id);
        client3.pop(client3id);

        curGcd = -8;
        mixGcd = -7;

        client1.pushValue(2*mixGcd, client1id);
        client2.pushValue(3*mixGcd, client2id);
        client3.pushValue(2*curGcd, client3id);
        client3.pushValue(3*curGcd, client3id);
        client4.pushValue(5*mixGcd, client4id);

        client3.pushOperation("gcd", client3id);

        assertEquals(client3.pop(client3id), Math.abs(curGcd));

        client1.pop(client1id);
        client2.pop(client2id);
        client4.pop(client4id);
    }

    @Test
    public void gcdPositivesAndNegatives() throws Exception
    {
        int curGcd = 5;
        int mixGcd = -3;

        client1.pushValue(2*curGcd, client1id);
        client1.pushValue(3*curGcd, client1id);
        client2.pushValue(-6*mixGcd, client2id);
        client3.pushValue(2*mixGcd, client3id);
        client4.pushValue(-5*mixGcd, client4id);

        client1.pushOperation("gcd", client1id);

        assertEquals(client1.pop(client1id), Math.abs(curGcd));

        client2.pop(client2id);
        client3.pop(client3id);
        client4.pop(client4id);


        curGcd = 8;
        mixGcd = -7;

        client1.pushValue(2*mixGcd, client1id);
        client2.pushValue(-3*mixGcd, client2id);
        client3.pushValue(2*curGcd, client3id);
        client3.pushValue(5*curGcd, client3id);
        client4.pushValue(-5*mixGcd, client4id);

        client3.pushOperation("gcd", client3id);

        assertEquals(client3.pop(client3id), Math.abs(curGcd));

        client1.pop(client1id);
        client2.pop(client2id);
        client4.pop(client4id);
    }

    @Test
    public void lcmPositives() throws Exception
    {
        int curLcm = 12;
        int mixLcm = 16;

        client1.pushValue(mixLcm/2, client1id);
        client2.pushValue(curLcm/4, client2id);
        client2.pushValue(curLcm/1, client2id);
        client3.pushValue(mixLcm/4, client3id);
        client4.pushValue(mixLcm/4, client4id);

        client2.pushOperation("lcm", client2id);

        assertEquals(client2.pop(client2id), Math.abs(curLcm));

        client1.pop(client1id);
        client3.pop(client3id);
        client4.pop(client4id);

        curLcm = 20;
        mixLcm = 18;

        client1.pushValue(mixLcm/2, client1id);
        client2.pushValue(mixLcm/4, client2id);
        client3.pushValue(curLcm/5, client3id);
        client3.pushValue(curLcm/4, client3id);
        client4.pushValue(mixLcm/3, client4id);

        client3.pushOperation("lcm", client3id);

        assertEquals(client3.pop(client3id), Math.abs(curLcm));

        client1.pop(client1id);
        client2.pop(client2id);
        client4.pop(client4id);
    }

    @Test
    public void lcmNegatives() throws Exception
    {
        int curLcm = -12;
        int mixLcm = -16;

        client1.pushValue(mixLcm/2, client1id);
        client2.pushValue(curLcm/4, client2id);
        client2.pushValue(curLcm/1, client2id);
        client3.pushValue(mixLcm/4, client3id);
        client4.pushValue(mixLcm/4, client4id);

        client2.pushOperation("lcm", client2id);

        assertEquals(client2.pop(client2id), Math.abs(curLcm));

        client1.pop(client1id);
        client3.pop(client3id);
        client4.pop(client4id);

        curLcm = -18;
        mixLcm = -20;

        client1.pushValue(mixLcm/2, client1id);
        client2.pushValue(mixLcm/4, client2id);
        client3.pushValue(curLcm/2, client3id);
        client3.pushValue(curLcm/3, client3id);
        client4.pushValue(mixLcm/3, client4id);

        client3.pushOperation("lcm", client3id);

        assertEquals(client3.pop(client3id), Math.abs(curLcm));

        client1.pop(client1id);
        client2.pop(client2id);
        client4.pop(client4id);
    }

    @Test
    public void lcmPositivesAndNegatives() throws Exception
    {
        int curLcm = 12;
        int mixLcm = -16;

        client1.pushValue(mixLcm/2, client1id);
        client2.pushValue(curLcm/4, client2id);
        client2.pushValue(curLcm/-1, client2id);
        client3.pushValue(mixLcm/4, client3id);
        client4.pushValue(mixLcm/-4, client4id);

        client2.pushOperation("lcm", client2id);

        assertEquals(client2.pop(client2id), Math.abs(curLcm));

        client1.pop(client1id);
        client3.pop(client3id);
        client4.pop(client4id);

        curLcm = -18;
        mixLcm = 20;

        client1.pushValue(mixLcm/2, client1id);
        client2.pushValue(mixLcm/-4, client2id);
        client3.pushValue(curLcm/-2, client3id);
        client3.pushValue(curLcm/3, client3id);
        client4.pushValue(mixLcm/3, client4id);

        client3.pushOperation("lcm", client3id);

        assertEquals(client3.pop(client3id), Math.abs(curLcm));

        client1.pop(client1id);
        client2.pop(client2id);
        client4.pop(client4id);
    }

    @Test
    public void maxPositives() throws Exception
    {
        int max = 11;

        client1.pushValue(max, client1id);
        client2.pushValue(max + 2, client2id);
        client3.pushValue(max + 4, client3id);
        client4.pushValue(max + 10, client4id);

        client1.pushOperation("max", client1id);

        assertEquals(client1.pop(client1id), max);

        client2.pop(client2id);
        client3.pop(client3id);
        client4.pop(client4id);

        max = 22;

        client1.pushValue(max + 9, client1id);
        client2.pushValue(max + 2, client2id);
        client3.pushValue(max, client3id);
        client4.pushValue(max + 10, client4id);

        client3.pushOperation("max", client3id);

        assertEquals(client3.pop(client3id), max);

        client1.pop(client1id);
        client2.pop(client2id);
        client4.pop(client4id);

    }

    @Test
    public void maxNegatives() throws Exception
    {
        int max = -9;

        client1.pushValue(max, client1id);
        client2.pushValue(max + 2, client2id);
        client3.pushValue(max + 4, client3id);
        client4.pushValue(max + 10, client4id);

        client1.pushOperation("max", client1id);

        assertEquals(client1.pop(client1id), max);

        client2.pop(client2id);
        client3.pop(client3id);
        client4.pop(client4id);

        max = -5;

        client1.pushValue(max + 9, client1id);
        client2.pushValue(max + 2, client2id);
        client3.pushValue(max, client3id);
        client4.pushValue(max + 10, client4id);

        client3.pushOperation("max", client3id);

        assertEquals(client3.pop(client3id), max);

        client1.pop(client1id);
        client2.pop(client2id);
        client4.pop(client4id);

    }

    @Test
    public void minPositives() throws Exception
    {
        int min = 11;

        client1.pushValue(min - 10, client1id);
        client2.pushValue(min - 2, client2id);
        client3.pushValue(min, client3id);
        client4.pushValue(min - 9, client4id);

        client3.pushOperation("min", client3id);

        assertEquals(client3.pop(client3id), min);

        client1.pop(client1id);
        client2.pop(client2id);
        client4.pop(client4id);      

        min = 22;

        client1.pushValue(min, client1id);
        client2.pushValue(min - 2, client2id);
        client3.pushValue(min - 1, client3id);
        client4.pushValue(min - 9, client4id);

        client1.pushOperation("min", client1id);

        assertEquals(client1.pop(client1id), min);

        client2.pop(client2id);
        client3.pop(client3id);
        client4.pop(client4id);     

    }

    @Test
    public void minNegatives() throws Exception
    {
        int min = -11;

        client1.pushValue(min - 10, client1id);
        client2.pushValue(min - 2, client2id);
        client3.pushValue(min, client3id);
        client4.pushValue(min - 9, client4id);

        client3.pushOperation("min", client3id);

        assertEquals(client3.pop(client3id), min);

        client1.pop(client1id);
        client2.pop(client2id);
        client4.pop(client4id);      

        min = -22;

        client1.pushValue(min, client1id);
        client2.pushValue(min - 2, client2id);
        client3.pushValue(min - 1, client3id);
        client4.pushValue(min - 9, client4id);

        client1.pushOperation("min", client1id);

        assertEquals(client1.pop(client1id), min);

        client2.pop(client2id);
        client3.pop(client3id);
        client4.pop(client4id);     

    }

}