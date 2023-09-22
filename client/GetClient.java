package client;

import parsers.JsonObject;
import parsers.HTTPParser;
import parsers.HTTPObject;

import java.net.*;
import java.io.*;
import java.util.*;

public class GETClient
{
    private static String hostname = "localhost";
    private static Socket socket = new Socket();
    private static String HTTPParserInput = "./parsers/HTTPParser.txt";
    static HTTPParser mHTTPParser = new HTTPParser(HTTPParserInput);
    static JsonObject mJsonParser = new JsonObject();

    public static void main(String[] args)  throws Exception
    {
        if (args.length < 1) 
        {
            System.out.println("GET client needs valid port");
            return;
        }
 
        int port = Integer.parseInt(args[0]);
 
        connect(port);

        try
        {
            getRequest("/weather.json");

            ArrayList<String> reply = readResponse();

            for (String rep : reply)
            {
                System.out.println(rep);
                mJsonParser.printString(rep);
                System.out.println(" ");
            }
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

    public static void getRequest(String path) throws IOException, Exception
    {
        // Create input and output streams to read from and write to the server
        PrintStream out = new PrintStream( socket.getOutputStream() );
        BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        // Follow the HTTP protocol of GET <path> HTTP/1.0 followed by an empty line
        out.println("GET " + path + " HTTP/1.1");
        out.println("contentType: application/json");
        out.println("contentLength:0");
    }

    public static ArrayList<String> readResponse() throws IOException, Exception
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        HTTPObject http = new HTTPObject("NULL");

        while (http.type == HTTPObject.RequestType.NULL)
        {   
            http = mHTTPParser.parse(reader);
        }

        return http.data;
    }
}