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
    private ConcurrentHashMap<Integer, Message> OutgoingMessages;
    private Socket connection;
    private int ID = -1;

    Selector clientHandler; 
    SocketChannel client;

    ServerThread(ServerSocketChannel socketChannel, ConcurrentHashMap<Integer, Message> MessgaeBank)
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
                        System.out.println("Reading client message " + ID);
                        // create a ServerSocketChannel to read the request   

                        if (ID == -1)
                        {
                            SocketChannel client = (SocketChannel)key.channel(); 
                            
                            // Create buffer to read data 
                            ByteBuffer buffer = ByteBuffer.allocate(10000);
                            
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
                                SendString("starting");
                                ID = id;

                                OutgoingMessages.put(id, new Message(null));
                            }
                        }
                        else
                        {
                            Message message = CheckForReceivedMessage(key);

                            if (message != null)
                            {
                                //See if we have some deadlock, only wait 1 second to try send message, then return fail
                                long startTime = System.currentTimeMillis();

                                while (OutgoingMessages.get(message.receiver).type != MessageType.NULL && System.currentTimeMillis() < startTime + 1000)
                                {
                                    //TODO
                                }

                                //If we can't send an outgoing message, reply with fail
                                if (OutgoingMessages.get(message.receiver).type != MessageType.NULL)
                                {
                                    Message reply = new Message(null);
                                    SendMessage(reply);
                                }
                                else
                                {
                                    OutgoingMessages.put(message.receiver, message);
                                }
                            }
                        } 
                    }
                    i.remove();
                } 

                Message message = CheckForMessageToSend();

                if (message.type != Message.MessageType.NULL)
                {
                    System.out.println("Passing message from " + message.sender + " to " + ID);
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

    private Message CheckForReceivedMessage(SelectionKey key) throws IOException
    {
        SocketChannel client = (SocketChannel)key.channel(); 
                        
        // Create buffer to read data 
        ByteBuffer buffer = ByteBuffer.allocate(10000);
        
        int bytesRead = client.read(buffer);

        if (bytesRead == 0) return new Message(null);

        buffer.flip();                     
        // Parse data from buffer to String
        String line = new String(buffer.array(), StandardCharsets.UTF_8).trim();  

        return new Message(line);
    }

    //See if we have any messages in our mailbox
    private Message CheckForMessageToSend()
    {
        Message check = OutgoingMessages.get(ID);

        if (check.type != MessageType.NULL)
        {
            OutgoingMessages.put(ID, new Message(null));
            return check;
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
            ByteBuffer buffer = ByteBuffer.allocate(256);
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
