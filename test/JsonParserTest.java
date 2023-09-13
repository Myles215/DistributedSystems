package json;

import parsers.JsonParser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.*;

public class JsonParserTest 
{
    static JsonParser jParser;

    @BeforeClass
    static public void setup()
    {
        Map<String, String> data = new HashMap<String, String>();

        //Add all of our data types
        data.put("id", "String");
        data.put("name", "String");
        data.put("state", "String");
        data.put("time_zone", "String");
        data.put("lat", "int");
        data.put("lon", "int");
        data.put("local_date_time", "String");
        data.put("local_date_time_full", "String");
        data.put("air_temp", "int");
        data.put("apparent_t", "int");
        data.put("cloud", "String");
        data.put("dewpt", "int");
        data.put("press", "int");
        data.put("rel_hum", "int");
        data.put("wind_dir", "String");
        data.put("wind_spd_kmh", "int");
        data.put("wind_spd_kt", "int");

        jParser = new JsonParser(data);
    }

    @Test
    public void nameOnly() throws Exception
    {
        String json = "{ \"name\" : \"Myles\" }";

        Map<String, String> name = jParser.decodeFromString(json);

        assertEquals(name.containsKey("name"), true);
        assertEquals(name.get("name"), "Myles");
    }

    @Test
    public void NameAndId() throws Exception
    {
        String json = "{ \"name\" : \"Myles\", \"id\" : \"good ID\" }";

        Map<String, String> name = jParser.decodeFromString(json);

        assertEquals(name.containsKey("name"), true);
        assertEquals(name.get("name"), "Myles");

        assertEquals(name.containsKey("id"), true);
        assertEquals(name.get("id"), "good ID");
    }

    @Test
    public void StringAndInt() throws Exception
    {
        String json = "{ \"name\" : \"Myles\", \"lat\" : 25, \"id\" : \"good ID\", \"lon\" : 89 }";

        Map<String, String> name = jParser.decodeFromString(json);

        assertEquals(name.containsKey("name"), true);
        assertEquals(name.get("name"), "Myles");

        assertEquals(name.containsKey("lat"), true);
        assertEquals(name.get("lat"), "25");

        assertEquals(name.containsKey("id"), true);
        assertEquals(name.get("id"), "good ID");

        assertEquals(name.containsKey("lon"), true);
        assertEquals(name.get("lon"), "89");
    }

    @Test
    public void NegativeInts() throws Exception
    {
        String json = "{ \"lat\" : -25, \"lon\" : -89 }";

        Map<String, String> name = jParser.decodeFromString(json);

        assertEquals(name.containsKey("lat"), true);
        assertEquals(name.get("lat"), "-25");

        assertEquals(name.containsKey("lon"), true);
        assertEquals(name.get("lon"), "-89");
    }

    @Test
    public void wholeTest() throws Exception
    {
        String json = "{"
                       + "\"id\" : \"IDS60901\", "
                       + "\"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", "
                       + "\"state\" : \"SA\", "
                       + "\"time_zone\" : \"CST\", "
                       + "\"lat\": -34.9, "
                       + "\"lon\": 138.6, "
                       + "\"local_date_time\": \"15/04:00pm\", "
                       + "\"local_date_time_full\": \"20230715160000\", "
                       + "\"air_temp\": 13.3, "
                       + "\"apparent_t\": 9.5, "
                       + "\"cloud\": \"Partly cloudy\", "
                       + "\"dewpt\": 5.7, "
                       + "\"press\": 1023.9, "
                       + "\"rel_hum\": 60, "
                       + "\"wind_dir\": \"S\", "
                       + "\"wind_spd_kmh\": 15, "
                       + "\"wind_spd_kt\": 8 "
                      + "}";

        Map<String, String> name = jParser.decodeFromString(json);


    }
}