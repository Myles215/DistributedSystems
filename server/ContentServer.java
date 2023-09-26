package server;

import java.net.*;
import java.io.*;
import java.util.*;


public class ContentServer
{

    private static String hostname = "localhost";
    private static Socket socket = new Socket();

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

    public static void main(String args[]) throws InterruptedException
    {
        setUpDataTypes();

        if (args.length < 2) 
        {
            System.out.println("Needs port and input file as args");
            return;
        }
 
        int port = Integer.parseInt(args[0]);
        String file = args[1];
 
        connect(port);
        Thread.sleep(500);

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

            json = json.substring(0, json.length() - 1);
            json += '}';

            putRequest("/weather.json", json);
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

    //Connect socket to specified port
    private static void connect(int port)
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

    //Send put request with specified path and content
    public static void putRequest(String path, String content) throws IOException
    {
        // Create input and output streams to read from and write to the server
        PrintStream out = new PrintStream( socket.getOutputStream() );
        BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        // Follow the HTTP protocol of GET <path> HTTP/1.0 followed by an empty line
        out.println("PUT " + path + " HTTP/1.1");
        out.println("User-Agent: ATOMClient/1/0");
        out.println("Content-Type: application/json");
        out.println("Content-Length:" + content.length());
        out.println(content);
    }

}