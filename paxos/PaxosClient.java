package paxos;

import java.util.*;
import java.net.*;
import java.io.*;

import java.nio.ByteBuffer; 
import java.nio.channels.SocketChannel; 

public class PaxosClient 
{
    //Connection variables
    private static SocketChannel connection;
    private static BufferedReader in = null;
    private static PrintStream out = null;

    //Paxos variables
    private int lamportTime = 0;
    private int acceptedTime = -1;
    private String acceptedValue = null;
    public static void main(String[] args)
    {
        if (args.length < 2) 
        {
            System.out.println("Client needs ID and port");
            return;
        }

        int ID = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);

        int retryCount = 0;
        Boolean connected = false;

        while (retryCount < 5 && !connected)
        {
            connected = Connect(port);
            retryCount++;

            if (!connected && retryCount < 5)
            {
                System.err.println("Failed connected to port " + port + " , will retry");
                try { Thread.sleep(100); } catch(Exception e) {}
            }
        }

        if (!connected) 
        {
            System.err.println("Failed to connect");
        }
        

        if (!AllocateSlot(ID))
        {
            //in.close();
            //out.close();
        }

        Boolean isProposer = false;
        String value = "";

        if (args.length > 2)
        {
            isProposer = true;
            value = args[2];
        }
        //check if we start with a proposed value
        //if yes, this client is a proposer

        //if proposer, prepare our value

        //if we get a quorum of promises for our value, send proposal for our value

        //if we don't get quorum, update ID and go back to start

        //send accept message and wait for commit then finalise
    }

    private static Boolean Connect(int port)
    {
        try
        {
            connection = SocketChannel.open( new InetSocketAddress("localhost", port));
        }
        catch (Exception e)
        {
            System.out.println("Exception when connecting: " + e);
            return false;
        }

        return true;
    }

    private static Boolean AllocateSlot(int ID)
    {
        long time = System.currentTimeMillis();
        String line = null;

        while (line == null && time + 1000 > System.currentTimeMillis())
        {
            line = ReadString();
        }

        if (line != null && line.equals("starting")) return true;

        return false;
    }

    private static Message ReadMessage()
    {
        return new Message(null);
    }

    private static String ReadString()
    {
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(256);
            connection.read(buffer);
            String line = new String(buffer.array()).trim();

            while (line.equals(""))
            {
                connection.read(buffer);
                line = new String(buffer.array()).trim();
            }

            return line;
        }
        catch (IOException e)
        {
            System.err.println("Error when reading server reply");
            e.printStackTrace();
        }

        return null;
    }

}
