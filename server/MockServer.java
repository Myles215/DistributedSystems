package server;

import parsers.HTTPParser;
import parsers.HTTPObject;

import java.net.*;
import java.io.*;

public class MockServer extends Thread
{
    private static String HTTPParserInput = "./parsers/HTTPParser.txt";
    HTTPParser mParser = new HTTPParser(HTTPParserInput);

    private int port;

    public MockServer(int p)
    {
        port = p;
    }

    public void run()
    {
        try (ServerSocket serverSocket = new ServerSocket(port)) 
        {
            Socket socket = serverSocket.accept();
            clientConnected = true;

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            sendTimeOnConnect(writer);

            try
            {   
                clientMessage = mParser.parse(reader);
            } 
            catch (Exception e)
            {
                System.out.println(e);
            }

            String re = "Good job sending GET request!";

            writer.println("HTTP/1.1 200 OK");
            writer.println("User-Agent: ATOMClient/1/0");
            writer.println("Content-Type: application/json");
            //TODO
            writer.println("Lamport-Time: 0");
            writer.println("Content-Length:" + re.length());
            writer.println(re);
 
        } catch (IOException ex) 
        {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendTimeOnConnect(PrintWriter writer)
    {
        writer.println("PUT /lamport HTTP/1.1");
        writer.println("User-Agent: ATOMClient/1/0");
        writer.println("Content-Type: plain/text");
        writer.println("Lamport-Time: 0");
        writer.println("Content-Length:0");
    }

    public boolean clientConnected = false;
    public HTTPObject clientMessage;
}