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

    void wait(int i)
    {
        try { Thread.sleep(i); } catch(InterruptedException e) { }
    }

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

        server.Stop();
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

        wait(100);

        ArrayList<Message> check = server.ReadStringFromClient(1);

        //Proposer sends proposals to all clients
        //Therefore, 1 through 9 should be received

        for (int i = 1;i<10;i++)
        {
            assertEquals(check.get(i-1).receiver, i);
        }

        server.Stop();
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
        wait(2000);

        //Client should retry sending prepares
        ArrayList<Message> check = server.ReadStringFromClient(1);

        System.out.println(check);

        assertEquals(check.size(), 2);
        for (int i = 1;i<3;i++)
        {
            assertEquals(check.get(i-1).type, Message.MessageType.Prepare);
            assertEquals(check.get(i-1).timeID, 2);
            assertEquals(check.get(i-1).receiver, i);
        }

        server.Stop();
    }

    @Test 
    public void PrepareSucceedsProposerProposes()
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

        wait(150);

        for (int i = 1;i<3;i++)
        {
            //Promise the clients value
            Message reply = new Message(1, i, "", 2, Message.MessageType.Promise);
            server.SendStringToClient(reply.toString(), 1);
        }

        wait(1500);

        ArrayList<Message> check = server.ReadStringFromClient(1);

        System.out.println(check);

        assertEquals(check.size(), 2);
        for (int i = 1;i<3;i++)
        {
            //Message type should be propose
            assertEquals(check.get(i-1).type, Message.MessageType.Propose);
            //Message time should be 1
            assertEquals(check.get(i-1).timeID, 1);
            assertEquals(check.get(i-1).receiver, i);
        }

        server.Stop();
    }
}
