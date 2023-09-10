import java.util.*;

class JsonParser
{
    public static void main(String[] args)
    {

    }

    Map<String, String> mDataTypes = new HashMap<String, String>();

    public JsonParser(HashMap<String, String> dataTypes)
    {
        mDataTypes = dataTypes;
    }

    String encodeFromMap(HashMap<String, String> data)
    {
        
    }

    Map<String, String> decodeFromString(String json)
    {
        Map<String, String> ret = new HashMap<String, String>();

        if (json.indexOf(0) == '{')
        {
            index = 1;
            ret = recurGetJson(json, '}');
        }
        else
        {
            throw new Exception("Json format incorrect, must start with {");
        }
    }

    int index;

    Map<String, String> recurGetJson(String rawJson, char endChar)
    {

        Map<String, String> ret = new HashMap<String, String> ret;

        while (rawJson.indexOf(index) != endChar)
        {
            if (rawJson.indexOf(index) == '"')
            {
                String dataName = "";

                while (rawJson.indexOf(++index) != '"')
                {
                    dataName += rawJson.indexOf(index);
                }

                if (mDataTypes[dataName].equals("String"))
                {
                    while (rawJson.indexOf(++index) != '"');
                    ret.put(dataName, getAsString(rawJson, ++index));
                }
                else
                {
                    while (rawJson.indexOf(++index) != ':');
                    ret.put(dataName, getAsInt(rawJson, ++index));
                }
            }
            index++;
        }

        return ret;
    }

    String getAsString(String rawJson, int index)
    {

        String data = "";

        while (rawJson.indexOf(index) != "")
        {
            data += rawJson.indexOf(index++);
        }

        return data;
    }

    String getAsInt(String rawJson, int index)
    {
        String data = "";

        while (rawJson.indexOf(index) != "")
        {
            data += rawJson.indexOf(index++);
        }

        for (int i = 0 ; i < data.size() ; i++)
        {
            if (!isDigit(data[i]))
            {
                if (i != 0 || data[i] != '-')
                {
                    throw new Exception("Data type should be int");
                }
            }
        }

        return data;
    }
}