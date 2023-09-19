package server;

import parsers.FileParser;
import parsers.HTTPParser;
import parsers.HTTPObject;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread
{
    private Socket mSocket;
    FileParser mFileParser;
    HTTPParser mHTTPParser;

    public ServerThread(Socket client) throws FileNotFoundException, IOException
    {
        mSocket = client;
        mFileParser = new FileParser();
        mHTTPParser = new HTTPParser();
    }

    public void run()
    {
        try 
        {
            String clientInput = "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(mSocket.getOutputStream(), true);

            String line = "";

            while (line != null)
            {
                try
                {   
                    HTTPObject http = mHTTPParser.parse(reader);

                    if (http.responseCode > 201)
                    {
                        writer.println(http.code + " " + http.errorMessage + " HTTP/1.1");
                    }
                    else if (http.type == HTTPObject.RequestType.PUT)
                    {
                        long currentTime = System.currentTimeMillis();
                        boolean created = mFileParser.PlaceInFile(http.data.get(0), currentTime);

                        if (created)
                        {
                            writer.println("201 Created HTTP/1.1");
                        }
                        else
                        {
                            writer.println("200 OK HTTP/1.1");
                        }
                    }
                    else if (http.type == HTTPObject.RequestType.GET)
                    {
                        ArrayList<String> allData = new ArrayList<String>();
                        int len = 0;

                        try
                        {
                            allData = mFileParser.ReturnFromFile();
                            for (String s : allData) len += s.length();

                            writer.println("200 OK HTTP/1.1");
                            writer.println("contentType:application/json");
                            writer.println("contentLength:" + len);
                            for (String s : allData) writer.println(s);
                        }
                        catch (Exception e)
                        {
                            System.out.println("Exception when reading file: " + e);
                            writer.println("500 Internal server error HTTP/1.1");
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                catch (Exception e)
                {
                    writer.println("500 Internal server error HTTP/1.1");
                }
            }

            reader.close();
            writer.close();
        } 
        catch (IOException e)
        {
            System.out.println("IOException: " + e);
        }

    }
}