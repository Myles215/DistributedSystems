import server.ThreadAggregationServer;
import server.ServerThread;
import client.MockClient;
import parsers.HTTPObject;

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

public class AggregationServerTest
{
    @Before
    public void resetFiles() throws InterruptedException
    {
        //Wait for all file readers to close
        Thread.sleep(25);

        File oldData = new File("./allData.txt");
        if (!oldData.delete());
        {
            System.out.println("Could not delete data, some error");
        }
    }
    
    int port = 1254;
    
    @Test
    public void clientConnect() throws InterruptedException, IOException, Exception
    {
        String[] args = {Integer.toString(port)};
        ThreadAggregationServer aggServer = new ThreadAggregationServer(args);
        aggServer.start();

        MockClient client = new MockClient();
        client.run(port);

        client.sendGetRequest("GET /weather.json HTTP/1.1");
        client.sendGetRequest("User-Agent: ATOMClient/1/0");
        client.sendGetRequest("Content-Type: application/json");
        //TODO
        client.sendGetRequest("Lamport-Time: 0");
        client.sendGetRequest("Content-Length:0");

        HTTPObject checkReply = client.readResponse();

        //We have no data in aggregation server yet
        assertEquals(checkReply.code, 200);
        assertEquals(checkReply.data.size(), 0);

        client.disconnect();
        aggServer.exit = true;
    }

    @Test
    public void GetClientAndContentServerConnect() throws InterruptedException, IOException, Exception
    {
        //Need to set new port to avoid exceptions
        String[] args = {Integer.toString(port + 1)};
        ThreadAggregationServer aggServer = new ThreadAggregationServer(args);
        aggServer.start();

        MockClient getClient = new MockClient();
        getClient.run(port + 1);

        MockClient contentServer = new MockClient();
        contentServer.run(port + 1);

        //Add content to server
        String content = "test content";
        contentServer.sendPutRequest("PUT /weather.json HTTP/1.1");
        contentServer.sendPutRequest("User-Agent: ATOMClient/1/0");
        contentServer.sendPutRequest("Content-Type: application/json");
        //TODO
        contentServer.sendPutRequest("Lamport-Time: 0");
        contentServer.sendPutRequest("Content-Length:" + content.length());
        contentServer.sendPutRequest(content);

        //Expect we get 201 as we're adding files for the first time
        HTTPObject checkReply = contentServer.readResponse();
        assertEquals(checkReply.code, 201);

        //Give time to update
        Thread.sleep(100);

        getClient.sendGetRequest("GET /weather.json HTTP/1.1");
        getClient.sendGetRequest("User-Agent: ATOMClient/1/0");
        getClient.sendGetRequest("Content-Type: application/json");
        //TODO
        getClient.sendGetRequest("Lamport-Time: 0");
        getClient.sendGetRequest("Content-Length:0");

        checkReply = getClient.readResponse();

        //We should have our placed data
        assertEquals(checkReply.code, 200);
        assertEquals(checkReply.data.size(), 1);
        assertEquals(checkReply.data.get(0), content);

        getClient.disconnect();
        contentServer.disconnect();
        aggServer.exit = true;
    }

    @Test
    public void ServerRestartRetainsData() throws InterruptedException, IOException, Exception
    {
        //Need to set new port to avoid exceptions
        String[] args = {Integer.toString(port + 2)};
        ThreadAggregationServer aggServer = new ThreadAggregationServer(args);
        aggServer.start();

        MockClient getClient = new MockClient();
        getClient.run(port + 2);

        MockClient contentServer = new MockClient();
        contentServer.run(port + 2);

        //Add content to server
        String content = "test content";
        contentServer.sendPutRequest("PUT /weather.json HTTP/1.1");
        contentServer.sendPutRequest("User-Agent: ATOMClient/1/0");
        contentServer.sendPutRequest("Content-Type: application/json");
        //TODO
        contentServer.sendPutRequest("Lamport-Time: 0");
        contentServer.sendPutRequest("Content-Length:" + content.length());
        contentServer.sendPutRequest(content);

        //Expect we get 200 back as we already have the file
        HTTPObject checkReply = contentServer.readResponse();
        assertEquals(checkReply.code, 201);

        //Give time to update
        Thread.sleep(100);

        getClient.sendGetRequest("GET /weather.json HTTP/1.1");
        getClient.sendGetRequest("User-Agent: ATOMClient/1/0");
        getClient.sendGetRequest("Content-Type: application/json");
        //TODO
        getClient.sendGetRequest("Lamport-Time: 0");
        getClient.sendGetRequest("Content-Length:0");

        checkReply = getClient.readResponse();

        //We should have our placed data
        assertEquals(checkReply.code, 200);
        assertEquals(checkReply.data.size(), 1);
        assertEquals(checkReply.data.get(0), content);

        getClient.disconnect();
        contentServer.disconnect();
        aggServer.exit = true;

        String[] args2 = {Integer.toString(port + 3)};
        ThreadAggregationServer aggServer2 = new ThreadAggregationServer(args2);
        aggServer2.start();

        getClient = new MockClient();
        getClient.run(port + 3);

        getClient.sendGetRequest("GET /weather.json HTTP/1.1");
        getClient.sendGetRequest("User-Agent: ATOMClient/1/0");
        getClient.sendGetRequest("Content-Type: application/json");
        //TODO
        getClient.sendGetRequest("Lamport-Time: 0");
        getClient.sendGetRequest("Content-Length:0");

        checkReply = getClient.readResponse();

        //We should have our placed data
        assertEquals(checkReply.code, 200);
        assertEquals(checkReply.data.size(), 1);
        assertEquals(checkReply.data.get(0), content);

        getClient.disconnect();
    }
}