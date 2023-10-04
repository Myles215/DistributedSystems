package parsers;

import java.util.*;
import java.lang.*;

public class HTTPObject
{

    public enum RequestType {
        GET,
        PUT,
        RES,
        NULL
    }

    public int code = 0;
    public String errorMessage = "";

    public int responseCode = 0;
    public String responseMessage = "";

    public ArrayList<String> data = new ArrayList<String>();
    public String pathName;
    public RequestType type;

    public int lamportTime = -1;

    //Make a new HTTP object
    public HTTPObject(String t)
    {

        if (t.equals("GET"))
        {
            type = RequestType.GET;
        }
        else if (t.equals("PUT"))
        {
            type = RequestType.PUT;
        }
        else if (t.equals("RES"))
        {
            type = RequestType.RES;
        }
        else
        {
            type = RequestType.NULL;
        }
    }

    //set path name
    public void setPathName(String path)
    {
        pathName = path;
    }

    //Set status
    public void status(int c, String message)
    {
        code = c;
        errorMessage = message;
    }

    //Set response status
    public void responseStatus(int c, String message)
    {
        responseCode = c;
        responseMessage = message;
    }

    //Add response data
    public void addData(String s)
    {
        data.add(s);
    }

    public void stamp(int time)
    {
        lamportTime = time;
    }
}