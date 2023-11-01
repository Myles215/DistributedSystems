package test;

import java.util.*;
import java.net.*;
import java.io.*;

import java.nio.ByteBuffer; 
import java.nio.channels.SocketChannel; 

public class MockClient 
{
    SocketChannel conn;
    BufferedReader in;
    PrintStream out;

    public MockClient(int port) throws IOException
    {
        conn = SocketChannel.open( new InetSocketAddress("localhost", port));
    }

    public String Join(int ID, String message)
    {
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(512); 

            buffer.put(Integer.toString(ID).getBytes()); 
            buffer.flip(); 
            conn.write(buffer);

            buffer.clear();

            conn.read(buffer);
            String line = new String(buffer.array()).trim();

            while (line.equals(""))
            {
                conn.read(buffer);
                line = new String(buffer.array()).trim();
            }

            //Expect success or fail message from server
            return line;
        }
        catch (Exception e)
        {
            System.out.println("Exception in test: " + e);
            e.printStackTrace();
        }

        return "";
    }

    public void Message(int sender, int receiver, String value, String type)
    {
        String msg = " -r " + Integer.toString(receiver) + "; -s " + Integer.toString(sender) + "; -v " + value + "; -t " + type + "; -i 1;*";

        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(512);
            buffer.put(msg.getBytes()); 
            buffer.flip(); 

            conn.write(buffer);
        }
        catch (IOException e)
        {
            System.out.println("Error when writing to client");
            e.printStackTrace();
        }
    }

    public void MessageString(String s)
    {
        out.println(s);
    }

    public String Read()
    {
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(512);
            conn.read(buffer);
            String line = new String(buffer.array()).trim();

            while (line.equals(""))
            {
                conn.read(buffer);
                line = new String(buffer.array()).trim();
            }

            return line;
        }
        catch (IOException e)
        {
            System.err.println("Error when reading server reply");
            e.printStackTrace();
        }

        return "";
    }

    public void HeartBeat()
    {
        out.println("");
    }
}
