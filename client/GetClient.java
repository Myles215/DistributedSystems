package client;

import parsers.JsonObject;
import parsers.HTTPParser;
import parsers.HTTPObject;

import java.net.*;
import java.io.*;
import java.util.*;

public class GETClient
{
    private static Socket socket = new Socket();
    private static String HTTPParserInput = "./parsers/HTTPParser.txt";
    static HTTPParser mHTTPParser = new HTTPParser(HTTPParserInput);
    static JsonObject mJsonParser = new JsonObject();
    static String hostname = "";

    public static void main(String[] args)  throws Exception
    {
        if (args.length < 1 || args[0].indexOf(':') == -1) 
        {
            System.out.println("GET client needs valid host and port in format 'hostname:port'");
            return;
        }
 
        int port = Integer.parseInt(args[0].substring(args[0].indexOf(':') + 1));
        hostname = args[0].substring(0, args[0].indexOf(':'));
 
        int retryCount = 0;
        Boolean connected = false;

        while (retryCount < 5 && !connected)
        {
            retryCount++;
            connected = connect(port);

            if (!connected) 
            {
                System.out.println("Couldn't connect, will retry");
                Thread.sleep(100);
            }
        }

        if (!connected) 
        {
            System.out.println("Connection retries exhausted");
            return;
        }

        int startTime = getStartTime();

        try
        {
            getRequest("/weather.json", startTime);

            HTTPObject reply = readResponse();

            for (String rep : reply.data)
            {
                mJsonParser.printString(rep);
                System.out.println(" ");
            }
        }
        catch (IOException e)
        {
            
        }
    }

    //Connect to server socket on supplied port
    public static Boolean connect(int port)
    {
        try
        {
            socket = new Socket(hostname, port);
            return true;
        } 
        catch (UnknownHostException ex) 
        {
            System.out.println("Server not found: " + ex.getMessage());
            return false;
        } 
        catch (IOException ex) 
        {
            System.out.println("I/O error: " + ex.getMessage());
            return false;
        }
    }

    //Disconnect when done
    public void disconnect() throws IOException
    {
        socket.close();
    }

    //Get starting lamport clock time
    private static int getStartTime() throws IOException, Exception
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        HTTPObject http = new HTTPObject("NULL");

        while (http.type != HTTPObject.RequestType.PUT)
        {
            http = mHTTPParser.parse(reader);
        }

        return http.lamportTime;
    }

    //Format and send GET request to specified path
    public static void getRequest(String path, int lamportTime) throws IOException, Exception
    {
        // Create input and output streams to read from and write to the server
        PrintStream out = new PrintStream( socket.getOutputStream() );

        // Follow the HTTP protocol of GET <path> HTTP/1.0 followed by an empty line
        out.println("GET " + path + " HTTP/1.1");
        out.println("User-Agent: ATOMClient/1/0");
        out.println("Content-Type: application/json");
        out.println("Lamport-Time: " + Integer.toString(lamportTime));
        out.println("Content-Length:0");
    }

    //Read reponse from get request
    public static HTTPObject readResponse() throws IOException, Exception
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        HTTPObject http = new HTTPObject("NULL");

        while (http.type != HTTPObject.RequestType.RES)
        {   
            http = mHTTPParser.parse(reader);
        }

        return http;
    }
}