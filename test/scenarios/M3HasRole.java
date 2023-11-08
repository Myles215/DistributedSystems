package test.scenarios;

import paxos.Server;
import paxos.PaxosClient;
import test.ClientThread;
import test.ThreadedServer;

import org.junit.Test;

public class M3HasRole {
    @Test
    public void runTest()
    {
        int port = 8432;

        ThreadedServer Tserver = new ThreadedServer(port);

        //Set role here
        ClientThread proposer = new ClientThread(port, 3, "M3IsPresident", 3);

        ClientThread acceptor2 = new ClientThread(port, 1, null);
        ClientThread acceptor3 = new ClientThread(port, 2, null);
        ClientThread acceptor4 = new ClientThread(port, 4, null);
        ClientThread acceptor5 = new ClientThread(port, 5, null);
        ClientThread acceptor6 = new ClientThread(port, 6, null);
        ClientThread acceptor7 = new ClientThread(port, 7, null);
        ClientThread acceptor8 = new ClientThread(port, 8, null);
        ClientThread acceptor9 = new ClientThread(port, 9, null);

        acceptor2.start();
        acceptor3.start();
        acceptor4.start();
        acceptor5.start();
        acceptor6.start();
        acceptor7.start();
        acceptor8.start();
        acceptor9.start();

        Tserver.start();

        try { Thread.sleep(150); } catch (InterruptedException e) {}

        proposer.start();

        while (proposer.client.committed == null)
        {
            try { Thread.sleep(100); } catch(InterruptedException e) {}
        }

        //Wait to complete
        try { Thread.sleep(250); } catch (InterruptedException e) {}
    }
}
