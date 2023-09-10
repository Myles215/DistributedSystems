package server;

import java.io.*;
import java.net.*;
import java.util.Date;

//use HTTPUrl connection

//use server socket

public class AggregationServer
{

    public static void main(String[] args)
    {

        int port = 5649;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            System.out.println("Server is listening on port " + port);
 
            while (true) 
            {
                Socket socket = serverSocket.accept();
 
                System.out.println("New client connected");

                String clientInput = "";

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                while (!clientInput.equals("receive"))
                {
                    clientInput = reader.readLine();

                    System.out.println(clientInput);
                }
 
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
 
                writer.println(new Date().toString());
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}