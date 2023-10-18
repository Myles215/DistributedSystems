package paxos;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.Object;
import paxos.LamportClock;
import paxos.Message;
import paxos.Message.MessageType;

public class ServerThread extends Thread {
    private LamportClock mLamportClock = new LamportClock();
    //We use this outgoing messages data structure to tell other server threads to communicate with their clients
    //Sort of like a shared memory system, each thread checks for incoming messages, then checks for 
    //outgoing messages
    private ConcurrentHashMap<Integer, Message> OutgoingMessages;
    private Socket connection;
    private int ID;

    ServerThread(Socket socket, ConcurrentHashMap<Integer, Message> MessgaeBank, int id)
    {
        connection = socket;
        OutgoingMessages = MessgaeBank;
        ID = id;
    }

    public void run()
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            PrintStream out = new PrintStream(connection.getOutputStream());

            System.out.println("Server thread starting to handle client with ID: " + ID);
        
            while (true)
            {
                //See if there are any messages in this clients mailbox
                Message output = CheckForMessageToSend();

                //If there is a message, send it to our client
                if (output != null)
                {

                    System.out.println("outputting " + ID);
                    SendMessage(output, out);
                }
                else HeartBeat(out);

                Message input = null;
                
                input = CheckForReceivedMessage(in);

                if (input != null)
                {
                    //See if we have some deadlock, only wait 1 second to try send message, then return fail
                    long startTime = System.currentTimeMillis();

                    System.out.println("waiting " + ID);

                    while (OutgoingMessages.get(input.receiver).type != MessageType.NULL && System.currentTimeMillis() < startTime + 1000)
                    {
                        //TODO
                    }

                    System.out.println("sending " + ID);

                    //If we can't send an outgoing message, reply with fail
                    if (OutgoingMessages.get(input.receiver).type != MessageType.NULL)
                    {
                        Message reply = new Message(null);
                        SendMessage(reply, out);
                    }
                    else
                    {
                        OutgoingMessages.put(input.receiver, input);
                    }
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("Exception in server thread: " + e);
            //this will reset our client and it can rejoin later if it likes
            OutgoingMessages.remove(ID);
        }
    }

    private Message CheckForReceivedMessage(BufferedReader in) throws IOException
    {
        System.out.println("Checking input " + ID);
        String line = in.readLine();

        System.out.println("Done input " + ID);

        if (line == null || line.equals("")) return null;

        return new Message(line);
    }

    //See if we have any messages in our mailbox
    private Message CheckForMessageToSend()
    {
        System.out.println("checking" + ID);
        Message check = OutgoingMessages.get(ID);

        if (check.type != MessageType.NULL)
        {
            OutgoingMessages.put(ID, new Message(null));
            return check;
        }

        System.out.println("done checking" + ID);

        return null;
    }

    //Send a message formatted to string
    private void SendMessage(Message message, PrintStream out)
    {
        out.println(message.toString());
    }

    //Stop blocking IO calls with a simple empty print allowing the client to escape the readLine call
    private void HeartBeat(PrintStream out)
    {
        out.print("");
    }
}
