package paxos;

import java.util.*;
import java.net.*;
import java.io.*;

public class PaxosClient 
{
    //Connection variables
    private static Socket connection;
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

        Connect(port);

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
            connection = new Socket("localhost", port);
            out = new PrintStream(connection.getOutputStream());
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }
        catch (Exception e)
        {
            System.out.println("Exception when connecting: " + e);
            connection = null;
            out = null;
            in = null;
            return false;
        }

        return true;
    }

    private static Boolean AllocateSlot(int ID)
    {
        try
        {
            String line = in.readLine();
            long time = System.currentTimeMillis();

            while (line == null && time + 1000 > System.currentTimeMillis())
            {
                line = in.readLine();
            }

            if (line != null && line.equals("starting")) return true;
        } 
        catch (IOException e)
        {
            System.out.println("IO error when allocating thread slot: " + e);
            e.printStackTrace();
        }

        return false;
    }

}
