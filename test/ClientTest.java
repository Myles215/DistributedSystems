package client;
import client.GetClient;
import server.TestServer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.*;
import java.net.*;
import java.io.*;

public class ClientTest
{   
    @Test
    public void connect()
    {
        GetClient client = new GetClient();
        TestServer server = new TestServer();
        server.startup(1111);

        assertEquals(server.clientConnected, false);

        client.connect(1111);

        assertEquals(server.clientConnected, true);
    }
}