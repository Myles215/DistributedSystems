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

    HTTPParser parser = new HTTPParser();

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

        assertEquals(check.code, 200);
    }

    public void PutData(BufferedReader reader) throws IOException, Exception
    {
        HTTPObject check = parser.parse(reader);

        assertEquals(check.code, 200);
    }

    @Test
    public void runAll() throws IOException, Exception
    {
        //Need to do in order
        BufferedReader reader = new BufferedReader(new FileReader("./test/HTTPParserTest.txt"));

        badRequest(reader);
        reader.readLine();

        NoContentType(reader);
        reader.readLine();

        NoContentLength(reader);
        reader.readLine();

        PutNoData(reader);
        reader.readLine();

        PutData(reader);
    }
}