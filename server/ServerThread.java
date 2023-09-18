package server;

import parsers.FileParser;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread
{
    private Socket mSocket;
    FileParser mFileParser;

    public ServerThread(Socket client) throws FileNotFoundException, IOException
    {
        mSocket = client;
        mFileParser = new FileParser();
    }

    public void run()
    {
        try 
        {
            String clientInput = "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(mSocket.getOutputStream(), true);

            String line = reader.readLine();

            while (line != null)
            {

                if (line.contains("GET"))
                {
                    ArrayList<String> allData = new ArrayList<String>();
                    int len = 0;

                    try
                    {
                        allData = mFileParser.ReturnFromFile();
                        for (String s : allData) len += s.length();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Exception when reading file: " + e);
                    }

                    writer.println("PUT / HTTP/1.1");
                    writer.println("contentType:application/json");
                    writer.println("contentLength:" + len);
                    for (String s : allData) writer.println(s);
                }
                else
                {
                    try
                    {
                        line = reader.readLine();
                        if (!line.contains("contentType"))
                        {
                            throw new Exception("Incorrect POST request format, needs content type");
                        }
                        line = reader.readLine();
                        if (!line.contains("contentLength"))
                        {
                            throw new Exception("Incorrect POST request format, needs content length");
                        }

                        int len = Integer.parseInt(line.substring(line.indexOf(":") + 1));

                        String JSON = "";

                        while (len - JSON.length() != 0)
                        {
                            JSON += reader.readLine();
                            if (JSON.length() > len) throw new Exception("Unexpected package length");
                        }

                        long currentTime = System.currentTimeMillis();
                        mFileParser.PlaceInFile(JSON, currentTime);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Json exception: " + e);
                    }
                }

                line = reader.readLine();
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