package client;

import java.net.*;
import java.io.*;
import java.util.*;

public class MockClient
{

    private static String hostname = "localhost";
    private static Socket socket = new Socket();

    public static void main(String[] args) 
    {
        if (args.length < 1) return;
 
        int port = Integer.parseInt(args[0]);
 
        connect(port);

        try
        {
            getRequest("/path", "Hmmm", "test");
        }
        catch (IOException e)
        {
            
        }
    }

    public static void connect(int port)
    {
        try
        {
            socket = new Socket(hostname, port);
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

    public void disconnect() throws IOException
    {
        socket.close();
    }

    public static void getRequest(String path, String content, String req) throws IOException
    {
        // Create input and output streams to read from and write to the server
        PrintStream out = new PrintStream( socket.getOutputStream() );
        BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        out.println(req);
    }

    public static ArrayList<String> readResponse() throws IOException
    {
        BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        ArrayList<String> responseDoc = new ArrayList<String>();

        String line = in.readLine();
        while(!line.equals("\0"))
        {
            System.out.println(line);
            responseDoc.add(line);
            line = in.readLine();
        }

        return responseDoc;
    }
}