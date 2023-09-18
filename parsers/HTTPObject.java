package parsers;

import java.util.*;
import java.lang.*;

enum RequestType {
    GET,
    PUT,
    OTHER
}

public class HTTPObject
{
    public int error = 0;
    public String errorMessage = "";
    public ArrayList<String> data = new ArrayList<String>();
    public RequestType type;

    public HTTPObject(String t)
    {

        type = type.OTHER;

        if (t.equals("GET"))
        {
            type = RequestType.GET;
        }
        else if (t.equals("PUT"))
        {
            type = RequestType.PUT;
        }
        
        
    }

    public void error(int err, String message)
    {
        error = err;
        errorMessage = message;
    }

    public void addData(String s)
    {
        data.add(s);
    }
}