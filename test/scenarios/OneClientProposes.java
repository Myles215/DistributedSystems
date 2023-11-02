package test.scenarios;

import paxos.Server;
import paxos.PaxosClient;
import test.ControlledClient;

public class OneClientProposes 
{
    public static void main(String[] args)
    {
        int port = 1234;

        Server server = new Server();
        String[] serverArgs = {Integer.toString(port)};

        ControlledClient proposer = new ControlledClient(port, 1, "M1IsPresident");

        ControlledClient acceptor2 = new ControlledClient(port, 2, null);
        ControlledClient acceptor3 = new ControlledClient(port, 3, null);
        ControlledClient acceptor4 = new ControlledClient(port, 4, null);
        ControlledClient acceptor5 = new ControlledClient(port, 5, null);
        ControlledClient acceptor6 = new ControlledClient(port, 6, null);
        ControlledClient acceptor7 = new ControlledClient(port, 7, null);
        ControlledClient acceptor8 = new ControlledClient(port, 8, null);
        ControlledClient acceptor9 = new ControlledClient(port, 9, null);

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
