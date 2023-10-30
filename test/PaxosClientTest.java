package test;

import paxos.PaxosClient;

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
}
