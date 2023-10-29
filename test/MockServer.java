package test;

import java.util.*;
import java.net.*;
import java.io.*;

import paxos.PaxosClient;

public class MockServer {
    ArrayList<Socket> clients = new ArrayList<Socket>();
    ArrayList<BufferedReader> InputToServer = new ArrayList<BufferedReader>();
    ArrayList<PrintStream> OutputToClient = new ArrayList<PrintStream>();

    ServerSocket server;

    MockServer(int p)
    {
        for (int i = 0;i<10;i++)
        {
            clients.add(null);
            InputToServer.add(null);
            OutputToClient.add(null);
        }

        try
        {
            server = new ServerSocket(p);
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
            Socket socket = server.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String ID = reader.readLine();
            int id = Integer.parseInt(ID);

            clients.set(id, socket);
            InputToServer.set(id, reader);
            OutputToClient.set(id, new PrintStream(socket.getOutputStream(), true));

            return true;
        }
        catch(IOException e)
        {
            System.out.println("Some exception when accepting socket connection: " + e);
        }

        return false;
    }
}
