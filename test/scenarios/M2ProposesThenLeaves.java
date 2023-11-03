package test.scenarios;

import paxos.Server;
import paxos.PaxosClient;
import test.ClientThread;
import test.ThreadedServer;

public class M2ProposesThenLeaves
{
    public static void main(String[] args)
    {
        int port = 1234;

        ThreadedServer server = new ThreadedServer(port);
        String[] serverArgs = {Integer.toString(port)};

        ClientThread M1 = new ClientThread(port, 1, "M1IsPresident");
        ClientThread M2 = new ClientThread(port, 2, "M2IsPresident");

        ClientThread acceptor3 = new ClientThread(port, 3, null);
        ClientThread acceptor4 = new ClientThread(port, 4, null);
        ClientThread acceptor5 = new ClientThread(port, 5, null);
        ClientThread acceptor6 = new ClientThread(port, 6, null);
        ClientThread acceptor7 = new ClientThread(port, 7, null);
        ClientThread acceptor8 = new ClientThread(port, 8, null);
        ClientThread acceptor9 = new ClientThread(port, 9, null);

        acceptor3.start();
        acceptor4.start();
        acceptor5.start();
        acceptor6.start();
        acceptor7.start();
        acceptor8.start();
        acceptor9.start();

        M2.start();

        server.start();

        //Give M2 a chance to get it's proposal through
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        M2.SetCommittedValue();

        //Now, we start M1 and it will push through M2s previous proposal
        M1.start();
    }
}