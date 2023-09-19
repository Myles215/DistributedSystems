package client;

import parsers.HTTPParser;
import parsers.HTTPObject;

import java.net.*;
import java.io.*;
import java.util.*;

public class MockClient
{

    private String hostname = "localhost";
    public Socket socket = new Socket();
    private HTTPParser mHTTPParser = new HTTPParser();

    public void run(int port) throws InterruptedException
    {
        connect(port);
    }

    public void connect(int port) throws InterruptedException
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

    public void sendPutRequest(String req) throws IOException, Exception
    {
        PrintStream out = new PrintStream( socket.getOutputStream() );

        out.println(req);
    }

    public void sendGetRequest(String req) throws IOException, Exception
    {
        // Create input and output streams to read from and write to the server
        PrintStream out = new PrintStream( socket.getOutputStream() );

        out.println(req);
    }

    public HTTPObject readResponse() throws IOException, Exception
    {
        BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        return mHTTPParser.parse(in);
    }
}