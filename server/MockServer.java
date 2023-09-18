package server;

import java.net.*;
import java.io.*;

public class MockServer extends Thread
{

    private int port = 1111;

    public void run()
    {
        try (ServerSocket serverSocket = new ServerSocket(port)) 
        {
            Socket socket = serverSocket.accept();
            clientConnected = true;

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            clientMessage = reader.readLine();

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String re = "Good job sending GET request!";

            writer.println("PUT / HTTP/1.1");
            writer.println("contentType:application/json");
            writer.println("contentLength:" + re.length());
            writer.println(re);
 
        } catch (IOException ex) 
        {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean clientConnected = false;
    public String clientMessage = "";
}