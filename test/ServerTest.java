import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.*;
import java.net.*;
import java.io.*;

import paxos.Message;

public class ServerTest 
{
    //We use this static start port and increment each time
    static int startPort = 1254;

    public void Join(BufferedReader in, PrintStream out, int ID, String message)
    {
        try
        {
            out.println(Integer.toString(ID));

            String line = in.readLine();

            while (line == null)
            {
                line = in.readLine();
            }

            //Expect success or fail message from server
            assertEquals(line, message);
        }
        catch (Exception e)
        {
            System.out.println("Exception in test: " + e);
        }
    }

    public void Message(PrintStream out, int sender, int receiver, String value, String type)
    {
        out.println(" -r " + Integer.toString(receiver) + "; -s " + Integer.toString(sender) + "; -v " + value + "; -t " + type + ";");
    }

    public String Read(BufferedReader in)
    {
        try
        {
            String line = in.readLine();
            while (line == null) line = in.readLine();

            return line;
        }
        catch (IOException e)
        {
            assertEquals(true, false);
        }

        return "";
    }

    @Test
    public void ClientJoinsSuccessfully() throws InterruptedException, IOException
    {
        int port = startPort++;

        ThreadedServer server = new ThreadedServer(port);
        server.start();

        MockClient conn1 = new MockClient(port);

        conn1.Join(1, "starting");

        Thread.sleep(1000);
    }

    @Test
    public void DuplicateId() throws IOException
    {
        int port = startPort++;

        ThreadedServer server = new ThreadedServer(port);
        server.start();

        MockClient conn1 = new MockClient(port);
        conn1.Join(1, "starting");

        MockClient conn2 = new MockClient(port);
        conn2.Join(1, "client with this ID already exists");
    }

    @Test
    public void TwoClientsSuccessfullyJoin() throws IOException
    {
        int port = startPort++;

        ThreadedServer server = new ThreadedServer(port);
        server.start();

        MockClient conn1 = new MockClient(port);
        conn1.Join(1, "starting");

        MockClient conn2 = new MockClient(port);
        conn2.Join(1, "client with this ID already exists");
    }

    @Test
    public void TwoClientsCanMessage() throws InterruptedException, IOException
    {
        int port = startPort++;

        ThreadedServer server = new ThreadedServer(port);
        server.start();

        MockClient conn1 = new MockClient(port);
        conn1.Join(1, "starting");

        MockClient conn2 = new MockClient(port);
        conn2.Join(2, "starting");

        conn1.Message(1, 2, "hi", "Propose");
        //Need to do this to break the blocking call
        Thread.sleep(50);
        conn2.HeartBeat();

        String reply = conn2.Read();

        Message rep = new Message(reply);

        assertEquals(rep.receiver, 2);
        assertEquals(rep.sender, 1);
        assertEquals(rep.value, "hi");
    }

    @Test
    public void TwoClientsCanCommunicateWell() throws InterruptedException, IOException
    {
        int port = startPort++;

        ThreadedServer server = new ThreadedServer(port);
        server.start();

        MockClient conn1 = new MockClient(port);
        conn1.Join(1, "starting");

        MockClient conn2 = new MockClient(port);
        conn2.Join(2, "starting");

        conn1.Message(1, 2, "hi", "Prepare");
        //Need to do this to break the blocking call
        Thread.sleep(50);
        conn2.HeartBeat();

        String reply = conn2.Read();
        Message rep = new Message(reply);

        assertEquals(rep.receiver, 2);
        assertEquals(rep.sender, 1);
        assertEquals(rep.value, "hi");

        conn2.Message(2, 1, "hi", "Promise");

        Thread.sleep(50);
        conn1.HeartBeat();

        rep = new Message(conn1.Read());

        assertEquals(rep.receiver, 1);
        assertEquals(rep.sender, 2);
        assertEquals(rep.value, "hi");

        conn1.Message(1, 2, "nice!", "Commit");

        Thread.sleep(50);
        conn2.HeartBeat();

        rep = new Message(conn2.Read());

        assertEquals(rep.receiver, 2);
        assertEquals(rep.sender, 1);
        assertEquals(rep.value, "nice!");
    }
}
