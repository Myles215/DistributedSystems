package parsers;

import java.util.*;
import java.lang.*;

public class JsonParser
{
    public static void main(String[] args)
    {

    }

    Map<String, String> mDataTypes = new HashMap<String, String>();

    public JsonParser(Map<String, String> dataTypes)
    {
        mDataTypes = dataTypes;
    }

    public String encodeFromMap(HashMap<String, String> data)
    {
        return "";
    }

    public Map<String, String> decodeFromString(String json) throws Exception
    {
        Map<String, String> ret = new HashMap<String, String>();

        if (json.charAt(0) == '{')
        {
            index = 1;
            ret = recurGetJson(json, '}');
        }
        else
        {
            throw new Exception("Json format incorrect, must start with {");
        }

        return ret;
    }

    int index;

    Map<String, String> recurGetJson(String rawJson, char endChar) throws Exception
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

                if (!mDataTypes.containsKey(dataName))
                {
                    throw new Exception("Data name " + dataName + " not in Json definition");
                }

                if (mDataTypes.get(dataName).equals("String"))
                {
                    while (rawJson.charAt(++index) != '"');
                    index++;
                    ret.put(dataName, getAsString(rawJson));
                }
                else
                {
                    while (rawJson.charAt(++index) != ':');
                    while (rawJson.charAt(++index) == ' ');
                    ret.put(dataName, getAsInt(rawJson, dataName));
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

    String getAsString(String rawJson)
    {

        String data = "";

        while (rawJson.charAt(index) != '"')
        {
            data += rawJson.charAt(index++);
        }

        return data;
    }

    String getAsInt(String rawJson, String dataName) throws Exception
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