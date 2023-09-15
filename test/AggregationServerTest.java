import server.AggregationServer;
import client.MockClient;

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
    AggregationServer aggServer = new AggregationServer();
    int port = 1254;
    String[] args = {Integer.toString(port)};

    @Test
    public void clientConnect()
    {
        aggServer.main(args);
        assertEquals(aggServer.threads.size(), 0);

        MockClient client = new MockClient();

        client.connect(port);

        System.out.println("Hmm");

        assertEquals(aggServer.threads.size(), 1);
    }
}