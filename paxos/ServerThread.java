package paxos;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.Object;
import paxos.LamportClock;
import paxos.Message;
import paxos.Message.MessageType;

import java.nio.channels.ServerSocketChannel;
import java.nio.ByteBuffer; 
import java.nio.channels.SelectionKey; 
import java.nio.channels.Selector; 
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ServerThread extends Thread {
    private LamportClock mLamportClock = new LamportClock();
    //We use this outgoing messages data structure to tell other server threads to communicate with their clients
    //Sort of like a shared memory system, each thread checks for incoming messages, then checks for 
    //outgoing messages
    private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Message>> OutgoingMessages;
    private Socket connection;
    private int ID = -1;

    Selector clientHandler; 
    SocketChannel client;

    ServerThread(ServerSocketChannel socketChannel, ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Message>> MessgaeBank)
    {
        try
        {
            clientHandler = Selector.open();
            client = socketChannel.accept();
            client.configureBlocking(false);

            client.register(clientHandler, SelectionKey.OP_READ);
            System.out.println("New client connected");
        }
        catch (IOException e)
        {
            System.out.println("Error setting up request processor: " + e);
        }

        OutgoingMessages = MessgaeBank;
    }

    public void run()
    {
        try
        {
            while (true) 
            { 
                clientHandler.selectNow();
                Set<SelectionKey> selectedKeys = clientHandler.selectedKeys(); 
                Iterator<SelectionKey> i = selectedKeys.iterator(); 

                while (i.hasNext()) 
                { 
                    SelectionKey key = i.next(); 

                    if (key.isReadable()) 
                    { 
                        // create a ServerSocketChannel to read the request   

                        if (ID == -1)
                        {
                            SocketChannel client = (SocketChannel)key.channel(); 
                            
                            // Create buffer to read data 
                            ByteBuffer buffer = ByteBuffer.allocate(512);
                            
                            int bytesRead = client.read(buffer);

                            if (bytesRead < 0)
                            {
                                i.remove();
                                continue;
                            }

                            buffer.flip();                     
                            // Parse data from buffer to String 
                            int id = Integer.parseInt(new String(buffer.array(), StandardCharsets.UTF_8).trim());

                            if (OutgoingMessages.containsKey(id))
                            {
                                SendString("client with this ID already exists");
                                
                                client.close();
                                throw new Exception("Duplicate client connected");
                            }
                            else
                            {
                                System.out.println("Setting up client with ID " + id);
                                SendString("starting");
                                ID = id;

                                OutgoingMessages.put(id, new ConcurrentLinkedQueue<Message>());
                            }
                        }
                        else
                        {
                            CheckForReceivedMessage(key);
                        } 
                    }
                    i.remove();
                } 

                Message message = CheckForMessageToSend();

                if (message.type != Message.MessageType.NULL)
                {
                    SendMessage(message);
                }
            } 
        }
        catch(Exception e)
        {
            System.out.println("Error in server thread: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void CheckForReceivedMessage(SelectionKey key) throws IOException
    {
        SocketChannel client = (SocketChannel)key.channel(); 
        // Create buffer to read data 
        ByteBuffer buffer = ByteBuffer.allocate(512);
        
        int bytesRead = 1;

        while (bytesRead > 0)
        {
            bytesRead = client.read(buffer);

            if (bytesRead == 0) return;

            buffer.flip();                     
            // Parse data from buffer to String
            String line = new String(buffer.array(), StandardCharsets.UTF_8).trim(); 
            int index = 0;
            int nextIndex = line.indexOf("*");

            while (nextIndex != -1)
            {
                Message message = new Message(line.substring(index, nextIndex));
                if (!PassOnMessage(message))
                {
                    SendMessage(new Message(ID, message.receiver, "", -1, Message.MessageType.NC));
                }

                index = nextIndex + 1;
                nextIndex = line.indexOf("*", index);
            }
        }
    }

    private boolean PassOnMessage(Message message)
    {
        try
        {
            OutgoingMessages.get(message.receiver).add(message);
            return true;
        }
        catch (Exception e)
        {
        }

        return false;
    }

    //See if we have any messages in our mailbox
    private Message CheckForMessageToSend()
    {
        if (!OutgoingMessages.get(ID).isEmpty())
        {
            try
            {
                Message check = OutgoingMessages.get(ID).remove();

                return check;
            }
            catch (Exception e)
            {
                System.err.println("Somehow accessing empty queue " + e);
            }
        }

        return new Message(null);
    }

    //Send a message formatted to string
    private void SendMessage(Message message)
    {
        SendString(message.toString());
    }

    private void SendString(String msg)
    {
        try
        {
            msg += "*";
            ByteBuffer buffer = ByteBuffer.allocate(512);
            buffer.put(msg.getBytes()); 
            buffer.flip(); 

            client.write(buffer);
        }
        catch (IOException e)
        {
            System.out.println("Error when writing to client");
            e.printStackTrace();
        }
    }
}
