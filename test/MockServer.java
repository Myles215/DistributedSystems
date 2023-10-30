package test;

import java.util.*;
import java.net.*;
import java.io.*;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import java.nio.ByteBuffer; 
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import paxos.PaxosClient;

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
            //serverSocket.setSoTimeout(30000);
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
            client.register(selector, SelectionKey.OP_READ);

            clientHandler.selectNow();
            Set<SelectionKey> readingKeys = clientHandler.selectedKeys(); 
            Iterator<SelectionKey> i = readingKeys.iterator();

            while (i.hasNext())
            {
                SelectionKey tempKey = i.next();

                if (tempKey.isReadable())
                {
                    SocketChannel tempClient = (SocketChannel)tempKey.channel();
                        
                    // Create buffer to read data 
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    
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
}
