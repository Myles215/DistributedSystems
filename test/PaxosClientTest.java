package test;

import paxos.PaxosClient;
import paxos.Message;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

public class PaxosClientTest {

    static int startPort = 1678;

    class ClientOnThread extends Thread
    {
        PaxosClient client;
        int port;
        int ID;
        String value;

        ClientOnThread(int p, int id, String val)
        {
            client = new PaxosClient();
            value = val;
            ID = id;
            port = p;
        }

        public void run()
        {
            if (value != null)
            {
                String[] args = {Integer.toString(ID), Integer.toString(port), value};
                client.main(args);
            }
            else
            {
                String[] args = {Integer.toString(ID), Integer.toString(port)};
                client.main(args);
            }
        }
    }

    @Test
    public void ClientJoinsSuccessfully()
    {
        int port = startPort++;

        MockServer server = new MockServer(port);

        ClientOnThread client = new ClientOnThread(port, 1, null);
        client.start();

        assertEquals(server.AcceptConnection(), true);
    }

    @Test
    public void ProposerProposes()
    {
        int port = startPort++;

        MockServer server = new MockServer(port);

        ClientOnThread client = new ClientOnThread(port, 1, "hi");
        client.start();

        assertEquals(server.AcceptConnection(), true);

        server.SendStringToClient("starting", 1);

        ArrayList<Message> check = server.ReadStringFromClient(1);

        //Proposer sends proposals to all clients
        //Therefore, 1 through 9 should be received

        //assertEquals(check.size(), 9);
        for (int i = 1;i<10;i++)
        {
            assertEquals(check.get(i-1).receiver, i);
        }
    }

    @Test
    public void PrepareFailsProposerRetries()
    {
        int port = startPort++;

        MockServer server = new MockServer(port);

        ClientOnThread client = new ClientOnThread(port, 1, "hi");
        client.start();

        assertEquals(server.AcceptConnection(), true);

        server.SendStringToClient("starting", 1);

        server.ReadStringFromClient(1);

        for (int i = 3;i<10;i++)
        {
            //Tell client that there are no client with ID 3 through 9
            Message reply = new Message(1, i, "", -1, Message.MessageType.NC);
            server.SendStringToClient(reply.toString(), 1);
        }
 
        //Wait for client to read and handle
        try { Thread.sleep(1000); } catch(InterruptedException e) {} 

        //Client should retry sending prepares
        ArrayList<Message> check = server.ReadStringFromClient(1);

        System.out.println("HMMMMM");

        assertEquals(check.size(), 2);
        for (int i = 1;i<3;i++)
        {
            assertEquals(check.get(i-1).type, Message.MessageType.Prepare);
            assertEquals(check.get(i-1).receiver, i);
        }
    }
}
