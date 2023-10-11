import server.ContentServer;
import parsers.HTTPObject;
import server.MockServer;

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

public class ContentServerTest
{
    ContentServer contentServer = new ContentServer();
    int port = 1324;
    MockServer server = new MockServer(port);

    @Test
    public void IdAndNameOnly() throws InterruptedException, IOException, Exception
    {
        server.start();

        String[] args = {"localhost:" + Integer.toString(port), "./test/testFiles/IDAndNameOnly.txt"};
        contentServer.main(args);

        Thread.sleep(1000);

        assertEquals(server.clientMessage.type, HTTPObject.RequestType.PUT);
        assertEquals(server.clientMessage.data.get(0), "{  \"id\" : \"Myles\" , \"name\" : \"Adelaide\" }");
    }

    @Test
    public void MultipleStrings() throws InterruptedException, IOException, Exception
    {
        server.start();

        String[] args = {"localhost:" + Integer.toString(port), "./test/testFiles/MultipleStrings.txt"};
        contentServer.main(args);

        Thread.sleep(1000);

        assertEquals(server.clientMessage.type, HTTPObject.RequestType.PUT);
        assertEquals(server.clientMessage.data.get(0), "{  \"name\" : \"Myles\" , \"id\" : \"TestID\" , \"state\" : \"SA\" }");
    }

    //@Test
    public void MultipleInts() throws InterruptedException, IOException, Exception
    {
        server.start();

        String[] args = {"localhost:" + Integer.toString(port), "./test/testFiles/MultipleInts.txt"};
        contentServer.main(args);

        Thread.sleep(1000);

        assertEquals(server.clientMessage.type, HTTPObject.RequestType.PUT);
        assertEquals(server.clientMessage.data.get(0), "{  \"lon\" : 50 , \"lat\" : -17 , \"air_temp\" : 18 , \"name\" : \"22\" }");
    }

    @Test
    public void NoID() throws InterruptedException, IOException, Exception
    {
        server.start();

        String[] args = {"localhost:" + Integer.toString(port), "./test/testFiles/NoID.txt"};
        contentServer.main(args);

        Thread.sleep(1000);

        assertEquals(server.clientMessage.type, HTTPObject.RequestType.NULL);
    }
    
}