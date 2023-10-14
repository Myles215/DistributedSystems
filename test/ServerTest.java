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

public class ServerTest 
{
    public void Join(ThreadedServer server, Socket conn, int ID, int port, String message)
    {
        try
        {
            conn = new Socket("localhost", port);

            PrintStream out = new PrintStream(conn.getOutputStream());

            out.println("1");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = in.readLine();

            while (line == null)
            {
                line = in.readLine();
            }

            //Expect success or fail message from server
            System.out.println(line);
            assertEquals(line, message);
        }
        catch (Exception e)
        {
            System.out.println("Exception in test: " + e);
        }
    }

    @Test
    public void ClientJoinsSuccessfully()
    {
        Socket conn1 = new Socket();
        int port = 1254;

        ThreadedServer server = new ThreadedServer(port);
        server.start();

        Join(server, conn1, 1, port, "starting");

        server.interrupt();
    }

    @Test
    public void DuplicateId()
    {
        Socket conn1 = new Socket();
        int port = 1255;

        ThreadedServer server = new ThreadedServer(port);
        server.start();

        Join(server, conn1, 1, port, "starting");

        Socket conn2 = new Socket();

        //This connect should fail as we already had a client with ID 1 join
        Join(server, conn2, 1, port, "client with this ID already exists");
    }
}
