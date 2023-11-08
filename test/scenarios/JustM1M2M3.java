package test.scenarios;

import paxos.Server;
import paxos.PaxosClient;
import test.ClientThread;
import test.ThreadedServer;

import org.junit.Test;

public class JustM1M2M3 
{
    @Test
    public void runTest()
    {
        int port = 1234;

        ThreadedServer Tserver = new ThreadedServer(port);
        String[] serverArgs = {Integer.toString(port)};

        ClientThread proposer = new ClientThread(port, 1, "M1IsPresident", 2);

        ClientThread acceptor2 = new ClientThread(port, 2, null, 2);
        ClientThread acceptor3 = new ClientThread(port, 3, null, 3);

        acceptor2.start();
        acceptor3.start();

        Tserver.start();

        //Wait for everyone else to set up
        try { Thread.sleep(100); } catch (InterruptedException e) {}

        proposer.start();

        while (proposer.client.committed == null)
        {
            try { Thread.sleep(100); } catch(InterruptedException e) {}
        }

        //Wait to complete
        try { Thread.sleep(250); } catch (InterruptedException e) {}
    }
}
