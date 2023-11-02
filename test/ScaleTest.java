package test;

import paxos.Message;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

public class ScaleTest 
{

    static int startPort = 4573;

    public void wait(int i)
    {
        try { Thread.sleep(i); } catch (InterruptedException e) {}
    }

    @Test
    public void ThreeClientsProposal() throws InterruptedException
    {
        int port = startPort++;
        MockServer server = new MockServer(port, 10);

        ClientThread proposer = new ClientThread(port, 1, "ThreeClients");
        proposer.start();

        assertEquals(server.AcceptConnection(), true);
        server.SendStringToClient("starting", 1);

        ClientThread acceptor1 = new ClientThread(port, 2, null);
        acceptor1.start();

        assertEquals(server.AcceptConnection(), true);
        server.SendStringToClient("starting", 2);

        ClientThread acceptor2 = new ClientThread(port, 3, null);
        acceptor2.start();

        assertEquals(server.AcceptConnection(), true);
        server.SendStringToClient("starting", 3);

        wait(100);
        ArrayList<Message> passOn = server.ReadStringFromClient(1);

        //Pass on the messags to clients 2 and 3 while sending back not connected for others
        for (int i = 2;i<4;i++)
        {
            server.SendStringToClient(passOn.get(i-1).toString(), i);
        }

        for (int i = 4;i<10;i++)
        {
            //Tell client that there are no client with ID 3 through 9
            Message reply = new Message(1, i, "", -1, Message.MessageType.NC);
            server.SendStringToClient(reply.toString(), 1);
        }

        wait(300);
        
        //We expect clients 2 and 3 to reply with promises
        ArrayList<Message> passBack = server.ReadStringFromClient(2);
        assertEquals(passBack.get(0).type, Message.MessageType.Promise);
        server.SendStringToClient(passBack.get(0).toString(), 1);

        passBack = server.ReadStringFromClient(3);
        assertEquals(passBack.get(0).type, Message.MessageType.Promise);
        server.SendStringToClient(passBack.get(0).toString(), 1);

        wait(1500);

        //Now, the proposer, having been promised 3 values, will send a proposal with the initial value
        passOn = server.ReadStringFromClient(1);

        assertEquals(passOn.size(), 3);
        assertEquals(passOn.get(0).type, Message.MessageType.Propose);
        assertEquals(passOn.get(0).value, "ThreeClients");

        for (int i = 2;i<4;i++)
        {
            server.SendStringToClient(passOn.get(i-1).toString(), i);
        }

        wait(300);

        //Now, our two acceptors will accept
        passBack = server.ReadStringFromClient(2);
        assertEquals(passBack.get(0).type, Message.MessageType.Accept);
        server.SendStringToClient(passBack.get(0).toString(), 1);

        passBack = server.ReadStringFromClient(3);
        assertEquals(passBack.get(0).type, Message.MessageType.Accept);
        server.SendStringToClient(passBack.get(0).toString(), 1);

        wait(1500);

        //Now, our proposer will commit
        passOn = server.ReadStringFromClient(1);

        assertEquals(passOn.size(), 3);
        assertEquals(passOn.get(0).type, Message.MessageType.Commit);
        assertEquals(passOn.get(0).value, "ThreeClients");

        for (int i = 2;i<4;i++)
        {
            server.SendStringToClient(passOn.get(i-1).toString(), i);
        }

        //Now that proposer has committed it will stop
        proposer.join();
        
        //So will our two acceptors
        acceptor1.join();
        acceptor2.join();

    }
}
