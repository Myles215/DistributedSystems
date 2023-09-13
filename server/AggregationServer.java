package server;

import java.io.*;
import java.net.*;
import java.util.Date;

import server.ServerThread;

public class AggregationServer
{

    public static void main(String[] args)
    {
        int port = 5649;

        try (ServerSocket serverSocket = new ServerSocket(port)) 
        {
            System.out.println("Server is listening on port " + port);
 
            while (true) 
            {
                Socket socket = serverSocket.accept();

                System.out.println("New client connected");

                new ServerThread(socket).start();
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}