package test.scenarios;

import paxos.Server;
import paxos.PaxosClient;
import test.ClientThread;
import test.ThreadedServer;

import org.junit.Test;

public class M1ProposesAllRoles {
    public void runTest()
    {
        int port = 4523;

        ThreadedServer Tserver = new ThreadedServer(port);
        String[] serverArgs = {Integer.toString(port)};

        ClientThread proposer = new ClientThread(port, 1, "M1IsPresident", 1);

        ClientThread acceptor2 = new ClientThread(port, 2, null, 2);
        ClientThread acceptor3 = new ClientThread(port, 3, null, 3);
        ClientThread acceptor4 = new ClientThread(port, 4, null, 4);
        ClientThread acceptor5 = new ClientThread(port, 5, null, 4);
        ClientThread acceptor6 = new ClientThread(port, 6, null, 4);
        ClientThread acceptor7 = new ClientThread(port, 7, null, 4);
        ClientThread acceptor8 = new ClientThread(port, 8, null, 4);
        ClientThread acceptor9 = new ClientThread(port, 9, null, 4);

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