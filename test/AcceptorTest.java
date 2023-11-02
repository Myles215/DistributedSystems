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

public class AcceptorTest 
{
    static int startPort = 7634;

    void wait(int i)
    {
        try { Thread.sleep(i); } catch(InterruptedException e) { }
    }

    @Test
    public void RepliesToPrepare() throws InterruptedException
    {
        int port = startPort++;

        MockServer server = new MockServer(port, 1000);

        ClientThread client = new ClientThread(port, 1, null);
        client.start();

        assertEquals(server.AcceptConnection(), true);
        server.SendStringToClient("starting", 1);

        Message send = new Message(1, 2, "RepliesToThis", 12, Message.MessageType.Prepare);
        server.SendStringToClient(send.toString(), 1);

        wait(400);

        ArrayList<Message> check = server.ReadStringFromClient(1);

        //we will get 1 message and this message will be a promise
        assertEquals(check.size(), 1);
        assertEquals(check.get(0).type, Message.MessageType.Promise);
        assertEquals(check.get(0).value, "");

        client.SetCommittedValue();
        //server.Stop();
        client.join();
    }

    @Test
    public void RepliesToPropose() throws InterruptedException
    {
        int port = startPort++;

        MockServer server = new MockServer(port, 1000);

        ClientThread client = new ClientThread(port, 1, null);
        client.start();

        assertEquals(server.AcceptConnection(), true);
        server.SendStringToClient("starting", 1);

        wait(100);

        Message send = new Message(1, 2, "RepliesToThis", 12, Message.MessageType.Prepare);
        server.SendStringToClient(send.toString(), 1);

        wait(400);

        server.ReadStringFromClient(1);

        send = new Message(1, 2, "RepliesToThis", 12, Message.MessageType.Propose);
        server.SendStringToClient(send.toString(), 1);

        wait(400);

        ArrayList<Message> check = server.ReadStringFromClient(1);

        //we will get 1 message and this message will be a promise
        assertEquals(check.size(), 1);
        assertEquals(check.get(0).type, Message.MessageType.Accept);

        client.SetCommittedValue();
        client.join();
    }

    @Test
    public void RepliesWithAlternateValue() throws InterruptedException
    {
        int port = startPort++;

        MockServer server = new MockServer(port, 1000);

        ClientThread client = new ClientThread(port, 1, null);
        client.start();

        assertEquals(server.AcceptConnection(), true);
        server.SendStringToClient("starting", 1);

        wait(100);

        Message send = new Message(1, 2, "RepliesToThis", 12, Message.MessageType.Prepare);
        server.SendStringToClient(send.toString(), 1);

        wait(400);

        server.ReadStringFromClient(1);

        send = new Message(1, 2, "RepliesToThis", 12, Message.MessageType.Propose);
        server.SendStringToClient(send.toString(), 1);

        wait(400);

        server.ReadStringFromClient(1);

        send = new Message(1, 2, "DifferentValue", 12, Message.MessageType.Prepare);
        server.SendStringToClient(send.toString(), 1);

        wait(400);

        ArrayList<Message> check = server.ReadStringFromClient(1);

        //we will get 1 message and this message will be a promise
        assertEquals(check.size(), 1);
        assertEquals(check.get(0).type, Message.MessageType.Promise);
        assertEquals(check.get(0).value, "RepliesToThis");

        client.SetCommittedValue();
        client.join();
    }
}
