package parsers;

import java.util.*;
import java.lang.*;

public class JsonObject
{
    public Map<String, String> mJsonMap = new HashMap<String, String>();

    public String JsonToString(HashMap<String, String> data)
    {
        return "";
    }

    //Turn JSON string to map
    public void StringToObject(String json) throws Exception
    {
        Map<String, String> ret = new HashMap<String, String>();

        index = json.indexOf('{') + 1;

        if (index != -1)
        {
            ret = recurGetJson(json, '}');
        }
        else
        {
            throw new Exception("Json format incorrect, must start with {");
        }

        mJsonMap = ret;
    }

    //Print JSON string as key : value
    public void printString(String rawJson) throws Exception
    {

        char endChar = '}';

        while (rawJson.charAt(index) != endChar)
        {
            if (rawJson.charAt(index) == '"')
            {
                String dataName = "";
                String data = "";

                while (rawJson.charAt(++index) != '"')
                {
                    dataName += rawJson.charAt(index);
                }

                while (rawJson.charAt(++index) != ':');
                while (rawJson.charAt(++index) == ' ');

                if (rawJson.charAt(index) == '"')
                {
                    index++;
                    data = getAsString(rawJson);
                }
                else if (rawJson.charAt(index) == '-' || Character.isDigit(rawJson.charAt(index)))
                {
                    data = getAsInt(rawJson, dataName);
                }
                else 
                {
                    throw new Exception("Data name " + dataName + " not in correct format");
                }

                System.out.println(dataName + " : " + data);
                
            }
            index++;
            
            if (index >= rawJson.length())
            {
                throw new Exception("Json doesn't end with specified char");
            }
        }
    }

    private int index;

    //Recursively get json
    public Map<String, String> recurGetJson(String rawJson, char endChar) throws Exception
    {

        Map<String, String> ret = new HashMap<String, String>();

        while (rawJson.charAt(index) != endChar)
        {
            if (rawJson.charAt(index) == '"')
            {
                String dataName = "";

                while (rawJson.charAt(++index) != '"')
                {
                    dataName += rawJson.charAt(index);
                }

                while (rawJson.charAt(++index) != ':');
                while (rawJson.charAt(++index) == ' ');

                if (rawJson.charAt(index) == '"')
                {
                    index++;
                    ret.put(dataName, getAsString(rawJson));
                }
                else if (rawJson.charAt(index) == '-' || Character.isDigit(rawJson.charAt(index)))
                {
                    ret.put(dataName, getAsInt(rawJson, dataName));
                }
                else 
                {
                    throw new Exception("Data name " + dataName + " not in correct format");
                }
                
            }
            index++;
            
            if (index >= rawJson.length())
            {
                throw new Exception("Json doesn't end with specified char");
            }
        }

        return ret;
    }

    //Get String variable type
    private String getAsString(String rawJson)
    {

        String data = "";

        while (rawJson.charAt(index) != '"')
        {
            data += rawJson.charAt(index++);
        }

        return data;
    }

    //get int variable type
    private String getAsInt(String rawJson, String dataName) throws Exception
    {
        String data = "";

        while (rawJson.charAt(index) != ' ' && rawJson.charAt(index) != ',')
        {
            data += rawJson.charAt(index++);
        }

        boolean seenDec = false;

        for (int i = 0 ; i < data.length() ; i++)
        {
            if (!Character.isDigit(data.charAt(i)))
            {
                if (data.charAt(i) == '.')
                {
                    if (!seenDec) seenDec = true;
                    else throw new Exception("Data type " + dataName + " should be int. Has multiple decimals");
                }
                else if (i != 0 || data.charAt(i) != '-')
                {
                    throw new Exception("Data type " + dataName + " should be int");
                }
            }
        }

        return data;
    }
}