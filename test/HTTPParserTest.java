import parsers.HTTPParser;
import parsers.HTTPObject;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.*;
import java.net.*;
import java.io.*;

public class HTTPParserTest
{

    private static String HTTPParserInput = "./parsers/HTTPParser.txt";
    HTTPParser parser = new HTTPParser(HTTPParserInput);

    public void badRequest(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject check = parser.parse(reader);

        assertEquals(check.responseCode, 400);
    }

    public void NoContentType(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject check = parser.parse(reader);

        assertEquals(check.responseCode, 500);
    }

    public void NoContentLength(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject check = parser.parse(reader);

        assertEquals(check.responseCode, 500);
    }

    public void PutNoData(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject check = parser.parse(reader);

        assertEquals(check.responseCode, 200);
    }

    public void PutData(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject check = parser.parse(reader);

        assertEquals(check.responseCode, 200);
    }

    public void BadRes(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject check = parser.parse(reader);

        assertEquals(check.responseCode, 500);
    }

    public void GoodRes(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject check = parser.parse(reader);

        assertEquals(check.type, HTTPObject.RequestType.RES);
    }

    @Test
    public void runAll() throws IOException, Exception
    {
        //Need to do in order
        BufferedReader reader = new BufferedReader(new FileReader("./test/testFiles/HTTPParserTest.txt"));

        badRequest(reader);
        reader.readLine();

        NoContentType(reader);
        reader.readLine();

        NoContentLength(reader);
        reader.readLine();

        PutNoData(reader);
        reader.readLine();

        PutData(reader);
        reader.readLine();

        BadRes(reader);
        reader.readLine();

        GoodRes(reader);

        reader.close();
    }
}