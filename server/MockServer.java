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

            replyTimeOnConnect(writer, reader);

            try
            {   
                clientMessage = mParser.parse(reader);
            } 
            catch (Exception e)
            {
                System.out.println(e);
            }

            String re = "{ Good job sending GET request! }";

            writer.println("HTTP/1.1 200 OK");
            writer.println("User-Agent: ATOMClient/1/0");
            writer.println("Content-Type: application/json");
            writer.println("Lamport-Time: 0");
            writer.println("Content-Length:" + re.length());
            writer.println(re);
 
        } catch (Exception ex) 
        {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void replyTimeOnConnect(PrintWriter writer, BufferedReader reader) throws IOException, Exception
    {

        HTTPObject timeCollection = new HTTPObject("NULL");

        while (timeCollection.type != HTTPObject.RequestType.GET) 
        {
            timeCollection = mParser.parse(reader);
        }

        writer.println("HTTP/1.1 200 OK");
        writer.println("User-Agent: ATOMClient/1/0");
        writer.println("Content-Type: application/json");
        writer.println("Lamport-Time: 1");
        writer.println("Content-Length:0");
    }

    public boolean clientConnected = false;
    public HTTPObject clientMessage = new HTTPObject("NULL");
}