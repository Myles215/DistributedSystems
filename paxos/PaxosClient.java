package paxos;

import java.util.*;
import java.net.*;
import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer; 
import java.nio.channels.SocketChannel; 

public class PaxosClient 
{
    //Connection variables
    private static SocketChannel connection;

    //Paxos variables
    private static int lamportTime = 0;

    private static int acceptedTime = -1;
    private static int acceptedID = -1;

    private static String acceptedValue = null;

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
            acceptedValue = args[2];
            acceptedTime = lamportTime = 1;
            acceptedID = ID;
        }
        //check if we start with a proposed value
        //if yes, this client is a proposer
        System.out.println("about to Acting as proposer");

        if (isProposer)
        {
            System.out.println("Acting as proposer");
            ArrayList<Integer> participants = new ArrayList<Integer>();

            for (int i = 1;i<10;i++)
            {
                participants.add(i);
                SendMessage(ID, i, value, "Prepare", lamportTime);
            }

            String committed = null;
            boolean hasQuorum = false;

            while (committed == null)
            {
                long start = System.currentTimeMillis();
                long ALLOWED = 2000;

                int replies = 0;

                while (start + ALLOWED > System.currentTimeMillis())
                {   
                    replies += HandleMessages(participants);
                }

                System.out.println("Received replies from " + replies + " other members");

                if (replies >= participants.size() / 2)
                {
                    hasQuorum = true;
                }

                if (hasQuorum)
                {
                    for (int i = 0;i<participants.size();i++)
                    {
                        SendMessage(ID, participants.get(i), acceptedValue, "Proposal", acceptedTime);
                    }
                }
                else
                {
                    System.out.println("Failed prepare, will retry with higher ID - " + ++acceptedTime);
                    //If we don't get quorum, we increment our time ID and try again
                    lamportTime = acceptedTime;

                    for (int i = 0;i<participants.size();i++)
                    {
                        SendMessage(ID, participants.get(i), value, "Prepare", lamportTime);
                    }
                }

            }
        }

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
            connection.configureBlocking(false);
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
        SendString(Integer.toString(ID));

        long time = System.currentTimeMillis();
        String line = null;

        while (line == null && time + 1000 > System.currentTimeMillis())
        {
            line = ReadString();
        }

        if (line != null && line.equals("starting")) return true;

        return false;
    }

    private static int HandleMessages(ArrayList<Integer> participants)
    {
        int ret = 0;

        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(256);
            int bytesRead = 1;

            while (bytesRead > 0)
            {
                bytesRead = connection.read(buffer);

                if (bytesRead == 0) break;

                buffer.flip();                     
                // Parse data from buffer to String
                String line = new String(buffer.array(), StandardCharsets.UTF_8).trim(); 
                int index = 0;
                int nextIndex = line.indexOf("*");

                while (nextIndex != -1)
                {
                    Message message = new Message(line.substring(index, nextIndex));
                    
                    if (message.type == Message.MessageType.NC)
                    {
                        System.out.println("Handling not connected");
                        participants.remove(participants.indexOf(message.sender));
                    }
                    else if (message.type == Message.MessageType.Promise)
                    {
                        HandlePromise(message);
                        ret++;
                    }
                    else if (message.type == Message.MessageType.Accept)
                    {
                        HandleAccept(message);
                        ret++;
                    }

                    index = nextIndex + 1;
                    nextIndex = line.indexOf("*", index);
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception in client " + e);
        }

        return ret;
    }

    private static void HandlePromise(Message promise)
    {
        if (promise.previousProposal != null)
        {
            if (promise.previousProposalTime > acceptedTime || (promise.previousProposalTime == acceptedTime && promise.sender < acceptedID))
            {
                acceptedValue = promise.previousProposal;
                acceptedTime = promise.previousProposalTime;
                acceptedID = promise.sender;
            }
        }
    }

    private static void HandleAccept(Message message)
    {

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

    private static void SendMessage(int sender, int receiver, String value, String type, int timeID)
    {
        String msg = " -r " + Integer.toString(receiver) + "; -s " + Integer.toString(sender) + "; -v " + value + "; -t " + type + "; -i " + timeID + ";*";

        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(256);
            buffer.put(msg.getBytes()); 
            buffer.flip(); 

            connection.write(buffer);
        }
        catch (IOException e)
        {
            System.out.println("Error when writing to client");
            e.printStackTrace();
        }
    }

    private static void SendString(String message)
    {
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(256);
            buffer.put(message.getBytes()); 
            buffer.flip(); 

            connection.write(buffer);
        }
        catch (IOException e)
        {
            System.out.println("Error when writing to client");
            e.printStackTrace();
        }
    }

}
