package test.scenarios;

import paxos.Server;
import paxos.PaxosClient;
import test.ClientThread;

public class OneClientProposes 
{
    public static void main(String[] args)
    {
        int port = 1234;

        Server server = new Server();
        String[] serverArgs = {Integer.toString(port)};

        ClientThread proposer = new ClientThread(port, 1, "M1IsPresident");

        ClientThread acceptor2 = new ClientThread(port, 2, null);
        ClientThread acceptor3 = new ClientThread(port, 3, null);
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

        proposer.start();

        server.main(serverArgs);
    }
}
