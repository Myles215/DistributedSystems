package json;

import parsers.HTTPObject;
import client.MockClient;
import server.AggregationServer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.*;
import java.net.*;
import java.io.*;

public class LamportClockTest
{
    //The gist of these tests is to check that we get some kind of deterministic order of operations
    //from the aggregation server

    @Before
    public void resetFiles()
    {
        File oldData = new File("./allData.txt");
        if (!oldData.delete())
        {
            System.out.println("Could not delete data, some error");
        }
    }

    AggregationServer aggregationServer;

    public class RunAggregationServer extends Thread
    {
        int port;

        RunAggregationServer(int p)
        {
            port = p;
        }

        public void run()
        {
            aggregationServer = new AggregationServer();
            String[] args = {Integer.toString(port)};
            aggregationServer.main(args);
        }
    }

    int startPort = 6001;

    public void startAll(int p) throws InterruptedException, IOException, Exception
    {
        RunAggregationServer runAgg = new RunAggregationServer(p);

        runAgg.start();

        //Wait for agg server to start
        Thread.sleep(100);
    }

    //@Test
    public void OneClientOneServer() throws IOException, InterruptedException, Exception
    {
        startAll(startPort);

        MockClient getClient = new MockClient();
        MockClient contentServer = new MockClient();

        getClient.connect(startPort);
        contentServer.connect(startPort);

        //We will send a lamport time of 3 then a lamport time of 1, we expect the req with time 1 will be completed before req with time 3
        getClient.sendGetRequest("GET /weather.json HTTP/1.1");
        getClient.sendGetRequest("User-Agent: ATOMClient/1/0");
        getClient.sendGetRequest("Content-Type: application/json");
        getClient.sendGetRequest("Lamport-Time: 3");
        getClient.sendGetRequest("Content-Length:0");

        //Add content to server with earlier timestamp than get
        String content = "test content";
        contentServer.sendPutRequest("PUT /weather.json HTTP/1.1");
        contentServer.sendPutRequest("User-Agent: ATOMClient/1/0");
        contentServer.sendPutRequest("Content-Type: application/json");
        contentServer.sendPutRequest("Lamport-Time: 1");
        contentServer.sendPutRequest("Content-Length:" + content.length());
        contentServer.sendPutRequest(content);

        HTTPObject checkReply = getClient.readResponse();

        //We should have our placed data from timestamp 3
        assertEquals(checkReply.code, 200);
        assertEquals(checkReply.data.size(), 1);
        assertEquals(checkReply.data.get(0), content);

    }

}