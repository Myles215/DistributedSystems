package parsers;

import java.util.*;
import java.lang.*;
import java.io.*;

public class HTTPParser
{

    public HTTPObject parse(BufferedReader reader) throws IOException, Exception
    {
        try
        {
            return parseRequest(reader);
        }
        catch(Exception e)
        {
            if (e.toString().contains("Bad request"))
            {
                HTTPObject http = new HTTPObject("OTHER");
                http.responseStatus(400, "Bad Request");
                return http;
            }

            HTTPObject http = new HTTPObject("OTHER");
            http.responseStatus(500, "Internal server error");
            return http;
        }

    }

    public HTTPObject parseRequest(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject http;

        String line = reader.readLine();

        if (line == null)
        {
            http = new HTTPObject("NULL");
            return http;
        }

        if (line.contains("GET"))
        {
            http = new HTTPObject("GET");
        }
        else if (line.contains("PUT"))
        {
            http = new HTTPObject("PUT");
        }
        else 
        {
            String code = line.substring(0, 3);

            http = new HTTPObject("RES");

            if (code.equals("200") || code.equals("201")) 
            {
                http.status(Integer.parseInt(code), "OK");

            }
            else if (code.equals("400") || code.equals("500"))
            {
                http = new HTTPObject("RES");

                String message;
                if (code == "400") message = "Bad request";
                else message = "Internal server error";

                http.status(Integer.parseInt(code), message);
                return http;
            }
            else
            {
                throw new Exception("Bad request");
            }
        }

        line = reader.readLine();
        if (!line.contains("contentType"))
        {
            throw new Exception("Incorrect request format, needs content type");
        }

        line = reader.readLine();
        if (!line.contains("contentLength"))
        {
            throw new Exception("Incorrect request format, needs content length");
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
                throw new Exception("Internal server error");
            }   
        }

        http.status(200, "OK");

        return http;
    }   

}