package test;

import java.util.*;
import java.net.*;
import java.io.*;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SelectableChannel;

import java.nio.ByteBuffer; 
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import paxos.PaxosClient;
import paxos.Message;

public class MockServer 
{
    ArrayList<Selector> clientHandlers = new ArrayList<Selector>();
    ArrayList<SocketChannel> clients = new ArrayList<SocketChannel>();

    Selector selector;
    ServerSocketChannel serverSocketChannel;
    ServerSocket serverSocket;

    MockServer(int p)
    {
        for (int i = 0;i<10;i++)
        {
            clients.add(null);
            clientHandlers.add(null);
        }
        try
        {
            selector = Selector.open();
            // We have to set connection host,port and 
            // non-blocking mode 
            serverSocketChannel = ServerSocketChannel.open(); 
            serverSocket = serverSocketChannel.socket(); 
            //serverSocket.setSoTimeout(timeout);
            serverSocket.bind( new InetSocketAddress("localhost", p)); 
            serverSocketChannel.configureBlocking(false); 
            int ops = serverSocketChannel.validOps(); 
            serverSocketChannel.register(selector, ops, null); 
        }
        catch (IOException e)
        {
            System.out.println("Error in mock server setup: " + e);
        }
    }

    boolean AcceptConnection()
    {
        try
        {
            System.out.println("Starting to accept client connections");
            boolean connected = false;

            while (!connected)
            {
                selector.selectNow();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) 
                {
                    SelectionKey key = iter.next();

                    if (key.isAcceptable()) 
                    {
                        connected = Register();
                    }

                    iter.remove();
                }
            }

            return true;
        }
        catch(IOException e)
        {
            System.out.println("Some exception when accepting socket connection: " + e);
        }
        catch (Exception e)
        {
            System.out.println("Some other exception: " + e);
            e.printStackTrace();
        }

        return false;
    }

    boolean Register()
    {
        try
        {
            System.out.println("Accepting new client");
            Selector clientHandler = Selector.open();
            SocketChannel client = serverSocketChannel.accept();
            client.configureBlocking(false);
            client.register(clientHandler, SelectionKey.OP_READ);

            clientHandler.select();
            Set<SelectionKey> readingKeys = clientHandler.selectedKeys(); 
            Iterator<SelectionKey> i = readingKeys.iterator();

            while (i.hasNext())
            {
                SelectionKey tempKey = i.next();

                if (tempKey.isReadable())
                {
                    SocketChannel tempClient = (SocketChannel)tempKey.channel();
                        
                    // Create buffer to read data 
                    ByteBuffer buffer = ByteBuffer.allocate(512);
                    
                    int bytesRead = tempClient.read(buffer);

                    if (bytesRead < 0)
                    {
                        i.remove();
                        continue;
                    }

                    buffer.flip();                     
                    // Parse data from buffer to String 
                    int id = Integer.parseInt(new String(buffer.array(), StandardCharsets.UTF_8).trim());

                    clients.set(id, client);
                    clientHandlers.set(id, clientHandler);
                    System.out.println("Setting up client with ID " + id);
                }
                i.remove();
            }

            return true;
        } 
        catch (IOException e)
        {
            System.out.println("Exception when accepting server client: " + e);
        }

        return false;
    }

    public void SendStringToClient(String message, int ID)
    {
        try
        {
            message += "*";
            ByteBuffer buffer = ByteBuffer.allocate(512);
            buffer.put(message.getBytes()); 
            buffer.flip(); 

            clients.get(ID).write(buffer);
        }
        catch (IOException e)
        {
            System.out.println("Error when writing to client");
            e.printStackTrace();
        }
    }

    public Message ReadMessageFromClient(int cli)
    {
        long start = System.currentTimeMillis();
        long timeAllowed = 500;

        while (start + timeAllowed > System.currentTimeMillis())
        {
            try
            {
                clientHandlers.get(cli).selectNow();
                Set<SelectionKey> selectedKeys = clientHandlers.get(cli).selectedKeys(); 
                Iterator<SelectionKey> i = selectedKeys.iterator(); 

                while (i.hasNext())
                {
                    SelectionKey key = i.next(); 

                    if (key.isReadable())
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
                        String message = new String(buffer.array(), StandardCharsets.UTF_8).trim();

                        if (!message.equals(""))
                        {
                            return new Message(message);
                        }
                    }
                }
            }
            catch (IOException e)
            {
                System.err.println("Error when reading client message " + e);
                e.printStackTrace();
            }
        }

        return new Message(null);
    }

    public ArrayList<Message> ReadStringFromClient(int cli)
    {
        long start = System.currentTimeMillis();
        long timeAllowed = 500;

        while (start + timeAllowed > System.currentTimeMillis())
        {
            try
            {
                clientHandlers.get(cli).select();
                Set<SelectionKey> selectedKeys = clientHandlers.get(cli).selectedKeys(); 
                Iterator<SelectionKey> i = selectedKeys.iterator(); 

                while (i.hasNext())
                {
                    SelectionKey key = i.next(); 

                    if (key.isReadable())
                    {
                        SocketChannel client = (SocketChannel)key.channel(); 
                                
                        // Create buffer to read data 
                        ByteBuffer buffer = ByteBuffer.allocate(512);
                        ArrayList<Message> allMessages = new ArrayList<Message>();
                        int bytesRead = 1;
                        
                        while (bytesRead > 0)
                        {
                            bytesRead = client.read(buffer);

                            if (bytesRead == 0) return allMessages;

                            buffer.flip();                     
                            // Parse data from buffer to String
                            String line = new String(buffer.array(), StandardCharsets.UTF_8).trim(); 
                            int index = 0;
                            int nextIndex = line.indexOf("*");

                            while (nextIndex != -1)
                            {
                                String message = line.substring(index, nextIndex);
                                allMessages.add(new Message(message));
                                index = nextIndex + 1;
                                nextIndex = line.indexOf("*", index);
                            }
                        }
                    }
                }
            }
            catch (IOException e)
            {
                System.err.println("Error when reading client message " + e);
                e.printStackTrace();
            }
            catch (Exception e)
            {
                System.err.println("Some other exception " + e);
                e.printStackTrace();
            }
        }

        return null;
    }

    public void Stop()
    {
        try
        {
            serverSocket.close();
            serverSocketChannel.close();

            for (int i = 0;i<clients.size();i++)
            {
                if (clients.get(i) != null)
                {
                    clients.get(i).close();
                    clientHandlers.get(i).close();
                }
            }
            selector.close();
        }
        catch (IOException e)
        {
            System.out.println("Error closing server: " + e);
        }
    }
}
