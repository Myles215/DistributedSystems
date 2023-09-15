package server;

import java.io.*;
import java.net.*;
import java.util.*;

import server.ServerThread;

public class AggregationServer
{

    static public ArrayList<ServerThread> threads;

    public static void main(String[] args)
    {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) 
        {
            System.out.println("Server is listening on port " + port);
 
            while (true) 
            {
                Socket socket = serverSocket.accept();

                System.out.println("New client connected");

                threads.add(new ServerThread(socket));
                threads.get(threads.size()-1).start();
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}