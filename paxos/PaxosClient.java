package paxos;

import java.util.*;
import java.net.*;
import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer; 
import java.nio.channels.SocketChannel; 

public class PaxosClient 
{
    enum Stage
    {
        PREPARING,
        PROPOSING,
        ACCEPTING
    }

    enum Role 
    {
        M1,
        M2,
        M3,
        M4t9,
        normal
    }

    //Connection variables
    private SocketChannel connection;

    //Paxos variables
    private int lamportTime = 0;
    private int lamportID = 0;

    private int acceptedTime = -1;
    private int acceptedID = -1;
    private String acceptedValue = null;

    public volatile String committed = null;

    public Stage stage = Stage.PREPARING;
    
    //Assignment variable
    public Role role = Role.normal;

    private int ID;

    public void main(String[] args)
    {
        if (args.length < 2) 
        {
            System.out.println("Client needs ID and port");
            return;
        }

        ID = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);

        int retryCount = 0;
        Boolean connected = false;

        System.out.println("Connecting to server");

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
            return;
        }

        if (!AllocateSlot(ID))
        {
            return;
        }

        Boolean isProposer = false;

        if (args.length > 2 && args[2] != "")
        {
            isProposer = true;
            acceptedValue = args[2];
            lamportTime = 1;
            acceptedID = ID;
        }
        //check if we start with a proposed value
        //if yes, this client is a proposer

        if (args.length > 3)
        {
            //Set our role if we include it
            switch (args[3])
            {
                case "M1":
                    role = Role.M1;
                    break;
                case "M2":
                    System.out.println("Starting up as M2");
                    role = Role.M2;
                    break;
                case "M3":
                    System.out.println("Starting up as M3");
                    role = Role.M3;
                    break;
                default:
                    role = Role.M4t9;
            }
        }

        try
        {
            if (isProposer)
            {
                RunProposer();
            }
            else
            {
                RunAcceptor();
            }

            System.out.println("Client M" + ID + " committed value: " + committed);
        }
        catch (IOException e)
        {
            System.err.println("Server shut down");
        }
        catch (InterruptedException e)
        {
            System.out.println("Thank fuck we can stop this server");
        }
    }

    private void RunProposer() throws IOException, InterruptedException
    {
        System.out.println("Acting as proposer");
        //Wait a bit for other participants to get set up
        Thread.sleep(600);
        ArrayList<Integer> participants = new ArrayList<Integer>();

        for (int i = 1;i<10;i++)
        {
            participants.add(i);
            try { SendMessage(ID, i, acceptedValue, "Prepare", lamportTime*10 + ID); } catch(IOException e) {}
        }

        while (committed == null)
        {
            //We will obviously always accept our own proposal
            int replies = 1;

            Thread.sleep(1800);
            Message.MessageType expected;

            if (stage == Stage.PREPARING)
            {
                expected = Message.MessageType.Promise;
                //Temporarily change our value and time to -1
                //SO we can check if we receive any replies with pre-accepted values
                acceptedTime = -1;
            }
            else 
            {
                expected = Message.MessageType.Accept;
            }

            replies += HandleProposerMessages(participants, expected);
            System.out.println("M" + ID + " Received replies from " + replies + " other members");

            //In this case, we didn't receive back a pre-accepted value
            if (acceptedTime == -1) acceptedTime = lamportTime;

            if (replies > participants.size()/2)
            {
                if (stage == Stage.PREPARING)
                {
                    MassSend(participants, acceptedValue, "Propose");
                    stage = Stage.PROPOSING;
                }
                else if (stage == Stage.PROPOSING)
                {
                    participants.clear();
                    for (int i = 1;i<=9;i++) participants.add(i);
                    Committed(participants);
                    committed = acceptedValue;
                }
            }
            else
            {
                stage = Stage.PREPARING;
                System.out.println("M" + ID + " Failed prepare, will retry value " + acceptedValue + " with higher ID - " + ++acceptedTime);
                //If we don't get quorum, we increment our time ID and try again
                lamportTime = acceptedTime;

                MassSend(participants, acceptedValue, "Prepare");
            }
        }
    }

    private void RunAcceptor()
    {
        System.out.println("Acting as acceptor");

        try
        {
            while (committed == null)
            {
                //Need this to avoid busy wait
                Thread.sleep(100);
                Random rand = new Random();
                int action = rand.nextInt() % 3;

                if (role == Role.M3)
                {
                    if (action == 0)
                    {
                        System.out.println("M3 is going camping, see you next round!");
                        Thread.sleep(1800);
                    }
                }
                else if (role == Role.M2)
                {
                    if (action > 0)
                    {
                        System.out.println("M2 finished their coffee at the cafe");
                        Thread.sleep(600 * action);
                    }
                }

                HandleAcceptorMessages();
            }
        }
        catch (IOException e)
        {
            System.err.println("Error in client IO " + e);
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            System.err.println("Error in client sleep " + e);
        }
    }

    private Boolean Connect(int port)
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

    private Boolean AllocateSlot(int ID)
    {
        SendString(Integer.toString(ID));

        String line = "";

        try
        {
            while (line.equals("")) line = ReadString();
        }
        catch (IOException e)
        {
            System.out.println("Server has shut down");
        }

        if (line.equals("starting*")) return true;

        return false;
    }

    private void MassSend(ArrayList<Integer> participants, String value, String type) throws IOException
    {
        for (int i = 0;i<participants.size();i++)
        {
            SendMessage(ID, participants.get(i), value, type, lamportTime*10 + ID);
        }
    }

    private int HandleProposerMessages(ArrayList<Integer> participants, Message.MessageType expected) throws IOException
    {
        Set<Integer> ret = new HashSet<Integer>();
        ret.add(0);

        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(512);
            int bytesRead = 1;

            while (bytesRead > 0)
            {
                buffer.clear();
                bytesRead = connection.read(buffer);

                if (bytesRead == 0) break;

                buffer.flip();                     
                // Parse data from buffer to String
                String line = new String(buffer.array(), StandardCharsets.UTF_8).trim(); 
                int index = 0;
                int nextIndex = line.indexOf("*");

                boolean awake = true;
                Random rand = new Random();

                while (nextIndex != -1)
                {
                    Message message;
                    try 
                    { 
                        message = new Message(line.substring(index, nextIndex));
                    }
                    catch (Exception e)
                    {
                        buffer.clear();
                        return 0;
                    }

                    if (role == Role.M2) //M2 has a 33% chance of going to sleep and 66% chance of waking up
                    {
                        if (awake && rand.nextInt() % 3 == 0)
                        {
                            awake = false;
                            System.out.println("M2 has left the cafe :(");
                        } 
                        else if (!awake && rand.nextInt() % 3 > 0)
                        {
                            awake = true;
                            System.out.println("M2 is back at the cafe :)");
                        }
                    }
                    else if (role == Role.M3) //M3 has a 60% chance of going to sleep and a 40% chance of waking up
                    {
                        if (awake && rand.nextInt() % 2 == 0)
                        {
                            awake = false;
                            System.out.println("M3 has gone camping :(");
                        }
                        else if (!awake && rand.nextInt() % 2 == 1)
                        {
                            awake = true;
                            System.out.println("M3 is back from camping :)");
                        }
                    }

                    //Always need to handle this case
                    if (message.type == Message.MessageType.NC)
                    {
                        if (participants.indexOf(message.sender) != -1) 
                        {
                            participants.remove(participants.indexOf(message.sender));
                            System.out.println("M" + ID + " Handling not connected");
                        }
                    }
                    else if (message.type == Message.MessageType.Commit)
                    {
                        committed = message.value;
                        return 0;
                    }

                    //If the proposer is awake, handle this message
                    if (awake)
                    {                    
                        if (message.type == Message.MessageType.Promise && message.type == expected)
                        {
                            System.out.println("M" + ID + " Handling promise from M" + message.sender);
                            ret.add(HandlePromise(message));
                        }
                        else if (message.type == Message.MessageType.Accept && message.type == expected)
                        {
                            if (message.timeID/10 >= lamportTime) 
                            {
                                ret.add(message.sender);
                                System.out.println("M" + ID + " Handling accept from M" + message.sender);
                            }
                            else System.err.println("M" + ID + " discarding old accept from M" + message.sender);
                        }
                    }

                    index = nextIndex + 1;
                    nextIndex = line.indexOf("*", index);
                }
            }
        }
        catch (IOException e)
        {
            throw new IOException(e);
        }

        return ret.size() - 1;
    }

    private void HandleAcceptorMessages() throws IOException
    {
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(512);
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

                    //If this client has a role, act accordingly
                    //Only replies 80% of the time 
                    Random rand = new Random();
                    if (role == Role.normal || (role == Role.M4t9 && rand.nextInt()%5 > 0) || (role == Role.M2 && rand.nextInt()%3 != 0) || role == Role.M3)
                    {
                        if (message.type == Message.MessageType.Prepare)
                        {
                            System.out.println("M" + ID + " handling prepare from M" + message.sender);
                            HandlePrepare(message);
                        }
                        else if (message.type == Message.MessageType.Propose)
                        { 
                            System.out.println("M" + ID + " handling propose from M" + message.sender);
                            HandlePropose(message);
                        }
                    }
                    else if (role == Role.M2)
                    {
                        System.out.println("M2 missed message!");
                    }

                    //Always handle this message
                    if (message.type == Message.MessageType.Commit)
                    {
                        committed = message.value;
                    }

                    index = nextIndex + 1;
                    nextIndex = line.indexOf("*", index);
                }
            }
        }
        catch (IOException e)
        {
            throw new IOException(e);
        }
    }

    private int HandlePromise(Message promise)
    {
        int timeID = promise.timeID;

        if (!promise.value.equals(""))
        {
            //Two cases, if we receive a time that is higher, or a node with a 
            //superior ID has already had it's value selected at the same time
            if (timeID/10 > acceptedTime || (timeID/10 == acceptedTime && timeID%10 < acceptedID))
            {
                System.out.println("M" + ID + " changed to propose new value from M" + promise.sender + " : " + promise.value);
                acceptedValue = promise.value;
                acceptedTime = timeID/10;
                acceptedID = timeID%10;
            }

            return promise.sender;
        }

        if (timeID / 10 < lamportTime) 
        {
            System.err.println("Proposer M" + ID + " discarding old promise");
            return 0;
        }

        return promise.sender;
    }

    private void HandlePrepare(Message prepare) throws IOException
    {
        if (prepare.timeID/10 >= lamportTime)
        {
            lamportTime = Math.max(lamportTime, prepare.timeID/10);
            lamportID = prepare.sender;

            try
            {
                if (acceptedValue != null)
                {
                    SendMessage(ID, prepare.sender, acceptedValue, "Promise", lamportTime*10 + lamportID);
                }
                else
                {
                    SendMessage(ID, prepare.sender, "", "Promise", lamportTime*10 + ID);
                }
            }
            catch (IOException e)
            {
                throw new IOException(e);
            }
        }
        else System.out.println("M" + ID + " Ignoring old prepare from M" + prepare.sender);
    }

    private void HandlePropose(Message proposal) throws IOException
    {
        if (proposal.timeID/10 > lamportTime || (proposal.timeID/10 == lamportTime && proposal.timeID%10 <= lamportID))
        {
            acceptedValue = proposal.value;
            lamportTime = proposal.timeID/10;
            lamportID = proposal.timeID%10;
            SendMessage(ID, proposal.sender, "", "Accept", proposal.timeID);
        }
        else System.out.println("M" + ID + " Ignoring old proposal from M" + proposal.sender);
    }

    //Now that we are done, broadcast committed a few times
    private void Committed(ArrayList<Integer> participants) throws IOException
    {
        for (int i = 0;i<3;i++)
        {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            MassSend(participants, acceptedValue, "Commit");
        }
    }

    private String ReadString() throws IOException
    {
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(512);
            connection.read(buffer);
            String line = new String(buffer.array()).trim();

            connection.read(buffer);
            line = new String(buffer.array()).trim();

            return line;
        }
        catch (IOException e)
        {
            System.err.println("Error when reading server reply");
            throw new IOException(e);
        }
    }

    private void SendMessage(int sender, int receiver, String value, String type, int timeID) throws IOException
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
            throw new IOException(e);
        }
    }

    private void SendString(String message)
    {
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(512);
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
