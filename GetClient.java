package client;

import java.net.*;
import java.io.*;

public class GetClient
{

    private static String hostname = "localhost";

    public static void main(String[] args) 
    {
        if (args.length < 1) return;
 
        int port = Integer.parseInt(args[0]);
 
        connect(port);
    }

    public static void connect(int port)
    {
        try (Socket socket = new Socket(hostname, port)) 
        {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            String ret = reader.readLine();
 
            System.out.println(ret);
 
        } 
        catch (UnknownHostException ex) 
        {
            System.out.println("Server not found: " + ex.getMessage());
        } 
        catch (IOException ex) 
        {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}