package server;

import java.net.*;
import java.io.*;

public class TestServer
{
    public void startup(int port)
    {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            Socket socket = serverSocket.accept();

            clientConnected = true;

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            while (!clientMessage.equals("receive"))
            {
                clientMessage = reader.readLine();
            }

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println("good job connecting!");
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean clientConnected = false;
    public String clientMessage = "";
}