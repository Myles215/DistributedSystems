package server;

import java.io.*;
import java.net.*;
import java.util.*;

import server.ServerThread;

public class ThreadAggregationServer extends Thread
{
    public ArrayList<ServerThread> threads = new ArrayList<ServerThread>();
    public Boolean exit = false;
    int port = 0;

    public ThreadAggregationServer(String[] args)
    {
        if (args.length < 1) return;
        port = Integer.parseInt(args[0]);
    }

    public void run()
    {
        try (ServerSocket serverSocket = new ServerSocket(port)) 
        {
            System.out.println("Server is listening on port " + port);
 
            while (!exit) 
            {
                Socket socket = serverSocket.accept();

                System.out.println("New client connected");

                threads.add(new ServerThread(socket));
                threads.get(threads.size()-1).start();
            }

            threads.get(threads.size()-1).interrupt();
            serverSocket.close();
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}