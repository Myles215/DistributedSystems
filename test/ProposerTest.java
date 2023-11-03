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

public class ProposerTest {

    static int startPort = 1678;

    void wait(int i)
    {
        try { Thread.sleep(i); } catch(InterruptedException e) { }
    }

    public class ClientThread extends Thread
    {
        PaxosClient client;
        int port;
        int ID;
        String value;

        ClientThread(int p, int id, String val)
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

        public String CommittedValue()
        {
            return client.committed;
        }

        public void SetCommittedValue()
        {
            //Client is pissing me off so I just want it to stop!
            while (CommittedValue() == null) client.committed = "Fucking stop";
        }
    }

    @Test
    public void ClientJoinsSuccessfully() throws InterruptedException
    {
        int port = startPort++;

        MockServer server = new MockServer(port, 1000);

        ClientThread client = new ClientThread(port, 1, null);
        client.start();

        assertEquals(server.AcceptConnection(), true);

        client.SetCommittedValue();
        server.SendStringToClient("starting", 1);
        client.join();
    }

    @Test
    public void Proposes() throws InterruptedException
    {
        int port = startPort++;

        MockServer server = new MockServer(port, 1000);

        ClientThread client = new ClientThread(port, 1, "Proposes");
        client.start();

        assertEquals(server.AcceptConnection(), true);

        server.SendStringToClient("starting", 1);

        wait(200);

        ArrayList<Message> check = server.ReadStringFromClient(1);

        //Proposer sends proposals to all clients
        //Therefore, 1 through 9 should be received

        for (int i = 1;i<10;i++)
        {
            assertEquals(check.get(i-1).receiver, i);
        }

        wait(1000);
        client.SetCommittedValue();
        client.join();

        server.Stop();
    }

    @Test
    public void PrepareFailsThenRetries() throws InterruptedException
    {
        int port = startPort++;

        MockServer server = new MockServer(port, 4000);

        ClientThread client = new ClientThread(port, 1, "PFTR");
        client.start();

        assertEquals(server.AcceptConnection(), true);

        server.SendStringToClient("starting", 1);

        wait(100);

        server.ReadStringFromClient(1);

        for (int i = 3;i<10;i++)
        {
            //Tell client that there are no client with ID 3 through 9
            Message reply = new Message(1, i, "", -1, Message.MessageType.NC);
            server.SendStringToClient(reply.toString(), 1);
        }

        wait(500);

        client.SetCommittedValue();

        //Wait for client to read and handle
        wait(1500);

        //Client should retry sending prepares
        ArrayList<Message> check = server.ReadStringFromClient(1);

        assertEquals(check.size(), 2);
        for (int i = 1;i<3;i++)
        {
            assertEquals(check.get(i-1).type, Message.MessageType.Prepare);
            //Try number = 2, ID = 1 so time ID = 21
            assertEquals(check.get(i-1).timeID, 21);
            assertEquals(check.get(i-1).receiver, i);
        }

        client.join();
    }

    @Test 
    public void PrepareSucceedsThenProposes() throws InterruptedException
    {
        int port = startPort++;

        MockServer server = new MockServer(port, 6000);

        ClientThread client = new ClientThread(port, 1, "PSTP");
        client.start();

        assertEquals(server.AcceptConnection(), true);

        server.SendStringToClient("starting", 1);

        wait(100);

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
            Message reply = new Message(1, i, "", 10 + i, Message.MessageType.Promise);
            server.SendStringToClient(reply.toString(), 1);
        }

        client.SetCommittedValue();

        wait(2000);

        ArrayList<Message> check = server.ReadStringFromClient(1);

        assertEquals(check.size(), 2);
        for (int i = 1;i<3;i++)
        {
            //Message type should be propose
            assertEquals(check.get(i-1).type, Message.MessageType.Propose);
            //Message time should be 1
            assertEquals(check.get(i-1).time, 1);
            assertEquals(check.get(i-1).receiver, i);
        }

        client.join();
    }

    @Test 
    public void PrepareReturnsAlternateValue() throws InterruptedException
    {
        int port = startPort++;

        MockServer server = new MockServer(port, 0);

        ClientThread client = new ClientThread(port, 1, "PRAV");
        client.start();

        assertEquals(server.AcceptConnection(), true);

        server.SendStringToClient("starting", 1);

        wait(100);
        server.ReadStringFromClient(1);

        for (int i = 3;i<10;i++)
        {
            //Tell client that there are no client with ID 3 through 9
            Message reply = new Message(1, i, "", -1, Message.MessageType.NC);
            server.SendStringToClient(reply.toString(), 1);
        }

        wait(150);

        //Promise the client a new value
        Message reply = new Message(1, 2, "", 12, Message.MessageType.Promise);
        server.SendStringToClient(reply.toString(), 1);
        
        //Same time and higher rank (lower) means we will update the value
        reply = new Message(1, 1, "newValue", 11, Message.MessageType.Promise);
        server.SendStringToClient(reply.toString(), 1);

        client.SetCommittedValue();

        wait(2000);

        ArrayList<Message> check = server.ReadStringFromClient(1);

        assertEquals(check.size(), 2);
        for (int i = 1;i<3;i++)
        {
            //Message type should be propose
            assertEquals(check.get(i-1).type, Message.MessageType.Propose);
            //Message time should be 1
            assertEquals(check.get(i-1).time, 1);
            assertEquals(check.get(i-1).receiver, i);
            assertEquals(check.get(i-1).value, "newValue");
        }

        client.join();
    }

    @Test
    public void ProposalSucceeds() throws InterruptedException
    {
        int port = startPort++;

        MockServer server = new MockServer(port, 6000);

        ClientThread client = new ClientThread(port, 1, "hi");
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
            Message reply = new Message(1, i, "", 10 + i, Message.MessageType.Promise);
            server.SendStringToClient(reply.toString(), 1);
        }

        wait(2000);
        server.ReadStringFromClient(1);

        for (int i = 1;i<3;i++)
        {
            //Accept the clients value
            Message reply = new Message(1, i, "", 10 + i, Message.MessageType.Accept);
            server.SendStringToClient(reply.toString(), 1);
        }

        wait(2500);

        //Client has now committed value
        assertEquals(client.CommittedValue(), "hi");

        server.Stop();
        client.join();
    }

    
}
