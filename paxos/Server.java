package paxos;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.Object;

import paxos.ServerThread;
import paxos.Message;

public class Server
{
    public static void main(String[] args)
    {
        int port = 4567;

        if (args.length == 1) 
        {
            System.out.println("Using input port");
            port = Integer.parseInt(args[0]);
        }

        ArrayList<ServerThread> mThreads = new ArrayList<ServerThread>();
        ArrayList<Message> mMessages = new ArrayList<Message>();

        for (int i = 0;i<10;i++)
        {
            mThreads.add(null);
            mMessages.add(null);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) 
        {
            System.out.println("Server is listening on port " + port);
 
            while (true) 
            {
                Socket socket = serverSocket.accept();

                System.out.println("New client connected");

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String ID = reader.readLine();

                while (ID == null)
                {
                    ID = reader.readLine();
                }

                int id = Integer.parseInt(ID);

                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                if (mThreads.get(id) != null)
                {
                    writer.println("client with this ID already exists");
                }
                else
                {
                    writer.println("starting");
                    mThreads.set(id, new ServerThread(socket, mMessages, id));
                    mThreads.get(id).start();
                }
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
