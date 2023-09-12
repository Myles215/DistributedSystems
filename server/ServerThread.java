package server;

import java.io.*;
import java.net.*;

public class ServerThread extends Thread
{
    private Socket mSocket;

    public ServerThread(Socket client)
    {
        mSocket = client;
    }

    public void run()
    {
        try 
        {
            String clientInput = "";

            InputStream input = mSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = mSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            while (true)
            {
                String line = reader.readLine();

                System.out.println(line);
            }
        } 
        catch (IOException e)
        {
            System.out.println("IOException: " + e);
        }

        //writer.println(new Date().toString());
    }
}