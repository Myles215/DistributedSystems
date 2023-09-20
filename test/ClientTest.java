import client.GetClient;
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
    MockServer server = new MockServer();

    @Test
    public void connect() throws IOException
    {
        GetClient client = new GetClient();
        server.start();

        assertEquals(server.clientConnected, false);

        client.connect(1111);

        //Wait for server to update
        try { Thread.sleep(1000); } catch (InterruptedException e) { }

        assertEquals(server.clientConnected, true);
        client.disconnect();
    }

    @Test
    public void connectAndRequest() throws IOException, Exception
    {
        GetClient client = new GetClient();
        server.start();

        assertEquals(server.clientConnected, false);

        client.connect(1111);
        client.getRequest("/test");

        try { Thread.sleep(1000); } catch (InterruptedException e) { }

        assertEquals(server.clientMessage.type, HTTPObject.RequestType.GET);

        client.disconnect();
    }

    @Test
    public void messageAndResponse() throws IOException, Exception
    {
        GetClient client = new GetClient();
        server.start();

        assertEquals(server.clientConnected, false);

        client.connect(1111);
        client.getRequest("/test");

        ArrayList<String> check = client.readResponse();

        assertEquals(check.size(), 1);
        assertEquals(check.get(0), "Good job sending GET request!");
    }
}