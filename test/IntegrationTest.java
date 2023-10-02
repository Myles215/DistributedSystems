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

    public void checkAllFields(JsonObject check)
    {
        assertEquals(check.mJsonMap.get("name"), "Myles");
        assertEquals(check.mJsonMap.get("id"), "IDS60901");
        assertEquals(check.mJsonMap.get("state"), "SA");
        assertEquals(check.mJsonMap.get("lat"), "-215");
        assertEquals(check.mJsonMap.get("lon"), "123");
        assertEquals(check.mJsonMap.get("local_date_time"), "15/04:00pm");
        assertEquals(check.mJsonMap.get("local_date_time_full"), "20230715160000");
        assertEquals(check.mJsonMap.get("air_temp"), "13.3");
        assertEquals(check.mJsonMap.get("apparent_t"), "9.5");
        assertEquals(check.mJsonMap.get("cloud"), "Very cloudy");
        assertEquals(check.mJsonMap.get("dewpt"), "5.7");
        assertEquals(check.mJsonMap.get("press"), "1023.9");
        assertEquals(check.mJsonMap.get("rel_hum"), "60");
        assertEquals(check.mJsonMap.get("wind_dir"), "S");
        assertEquals(check.mJsonMap.get("wind_spd_kmh"), "15");
        assertEquals(check.mJsonMap.get("wind_spd_kt"), "8");
    }

    @Test
    public void SendOneData() throws InterruptedException, IOException, Exception
    {
        startAll(startPort);

        String[] args = {"localhost:" + Integer.toString(startPort), "./test/testFiles/NameOnly.txt"};
        contentServer.main(args);

        //Wait for data to process
        Thread.sleep(100);
        getClient.connect(startPort);

        getClient.getRequest("/weather.json", 0);
        HTTPObject res = getClient.readResponse();

        JsonObject check = new JsonObject();
        check.StringToObject(res.data.get(0));

        assertEquals(res.data.size(), 1);
        assertEquals(check.mJsonMap.size(), 1);
        assertEquals(check.mJsonMap.get("name"), "Myles");
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

        getClient.getRequest("/weather.json", 0);
        HTTPObject res = getClient.readResponse();

        JsonObject check = new JsonObject();
        check.StringToObject(res.data.get(0));

        checkAllFields(check);
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
        getClient1.getRequest("/weather.json", 30);
        HTTPObject res = getClient1.readResponse();
        JsonObject check = new JsonObject();
        check.StringToObject(res.data.get(0));

        checkAllFields(check);

        getClient2.connect(startPort + 2);
        getClient2.getRequest("/weather.json", 0);
        res = getClient2.readResponse();
        check.StringToObject(res.data.get(0));

        checkAllFields(check);

        getClient3.connect(startPort + 2);
        getClient3.getRequest("/weather.json", 0);
        res = getClient3.readResponse();
        check.StringToObject(res.data.get(0));

        checkAllFields(check);
    }

    @Test
    public void MultipleContentMultipleClients() throws InterruptedException, IOException, Exception
    {
        startAll(startPort + 3);

        String[] args = {"localhost:" + Integer.toString(startPort + 3), "./test/testFiles/AllFields.txt"};
        contentServer.main(args);

        ContentServer content2 = new ContentServer();
        content2.main(args);

        ContentServer content3 = new ContentServer();
        content3.main(args);

        GETClient getClient1 = new GETClient();
        GETClient getClient2 = new GETClient();
        GETClient getClient3 = new GETClient();

        getClient1.connect(startPort + 3);

        getClient1.getRequest("/weather.json", 15);
        HTTPObject res = getClient1.readResponse();
        assertEquals(res.data.size(), 3);
        JsonObject check = new JsonObject();

        check.StringToObject(res.data.get(0));
        checkAllFields(check);

        check.StringToObject(res.data.get(1));
        checkAllFields(check);

        check.StringToObject(res.data.get(2));
        checkAllFields(check);


        getClient2.connect(startPort + 3);
        getClient2.getRequest("/weather.json", 16);
        res = getClient2.readResponse();
        assertEquals(res.data.size(), 3);
        check = new JsonObject();

        check.StringToObject(res.data.get(0));
        checkAllFields(check);

        check.StringToObject(res.data.get(1));
        checkAllFields(check);

        check.StringToObject(res.data.get(2));
        checkAllFields(check);


        getClient3.connect(startPort + 3);
        getClient3.getRequest("/weather.json", 17);
        res = getClient3.readResponse();
        assertEquals(res.data.size(), 3);
        check = new JsonObject();

        check.StringToObject(res.data.get(0));
        checkAllFields(check);

        check.StringToObject(res.data.get(1));
        checkAllFields(check);

        check.StringToObject(res.data.get(2));
        checkAllFields(check);
    }
}