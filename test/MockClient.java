package test;

import java.util.*;
import java.net.*;
import java.io.*;

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

    public String Join(int ID, String message)
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
            return line;
        }
        catch (Exception e)
        {
            System.out.println("Exception in test: " + e);
        }

        return "";
    }

    public void Message(int sender, int receiver, String value, String type)
    {
        out.println(" -r " + Integer.toString(receiver) + "; -s " + Integer.toString(sender) + "; -v " + value + "; -t " + type + "; -i 1;");
    }

    public void MessageString(String s)
    {
        out.println(s);
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
        }

        return "";
    }

    public void HeartBeat()
    {
        out.println("");
    }
}
