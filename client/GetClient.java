package client;

import parsers.JsonObject;
import parsers.HTTPParser;
import parsers.HTTPObject;
import util.LamportClock;

import java.net.*;
import java.io.*;
import java.util.*;

public class GETClient
{
    private static Socket socket = new Socket();
    private static String HTTPParserInput = "./parsers/HTTPParser.txt";
    static HTTPParser mHTTPParser = new HTTPParser(HTTPParserInput);
    private static LamportClock mLamportClock = new LamportClock();
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

        int startTime = -1;
        String expected = "";
        int unexpectedCount = 0;
        Boolean finished = false;
        Boolean waiting = false;
        retryCount = 0;

        try
        {
            while (!finished && unexpectedCount < 5)
            {
                if (!expected.isEmpty())
                {
                    HTTPObject reply = readServerResponse();
                    
                    if (reply.type == HTTPObject.RequestType.RES && expected.equals("weather"))
                    {
                        System.out.println(reply.type + " " + reply.code + " " + reply.errorMessage);
                        Boolean good = handleResponse(reply);
                        if (!good)
                        {
                            retryCount++;
                            if (retryCount >= 5)
                            {
                                throw new IOException("Server not ready");
                            }
                            waiting = false;
                            expected = "";
                        }
                        else finished = true;
                    }
                    else if (reply.type == HTTPObject.RequestType.RES && expected.equals("lamport") && reply.code == 200)
                    {
                        startTime = reply.lamportTime;
                        System.out.println(reply.type + " " + reply.code + " " + reply.errorMessage);
                        mLamportClock.newTime(startTime);
                        expected = "";
                    }
                    else 
                    {
                        System.out.println("Unexpected response number: " + unexpectedCount++);
                        expected = "";
                    }

                    waiting = false;
                }
                else if (startTime == -1 && !waiting)
                {   
                    expected = "lamport";
                    System.out.println("Reply from GET /lamport: ");
                    getRequest("./lamport", mLamportClock.increment());
                    
                    waiting = true;
                }
                else if (startTime != -1 && !waiting)
                {
                    expected = "weather";
                    System.out.println("Reply from GET /weather.json: ");
                    getRequest("./weather.json", mLamportClock.increment());

                    waiting = true;
                }
            }

            if (unexpectedCount > 5) throw new IOException("More than 5 unexpected messages");
        }
        catch (IOException e)
        {
            System.out.println("Some error with client / server communication" + e);
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

    private static Boolean handleResponse(HTTPObject reply) throws IOException, Exception
    {
        if (reply.code != 200)
        {
            System.out.println("Server data not available");
            return false;
        }

        String jsonObject = "";

        for (String rep : reply.data)
        {
            jsonObject += rep;
        }

        ArrayList<String> allJson = mJsonParser.GetFromNestAsString(jsonObject);
        
        for (String JSON : allJson)
        {
            mJsonParser.printString(JSON);
            System.out.println(" ");
        }
        
        return true;
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
    public static HTTPObject readServerResponse() throws IOException, Exception
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        HTTPObject http = new HTTPObject("NULL");

        while (http.type == HTTPObject.RequestType.NULL)
        {   
            http = mHTTPParser.parse(reader);
        }

        return http;
    }
}