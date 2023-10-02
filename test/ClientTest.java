import client.GETClient;
import server.MockServer;

import static org.junit.Assert.assertEquals;
import parsers.HTTPObject;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.*;
import java.net.*;
import java.io.*;

public class ClientTest
{   

    private int port = 1111;

    class ClientThread extends Thread
    {
        int portT;
        public Boolean exit = false;

        ClientThread(int p)
        {
            portT = p;
        }

        public void run()
        {
            GETClient getClient = new GETClient();
            String[] args = {"localhost:" + Integer.toString(portT)};
            try
            {
                getClient.main(args);
            } 
            catch (Exception e)
            {
                System.out.println("Some exception in test: " + e);
            }

            while (!exit) {}
        }
    }

    @Test
    public void connect() throws IOException
    {
        MockServer server = new MockServer(port);
        GETClient client = new GETClient();
        server.start();

        assertEquals(server.clientConnected, false);

        client.connect(port);

        //Wait for server to update
        try { Thread.sleep(100); } catch (InterruptedException e) { }

        assertEquals(server.clientConnected, true);
        client.disconnect();
    }

    @Test
    public void connectAndRequest() throws IOException, Exception
    {
        MockServer server = new MockServer(port + 1);
        GETClient client = new GETClient();
        server.start();

        assertEquals(server.clientConnected, false);

        client.connect(port + 1);
        client.getRequest("/weather.json", 0);

        try { Thread.sleep(1000); } catch (InterruptedException e) { }

        assertEquals(server.clientMessage.type, HTTPObject.RequestType.GET);

        client.disconnect();
    }

    @Test
    public void messageAndResponse() throws IOException, Exception
    {
        MockServer server = new MockServer(port + 2);
        GETClient client = new GETClient();
        server.start();

        assertEquals(server.clientConnected, false);

        client.connect(port + 2);
        client.getRequest("/weather.json", 0);

        HTTPObject check = client.readResponse();

        assertEquals(check.data.size(), 1);
        assertEquals(check.data.get(0), "{ Good job sending GET request! }");
        client.disconnect();
    }

    @Test
    public void RetryConnection() throws IOException, Exception
    {
        MockServer server = new MockServer(port + 3);
        //Need to multithread this so we can run client then server in parallel
        ClientThread client = new ClientThread(port + 3);

        client.start();

        //Wait to start server, client should keep retrying for 500 ms
        try { Thread.sleep(300); } catch (InterruptedException e) { }

        assertEquals(server.clientConnected, false);
        server.start();

        //Now, client should connect 
        try { Thread.sleep(1000); } catch (InterruptedException e) {  }

        assertEquals(server.clientConnected, true);
        client.exit = true;
    }
}