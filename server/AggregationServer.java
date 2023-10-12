package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.Object;

import server.ServerThread;
import util.LamportClock;

public class AggregationServer
{
    static public ArrayList<ServerThread> mThreads = new ArrayList<ServerThread>();

    static LamportClock mLamportClock = new LamportClock();

    public static void main(String[] args)
    {
        int port = 4567;

        if (args.length == 1) 
        {
            System.out.println("Using input port");
            port = Integer.parseInt(args[0]);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) 
        {
            System.out.println("Server is listening on port " + port);
 
            while (true) 
            {
                Socket socket = serverSocket.accept();

                System.out.println("New client connected");

                mThreads.add(new ServerThread(socket, mLamportClock));
                mThreads.get(mThreads.size()-1).start();
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}