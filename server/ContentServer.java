package server;

import java.net.*;
import java.io.*;
import java.util.*;


class ContentServer
{

    private static String hostname = "localhost";
    private static Socket socket = new Socket();

    public static void main(String args[])
    {
        if (args.length < 2) return;
 
        int port = Integer.parseInt(args[0]);
        String file = args[1];
 
        connect(port);

        try
        {
            String json = "";
            BufferedReader reader = new BufferedReader(new FileReader("./" + file));
            String line = reader.readLine();

            while (line != null)
            {
                json += line;
                line = reader.readLine();
            }

            putRequest("", json);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Invalid input file");
        }
        catch (IOException e)
        {
            System.out.println("Could not send content to aggregation server");
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

    public static void putRequest(String path, String content) throws IOException
    {
        // Create input and output streams to read from and write to the server
        PrintStream out = new PrintStream( socket.getOutputStream() );
        BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        // Follow the HTTP protocol of GET <path> HTTP/1.0 followed by an empty line
        out.println("PUT " + path + " HTTP/1.1");
        out.println("contentType:application/json");
        out.println("contentLength:" + content.length());
        out.println(content);
    }

}