package server;

import java.net.*;
import java.io.*;
import java.util.*;

import parsers.HTTPParser;
import parsers.HTTPObject;

public class ContentServer
{
    private static String hostname = "localhost";
    private static Socket socket = new Socket();
    private static String HTTPParserInput = "./parsers/HTTPParser.txt";
    private static HTTPParser mHTTPParser = new HTTPParser(HTTPParserInput);

    static Map<String, String> mDataTypes = new HashMap<String, String>();

    public ContentServer()
    {
        setUpDataTypes();
    }

    static public void setUpDataTypes()
    {
        //Add all of our data types
        mDataTypes.put("id", "String");
        mDataTypes.put("name", "String");
        mDataTypes.put("state", "String");
        mDataTypes.put("time_zone", "String");
        mDataTypes.put("lat", "int");
        mDataTypes.put("lon", "int");
        mDataTypes.put("local_date_time", "String");
        mDataTypes.put("local_date_time_full", "String");
        mDataTypes.put("air_temp", "int");
        mDataTypes.put("apparent_t", "int");
        mDataTypes.put("cloud", "String");
        mDataTypes.put("dewpt", "int");
        mDataTypes.put("press", "int");
        mDataTypes.put("rel_hum", "int");
        mDataTypes.put("wind_dir", "String");
        mDataTypes.put("wind_spd_kmh", "int");
        mDataTypes.put("wind_spd_kt", "int");
    }

    public static void main(String args[]) throws InterruptedException, IOException, Exception
    {
        setUpDataTypes();

        if (args.length < 2 || args[0].indexOf(':') == -1) 
        {
            System.out.println("GET client needs valid host and port in format 'hostname:port'");
            return;
        }
 
        int port = Integer.parseInt(args[0].substring(args[0].indexOf(':') + 1));
        hostname = args[0].substring(0, args[0].indexOf(':'));
        String file = args[1];

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

        Thread.sleep(100);

        int startTime = getStartTime();
        Boolean foundID = false;

        try
        {
            String json = "{ ";
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            while (line != null)
            {
                int index = 0;
                while (line.charAt(index) == ' ') index++;

                String dataName = "";

                while (line.charAt(index) != ' ' && line.charAt(index) != ':') dataName += line.charAt(index++);

                while (line.charAt(index) == ' ' || line.charAt(index) == ':') index++;

                String data = "";

                while (index < line.length()) data += line.charAt(index++);

                if (mDataTypes.containsKey(dataName)) 
                {
                    if (dataName.equals("id")) foundID = true;

                    if (mDataTypes.get(dataName).equals("String"))
                    {
                        json += " \"" + dataName + "\" : " + "\"" + data + "\" ,";
                    }
                    else 
                    {
                        json += " \"" + dataName + "\" : " + data + " ,";
                    }
                }
                else
                {
                    System.out.println("Unrecognized data name: " + dataName + " in file");
                    return;
                }
                line = reader.readLine();
            }

            if (!foundID) 
            {
                System.out.println("No ID in this data");
                socket.close();
                return;
            }

            json = json.substring(0, json.length() - 1);
            json += '}';

            putRequest("/weather.json", json, startTime);
            socket.close();
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

    //Connect socket to specified port
    private static Boolean connect(int port)
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

    //Send put request with specified path and content
    public static void putRequest(String path, String content, int lamportTime) throws IOException
    {
        // Create input and output streams to read from and write to the server
        PrintStream out = new PrintStream( socket.getOutputStream() );
        BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        // Follow the HTTP protocol of GET <path> HTTP/1.0 followed by an empty line
        out.println("PUT " + path + " HTTP/1.1");
        out.println("User-Agent: ATOMClient/1/0");
        out.println("Content-Type: application/json");
        out.println("Lamport-Time: " + Integer.toString(lamportTime));
        out.println("Content-Length:" + content.length());
        out.println(content);
    }

}