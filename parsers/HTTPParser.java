package parsers;

import java.util.*;
import java.lang.*;
import java.io.*;

public class HTTPParser
{

    public HTTPObject parse(BufferedReader reader) throws IOException, Exception
    {
        String req = reader.readLine();

        try
        {
            if (req.contains("GET"))
            {
                return new HTTPObject("GET");
            }
            else if (req.contains("PUT"))
            {
                return parsePUT(reader);
            }
        }
        catch(Exception e)
        {
            HTTPObject http = new HTTPObject("PUT");
            http.error(500, "Internal server error");
            return http;
        }

        HTTPObject http = new HTTPObject("OTHER");
        http.error(400, "Bad Request");
        return http;
        
    }

    public HTTPObject parsePUT(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject http = new HTTPObject("PUT");

        String line = reader.readLine();
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
        int totalLen = 0;

        while (len - totalLen > 0)
        {
            JSON = reader.readLine();
            totalLen += JSON.length();
            http.addData(JSON);

            if (totalLen > len) 
            {
                http.error(500, "Internal server error");
            }   
        }

        return http;
    }   

}