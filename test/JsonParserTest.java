package json;

import parsers.JsonObject;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.*;

public class JsonParserTest 
{
    static JsonObject jsonObject;

    @BeforeClass
    static public void setup()
    {
        jsonObject = new JsonObject();
    }

    @Test
    public void nameOnly() throws Exception
    {
        String json = "{ \"name\" : \"Myles\" }";

        jsonObject.StringToObject(json);

        assertEquals(jsonObject.mJsonMap.containsKey("name"), true);
        assertEquals(jsonObject.mJsonMap.get("name"), "Myles");
    }

    @Test
    public void NameAndId() throws Exception
    {
        String json = "{ \"name\" : \"Myles\", \"id\" : \"good ID\" }";

        jsonObject.StringToObject(json);

        assertEquals(jsonObject.mJsonMap.containsKey("name"), true);
        assertEquals(jsonObject.mJsonMap.get("name"), "Myles");

        assertEquals(jsonObject.mJsonMap.containsKey("id"), true);
        assertEquals(jsonObject.mJsonMap.get("id"), "good ID");
    }

    @Test
    public void StringAndInt() throws Exception
    {
        String json = "{ \"name\" : \"Myles\", \"lat\" : 25, \"id\" : \"good ID\", \"lon\" : 89 }";

        jsonObject.StringToObject(json);

        assertEquals(jsonObject.mJsonMap.containsKey("name"), true);
        assertEquals(jsonObject.mJsonMap.get("name"), "Myles");

        assertEquals(jsonObject.mJsonMap.containsKey("lat"), true);
        assertEquals(jsonObject.mJsonMap.get("lat"), "25");

        assertEquals(jsonObject.mJsonMap.containsKey("id"), true);
        assertEquals(jsonObject.mJsonMap.get("id"), "good ID");

        assertEquals(jsonObject.mJsonMap.containsKey("lon"), true);
        assertEquals(jsonObject.mJsonMap.get("lon"), "89");
    }

    @Test
    public void NegativeInts() throws Exception
    {
        String json = "{ \"lat\" : -25, \"lon\" : -89 }";

        jsonObject.StringToObject(json);

        assertEquals(jsonObject.mJsonMap.containsKey("lat"), true);
        assertEquals(jsonObject.mJsonMap.get("lat"), "-25");

        assertEquals(jsonObject.mJsonMap.containsKey("lon"), true);
        assertEquals(jsonObject.mJsonMap.get("lon"), "-89");
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

        jsonObject.StringToObject(json);
    }

    @Test
    public void getJustName() throws Exception
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

        String check = jsonObject.getDataName(json, "name");

        assertEquals(check, "Adelaide (West Terrace /  ngayirdapira)");
    }
}