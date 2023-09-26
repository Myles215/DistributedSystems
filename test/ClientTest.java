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
    MockServer server = new MockServer(port);

    @Test
    public void connect() throws IOException
    {
        GETClient client = new GETClient();
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
        GETClient client = new GETClient();
        server.start();

        assertEquals(server.clientConnected, false);

        client.connect(1111);
        client.getRequest("/weather.json");

        try { Thread.sleep(1000); } catch (InterruptedException e) { }

        assertEquals(server.clientMessage.type, HTTPObject.RequestType.GET);

        client.disconnect();
    }

    @Test
    public void messageAndResponse() throws IOException, Exception
    {
        GETClient client = new GETClient();
        server.start();

        assertEquals(server.clientConnected, false);

        client.connect(1111);
        client.getRequest("/weather.json");

        HTTPObject check = client.readResponse();

        assertEquals(check.data.size(), 1);
        assertEquals(check.data.get(0), "Good job sending GET request!");
    }
}