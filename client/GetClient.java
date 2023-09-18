package client;

import parsers.JsonParser;

import java.net.*;
import java.io.*;
import java.util.*;

public class GetClient
{

    private static String hostname = "localhost";
    private static Socket socket = new Socket();

    JsonParser mJsonParser;

    public GetClient()
    {
        Map<String, String> data = new HashMap<String, String>();

        //Add all of our data types
        data.put("id", "String");
        data.put("name", "String");
        data.put("state", "String");
        data.put("time_zone", "String");
        data.put("lat", "int");
        data.put("lon", "int");
        data.put("local_date_time", "String");
        data.put("local_date_time_full", "String");
        data.put("air_temp", "int");
        data.put("apparent_t", "int");
        data.put("cloud", "String");
        data.put("dewpt", "int");
        data.put("press", "int");
        data.put("rel_hum", "int");
        data.put("wind_dir", "String");
        data.put("wind_spd_kmh", "int");
        data.put("wind_spd_kt", "int");

        mJsonParser = new JsonParser(data);
    }

    public static void main(String[] args)  throws Exception
    {
        if (args.length < 1) return;
 
        int port = Integer.parseInt(args[0]);
 
        connect(port);

        try
        {
            getRequest("/path", "Hmmm");
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

    public static void getRequest(String path, String content) throws IOException, Exception
    {
        // Create input and output streams to read from and write to the server
        PrintStream out = new PrintStream( socket.getOutputStream() );
        BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        // Follow the HTTP protocol of GET <path> HTTP/1.0 followed by an empty line
        out.println("GET " + path + " HTTP/1.1");
    }

    public static ArrayList<String> readResponse() throws IOException, Exception
    {
        BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        ArrayList<String> responseDoc = new ArrayList<String>();

        String line = in.readLine();

        if (!line.contains("PUT")) throw new Exception("Unexpected response");

        line = in.readLine();
        if (!line.contains("contentType"))
        {
            throw new Exception("Incorrect PUT request format, needs content type");
        }
        line = in.readLine();
        if (!line.contains("contentLength"))
        {
            throw new Exception("Incorrect PUT request format, needs content length");
        }

        int len = Integer.parseInt(line.substring(line.indexOf(":") + 1));

        String JSON = "";
        int totalLen = 0;

        System.out.println("length is; " + len);

        while (len - totalLen != 0)
        {
            JSON = in.readLine();
            totalLen += JSON.length();
            responseDoc.add(JSON);
            if (totalLen > len) throw new Exception("Unexpected package length");
        }

        return responseDoc;
    }
}