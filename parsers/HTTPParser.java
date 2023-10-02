package parsers;

import java.util.*;
import java.lang.*;
import java.io.*;

public class HTTPParser
{

    class PathFile
    {

        public PathFile(String p, String req, String type, String agent)
        {
            mPath = p;
            mType = req;
            mDataType = type;
            mAgent = agent;
        }

        String mPath;
        String mType;
        String mDataType;
        String mAgent;
    }

    private Map<String, PathFile> mPaths = new HashMap<String, PathFile>();


    public HTTPParser(String input)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(input));
            String line = "";
            
            while ((line = br.readLine()) != null)
            {
                //Need to set properly for input
                String pathName = br.readLine();
                mPaths.put(pathName, new PathFile(pathName, br.readLine(), br.readLine(), br.readLine()));
            }

            br.close();
        }
        catch (FileNotFoundException fileNotFound)
        {
            System.out.println("No input files therefore no paths declared");
        }
        catch (IOException ioexception)
        {
            System.out.println("Failed to read input file therefore no paths declared");
        }

    }

    public HTTPObject parse(BufferedReader reader) throws IOException, Exception
    {
        try
        {
            return parseRequest(reader);
        }
        catch (IOException e)
        {
            HTTPObject http = new HTTPObject("OTHER");
            http.responseStatus(500, "Internal server error");
            return http;
        }
        catch(Exception e)
        {
            System.out.println(e);
            if (e.toString().contains("HP4"))
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

    public String getMessage(int index, String line)
    {
        String ret = "";

        while (line.charAt(index) != ' ')
        {
            ret += line.charAt(index++);
        }

        return ret;
    }

    //Parse whole HTTP request 
    private HTTPObject parseRequest(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject http;

        String line = reader.readLine();

        if (line == null)
        {
            http = new HTTPObject("NULL");
            http.status(0, "NONE");
            return http;
        }
        else if (line.substring(0, 4).equals("HTTP"))
        {
            try
            {
                return parseResponse(line, reader);
            }
            catch (Exception e)
            {
                throw new Exception("HP500: Error evaluating response HTTP: " + e);
            }
        }
        else if (!line.contains("/"))
        {
            throw new Exception("HP400: Not a route");
        }

        String pathName = line.substring(line.indexOf('/') + 1, line.indexOf('/') + 1 + line.substring(line.indexOf('/') + 1).indexOf(' '));

        if (!mPaths.containsKey(pathName))
        {
            throw new Exception("HP401: Pathname " + pathName + " not present in possible paths");
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
            throw new Exception("HP502: unrecognized route type: " + line);
        }

        line = reader.readLine();
        if (!line.contains("User-Agent") || !line.substring(line.indexOf(':') + 2).equals(mPaths.get(pathName).mAgent))
        {
            throw new Exception("HP503: Incorrect request format, needs user agent");
        }

        line = reader.readLine();
        if (!line.contains("Content-Type") || !line.substring(line.indexOf(':') + 2).equals(mPaths.get(pathName).mDataType))
        {
            throw new Exception("HP503: Incorrect request format, needs content type");
        }

        line = reader.readLine();
        if (!line.contains("Lamport-Time"))
        {
            throw new Exception("HP503: Incorrect request format, needs lamport time");
        }

        http.stamp(Integer.parseInt(line.substring(line.indexOf(':') + 2)));

        line = reader.readLine();
        if (!line.contains("Content-Length"))
        {
            throw new Exception("HP504: Incorrect request format, needs content length");
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
                throw new Exception("HP505: Total length of JSON is larger than expected length");
            }   
        }

        http.responseStatus(200, "OK");

        return http;
    }   

    //If we discover that the HTTP message is a response, go through a seperate response protocol
    private HTTPObject parseResponse(String line, BufferedReader reader) throws IOException, Exception
    {
        String code = line.substring(line.indexOf(' ') + 1, line.indexOf(' ') + 4);
        
        HTTPObject http = new HTTPObject("RES");

        if (code.equals("200") || code.equals("201")) 
        {
            http.status(Integer.parseInt(code), getMessage(line.indexOf(' '), line));
        }
        else if (code.equals("400") || code.equals("500"))
        {
            http.status(Integer.parseInt(code), getMessage(line.indexOf(' '), line));
            return http;
        }
        else
        {
            throw new Exception("HP402: Request type wasn't get or put and didn't have a numerical code");
        }

        line = reader.readLine();
        if (!line.contains("User-Agent"))
        {
            throw new Exception("HP503: Incorrect request format, needs user agent");
        }

        line = reader.readLine();
        if (!line.contains("Content-Type"))
        {
            throw new Exception("HP503: Incorrect request format, needs content type");
        }

        line = reader.readLine();
        if (!line.contains("Lamport-Time"))
        {
            throw new Exception("HP504: Incorrect request format, needs lamport time");
        }

        http.stamp(Integer.parseInt(line.substring(line.indexOf(':') + 2)));

        line = reader.readLine();
        if (!line.contains("Content-Length"))
        {
            throw new Exception("HP504: Incorrect request format, needs content length");
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
                throw new Exception("HP505: Total length of JSON is larger than expected length");
            }   
        }

        return http;
    }

}