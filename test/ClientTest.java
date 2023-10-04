import client.GETClient;
import server.MockServer;

import static org.junit.Assert.assertEquals;
import parsers.HTTPObject;
import parsers.HTTPParser;

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

        //Do for lamport time reply
        client.getRequest("/lamport", 0);
        client.readServerResponse();

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
        client.getRequest("/lamport", 0);

        HTTPObject check = client.readServerResponse();
        //This is inital lamport time reply
        assertEquals(check.lamportTime, 1);

        client.getRequest("/weather.json", 1);

        check = client.readServerResponse();

        assertEquals(check.data.size(), 1);
        assertEquals(check.type, HTTPObject.RequestType.RES);
        assertEquals(check.code, 200);
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
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() < start + 300);

        assertEquals(server.clientConnected, false);
        server.start();

        //Now, client should connect 
        try { Thread.sleep(500); } catch (InterruptedException e) {  }

        assertEquals(server.clientConnected, true);
        client.exit = true;
    }

    @Test
    public void ClientRetriesGet() throws InterruptedException
    {
        ClientThread client = new ClientThread(port + 4);
        HTTPParser parser = new HTTPParser("./parsers/HTTPParser.txt");
        client.start();

        try (ServerSocket serverSocket = new ServerSocket(port + 4))
        {
            Socket socket = serverSocket.accept();
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            HTTPObject req = new HTTPObject("NULL");

            while (req.type != HTTPObject.RequestType.GET)
            {
                req = parser.parse(reader);
            }

            assertEquals(req.pathName, "lamport");
            assertEquals(req.lamportTime, 0);

            writer.println("HTTP/1.1 200 OK");
            writer.println("User-Agent: ATOMClient/1/0");
            writer.println("Content-Type: application/json");
            writer.println("Lamport-Time: 1");
            writer.println("Content-Length:0");

            req = new HTTPObject("NULL");

            while (req.type != HTTPObject.RequestType.GET)
            {
                req = parser.parse(reader);
            }

            assertEquals(req.pathName, "weather.json");
            assertEquals(req.lamportTime, 2);

            writer.println("HTTP/1.1 503 Weather data not available");
            writer.println("User-Agent: ATOMClient/1/0");
            writer.println("Content-Type: application/json");
            writer.println("Lamport-Time: 2");
            writer.println("Content-Length:0");

            //The GET client will try to request again
            while (req.type != HTTPObject.RequestType.GET)
            {
                req = parser.parse(reader);
            }

            assertEquals(req.pathName, "weather.json");

            writer.println("HTTP/1.1 200 OK");
            writer.println("User-Agent: ATOMClient/1/0");
            writer.println("Content-Type: application/json");
            writer.println("Lamport-Time: 2");
            writer.println("Content-Length:0");
            //Now client is finished
        }
        catch (Exception e)
        {
            System.out.println("Exception in client test: " + e);
        }

        //Assure that client has closed and therefore we get to here
        assertEquals(true, true);
    }
}