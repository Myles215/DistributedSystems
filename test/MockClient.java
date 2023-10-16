package test;

import java.util.*;
import java.net.*;
import java.io.*;

import static org.junit.Assert.assertEquals;

public class MockClient 
{
    Socket conn;
    BufferedReader in;
    PrintStream out;

    public MockClient(int port) throws IOException
    {
        conn = new Socket("localhost", port);
        out = new PrintStream(conn.getOutputStream());
        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    public void Join(int ID, String message)
    {
        try
        {
            out.println(Integer.toString(ID));

            String line = in.readLine();

            while (line == null)
            {
                line = in.readLine();
            }

            //Expect success or fail message from server
            assertEquals(line, message);
        }
        catch (Exception e)
        {
            System.out.println("Exception in test: " + e);
        }
    }

    public void Message(int sender, int receiver, String value, String type)
    {
        out.println(" -r " + Integer.toString(receiver) + "; -s " + Integer.toString(sender) + "; -v " + value + "; -t " + type + ";");
    }

    public String Read()
    {
        try
        {
            String line = in.readLine();
            while (line == null) line = in.readLine();

            return line;
        }
        catch (IOException e)
        {
            assertEquals(true, false);
        }

        return "";
    }

    public void HeartBeat()
    {
        out.println("");
    }
}
