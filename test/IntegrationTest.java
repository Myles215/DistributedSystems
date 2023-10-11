import client.GETClient;
import server.AggregationServer;
import server.ContentServer;
import parsers.HTTPObject;
import parsers.JsonObject;

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

public class IntegrationTest
{
    @Before
    public void resetFiles() throws InterruptedException
    {
        //Wait for all file readers to close
        Thread.sleep(25);
        File oldData = new File("./allData.txt");
        oldData.delete();
    }

    GETClient getClient = new GETClient();
    ContentServer contentServer = new ContentServer();
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

    int startPort = 5001;

    public void startAll(int p) throws InterruptedException, IOException, Exception
    {
        RunAggregationServer runAgg = new RunAggregationServer(p);

        runAgg.start();

        //Wait for agg server to start
        Thread.sleep(100);
    }

    //Name is the only unique part
    public void checkAllFields(Map<String, String> check, String name)
    {
        assertEquals(check.get("name"), name);
        assertEquals(check.get("id"), "IDS60901");
        assertEquals(check.get("state"), "SA");
        assertEquals(check.get("lat"), "-215");
        assertEquals(check.get("lon"), "123");
        assertEquals(check.get("local_date_time"), "15/04:00pm");
        assertEquals(check.get("local_date_time_full"), "20230715160000");
        assertEquals(check.get("air_temp"), "13.3");
        assertEquals(check.get("apparent_t"), "9.5");
        assertEquals(check.get("cloud"), "Very cloudy");
        assertEquals(check.get("dewpt"), "5.7");
        assertEquals(check.get("press"), "1023.9");
        assertEquals(check.get("rel_hum"), "60");
        assertEquals(check.get("wind_dir"), "S");
        assertEquals(check.get("wind_spd_kmh"), "15");
        assertEquals(check.get("wind_spd_kt"), "8");
    }

    @Test
    public void SendOneData() throws InterruptedException, IOException, Exception
    {
        startAll(startPort);

        String[] args = {"localhost:" + Integer.toString(startPort), "./test/testFiles/IDAndNameOnly.txt"};
        contentServer.main(args);

        //Wait for data to process
        Thread.sleep(100);
        getClient.connect(startPort);

        getClient.getRequest("/lamport", 0);
        HTTPObject time = getClient.readServerResponse();

        getClient.getRequest("/weather.json", time.lamportTime);
        HTTPObject res = getClient.readServerResponse();

        JsonObject check = new JsonObject();

        assertEquals(res.data.size(), 3);

        //Strip our object into JSON strings
        check.NestedStringToObject(res.data.get(0) + res.data.get(1) + res.data.get(2));

        assertEquals(check.mObject.size(), 1);
        assertEquals(check.mObject.containsKey("Adelaide"), true);
        //Individual JSON will be stored in JsonMap
        assertEquals(check.mJsonMap.get("id"), "Myles");
        assertEquals(check.mJsonMap.get("name"), "Adelaide");
    }

    @Test
    public void SendAllFields() throws InterruptedException, IOException, Exception
    {
        startAll(startPort + 1);

        String[] args = {"localhost:" + Integer.toString(startPort + 1), "./test/testFiles/AllFields.txt"};
        contentServer.main(args);

        //Wait for data to process
        Thread.sleep(100);
        getClient.connect(startPort + 1);

        getClient.getRequest("/lamport", 0);
        HTTPObject time = getClient.readServerResponse();

        getClient.getRequest("/weather.json", time.lamportTime);
        HTTPObject res = getClient.readServerResponse();

        JsonObject check = new JsonObject();
        //Strip our object into JSON strings
        check.NestedStringToObject(res.data.get(0) + res.data.get(1) + res.data.get(2));

        assertEquals(check.mObject.size(), 1);
        assertEquals(check.mObject.containsKey("Myles"), true);

        checkAllFields(check.mObject.get("Myles"), "Myles");
    }

    @Test
    public void MultipleClients() throws InterruptedException, IOException, Exception
    {
        startAll(startPort + 2);

        String[] args = {"localhost:" + Integer.toString(startPort + 2), "./test/testFiles/AllFields.txt"};
        contentServer.main(args);

        GETClient getClient1 = new GETClient();
        GETClient getClient2 = new GETClient();
        GETClient getClient3 = new GETClient();

        getClient1.connect(startPort + 2);

        //Use a super late timestamp to avoid this being handled before above
        getClient1.getRequest("/lamport", 0);
        HTTPObject time = getClient.readServerResponse();

        getClient1.getRequest("/weather.json", time.lamportTime);
        HTTPObject res = getClient1.readServerResponse();
        JsonObject check = new JsonObject();
        String json = "";

        for (String j : res.data) json += j;

        check.NestedStringToObject(json);

        assertEquals(check.mObject.size(), 1);
        assertEquals(check.mObject.containsKey("Myles"), true);

        checkAllFields(check.mObject.get("Myles"), "Myles");

        getClient2.connect(startPort + 2);

        getClient2.getRequest("/lamport", 0);
        time = getClient2.readServerResponse();

        getClient2.getRequest("/weather.json", time.lamportTime);
        res = getClient2.readServerResponse();

        json = "";

        for (String j : res.data) json += j;

        check.NestedStringToObject(json);

        assertEquals(check.mObject.size(), 1);
        assertEquals(check.mObject.containsKey("Myles"), true);

        checkAllFields(check.mObject.get("Myles"), "Myles");
        
        getClient3.connect(startPort + 2);

        getClient3.getRequest("/lamport", 0);
        time = getClient3.readServerResponse();

        getClient3.getRequest("/weather.json", time.lamportTime);
        res = getClient3.readServerResponse();
        json = "";

        for (String j : res.data) json += j;

        check.NestedStringToObject(json);

        assertEquals(check.mObject.size(), 1);
        assertEquals(check.mObject.containsKey("Myles"), true);

        checkAllFields(check.mObject.get("Myles"), "Myles");
    }

    @Test
    public void MultipleContentMultipleClients() throws InterruptedException, IOException, Exception
    {
        startAll(startPort + 3);

        //We need different files as each data entry is mapped to name, so to have 3 entries we need 3 names
        String[] args = {"localhost:" + Integer.toString(startPort + 3), "./test/testFiles/AllFields.txt"};
        contentServer.main(args);

        ContentServer content2 = new ContentServer();
        String[] args2 = {"localhost:" + Integer.toString(startPort + 3), "./test/testFiles/AllFieldsMelbourne.txt"};
        content2.main(args2);

        ContentServer content3 = new ContentServer();
        String[] args3 = {"localhost:" + Integer.toString(startPort + 3), "./test/testFiles/AllFieldsAdelaide.txt"};
        content3.main(args3);

        GETClient getClient1 = new GETClient();
        GETClient getClient2 = new GETClient();
        GETClient getClient3 = new GETClient();

        getClient1.connect(startPort + 3);

        getClient1.getRequest("/lamport", 0);
        HTTPObject time = getClient.readServerResponse();

        getClient1.getRequest("/weather.json", time.lamportTime);
        HTTPObject res = getClient1.readServerResponse();
        //Assume we have JSON formatted message size
        assertEquals(res.data.size(), 5);
        JsonObject check = new JsonObject();

        String json = "";

        for (String j : res.data) json += j;

        check.NestedStringToObject(json);

        assertEquals(check.mObject.size(), 3);
        assertEquals(check.mObject.containsKey("Myles"), true);
        assertEquals(check.mObject.containsKey("Melbourne"), true);
        assertEquals(check.mObject.containsKey("Adelaide"), true);

        checkAllFields(check.mObject.get("Myles"), "Myles");
        checkAllFields(check.mObject.get("Melbourne"), "Melbourne");
        checkAllFields(check.mObject.get("Adelaide"), "Adelaide");

        getClient2.connect(startPort + 3);

        getClient2.getRequest("/lamport", 0);
        time = getClient2.readServerResponse();

        getClient2.getRequest("/weather.json", time.lamportTime);
        res = getClient2.readServerResponse();
        check = new JsonObject();

        json = "";

        for (String j : res.data) json += j;

        check.NestedStringToObject(json);

        assertEquals(check.mObject.size(), 3);
        assertEquals(check.mObject.containsKey("Myles"), true);
        assertEquals(check.mObject.containsKey("Melbourne"), true);
        assertEquals(check.mObject.containsKey("Adelaide"), true);

        checkAllFields(check.mObject.get("Myles"), "Myles");
        checkAllFields(check.mObject.get("Melbourne"), "Melbourne");
        checkAllFields(check.mObject.get("Adelaide"), "Adelaide");

        getClient3.connect(startPort + 3);

        getClient3.getRequest("/lamport", 0);
        time = getClient3.readServerResponse();

        getClient3.getRequest("/weather.json", time.lamportTime);
        res = getClient3.readServerResponse();
        check = new JsonObject();

        json = "";

        for (String j : res.data) json += j;

        check.NestedStringToObject(json);

        assertEquals(check.mObject.size(), 3);
        assertEquals(check.mObject.containsKey("Myles"), true);
        assertEquals(check.mObject.containsKey("Melbourne"), true);
        assertEquals(check.mObject.containsKey("Adelaide"), true);

        checkAllFields(check.mObject.get("Myles"), "Myles");
        checkAllFields(check.mObject.get("Melbourne"), "Melbourne");
        checkAllFields(check.mObject.get("Adelaide"), "Adelaide");
    }

    @Test
    public void ContentServerSendsSameContent() throws IOException, InterruptedException, Exception
    {
        startAll(startPort + 4);

        //We need different files as each data entry is mapped to name, so to have 3 entries we need 3 names
        String[] args = {"localhost:" + Integer.toString(startPort + 4), "./test/testFiles/AllFields.txt"};
        contentServer.main(args);

        ContentServer content2 = new ContentServer();
        String[] args2 = {"localhost:" + Integer.toString(startPort + 4), "./test/testFiles/AllFields.txt"};
        content2.main(args2);

        ContentServer content3 = new ContentServer();
        String[] args3 = {"localhost:" + Integer.toString(startPort + 4), "./test/testFiles/AllFields.txt"};
        content3.main(args3);

        GETClient getClient1 = new GETClient();
        getClient1.connect(startPort + 4);

        getClient1.getRequest("/lamport", 0);
        HTTPObject time = getClient.readServerResponse();

        getClient1.getRequest("/weather.json", time.lamportTime);
        HTTPObject res = getClient1.readServerResponse();
        
        JsonObject check = new JsonObject();

        String json = "";

        for (String j : res.data) json += j;

        check.NestedStringToObject(json);
        //We should replace each entry with another as they all have same name
        assertEquals(check.mObject.size(), 1);
        assertEquals(check.mObject.containsKey("Myles"), true);

        checkAllFields(check.mObject.get("Myles"), "Myles");
    }

    @Test
    public void ServerRestartRetainsData() throws IOException, InterruptedException, Exception
    {
        startAll(startPort + 5);

        String[] args = {"localhost:" + Integer.toString(startPort + 4), "./test/testFiles/IDAndNameOnly.txt"};
        contentServer.main(args);

        //Wait for data to process
        Thread.sleep(100);
        getClient.connect(startPort + 5);

        getClient.getRequest("/lamport", 0);
        HTTPObject time = getClient.readServerResponse();

        getClient.getRequest("/weather.json", time.lamportTime);
        HTTPObject res = getClient.readServerResponse();

        JsonObject check = new JsonObject();
        String json = "";

        for (String j : res.data) json += j;

        check.NestedStringToObject(json);
        //We should replace each entry with another as they all have same name
        assertEquals(check.mObject.size(), 1);
        assertEquals(check.mObject.containsKey("Adelaide"), true);
        assertEquals(check.mObject.get("Adelaide").size(), 2); 

        startAll(startPort + 6);

        //Data should be saved from last time
        getClient.connect(startPort + 6);

        getClient.getRequest("/lamport", time.lamportTime);
        time = getClient.readServerResponse();

        getClient.getRequest("/weather.json", time.lamportTime);
        res = getClient.readServerResponse();

        check = new JsonObject();
        json = "";

        for (String j : res.data) json += j;

        check.NestedStringToObject(json);
        //We should replace each entry with another as they all have same name
        assertEquals(check.mObject.size(), 1);
        assertEquals(check.mObject.containsKey("Adelaide"), true);
        assertEquals(check.mObject.get("Adelaide").size(), 2); 
    }
}