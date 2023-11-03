package test.scenarios;

import paxos.Server;
import paxos.PaxosClient;
import test.ClientThread;

public class JustM1M2M3 {
    public static void main(String[] args)
    {
        int port = 1234;

        Server server = new Server();
        String[] serverArgs = {Integer.toString(port)};

        ClientThread proposer = new ClientThread(port, 1, "M1IsPresident", 2);

        ClientThread acceptor2 = new ClientThread(port, 2, null, 2);
        ClientThread acceptor3 = new ClientThread(port, 3, null, 3);

        acceptor2.start();
        acceptor3.start();

        proposer.start();

        server.main(serverArgs);
    }
}
