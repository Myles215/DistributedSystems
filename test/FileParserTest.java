import parsers.FileParser;

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

public class FileParserTest
{
    @Before
    public void eraseAll() throws IOException, Exception
    {
        File oldData = new File("./allData.txt");
        oldData.delete();

        File oldTemp = new File("./temp.txt");
        oldTemp.delete();

        File dir = new File("./midwayFiles");
        File[] midways = dir.listFiles();

        for (File midway : midways) 
        {
            midway.delete();
        }

        File newData = new File("./allData.txt");
        newData.createNewFile();
    }

    FileParser parser;

    public FileParserTest() throws IOException
    {
        File data = new File("./allData.txt");
        data.delete();

        File dir = new File("./midwayFiles");
        File[] midways = dir.listFiles();

        for (File midway : midways) 
        {
            midway.delete();
        }

        parser = new FileParser();
    }

    @Test
    public void AddString() throws IOException
    {
        String content = "{ \"name\" : \"AddString\" }";
        long timeNow = System.currentTimeMillis();

        parser.PlaceInFile(content, timeNow, 1);

        File checkFile = new File("allData.txt");

        assertEquals(checkFile.createNewFile(), false);

        BufferedReader br = new BufferedReader(new FileReader("allData.txt"));

        assertEquals(br.readLine(), "time-" + Long.toString(timeNow));
        assertEquals(br.readLine(), "AddString:1 " + content);

        br.close();
    }

    @Test
    public void AddMultipleStrings() throws IOException
    {
        String content1 = "{ \"name\" : \"Test1\" }";
        long time1 = System.currentTimeMillis();

        parser.PlaceInFile(content1, time1, 1);

        String content2 = "{ \"name\" : \"Test2\" }";
        long time2 = System.currentTimeMillis() + 1;

        parser.PlaceInFile(content2, time2, 2);

        String content3 = "{ \"name\" : \"Test3\" }";
        long time3 = System.currentTimeMillis() + 2;

        parser.PlaceInFile(content3, time3, 3);

        File checkFile = new File("allData.txt");

        assertEquals(checkFile.createNewFile(), false);

        BufferedReader br = new BufferedReader(new FileReader("allData.txt"));

        assertEquals(br.readLine(), "time-" + Long.toString(time1));
        assertEquals(br.readLine(), "Test1:1 " + content1);

        assertEquals(br.readLine(), "time-" + Long.toString(time2));
        assertEquals(br.readLine(), "Test2:2 " + content2);

        assertEquals(br.readLine(), "time-" + Long.toString(time3));
        assertEquals(br.readLine(), "Test3:3 " + content3);

        br.close();
    }

    @Test
    public void TestRestart() throws IOException
    {
        String content1 = "{ \"name\" : \"Myles\" }";
        long time1 = System.currentTimeMillis();

        //Now writing to backup
        File create = new File("./midwayFiles/" + Long.toString(time1) + ".txt");
        create.createNewFile();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./midwayFiles/" + Long.toString(time1) + ".txt")));

        writer.println(Long.toString(time1));
        writer.println(":1 " + content1);
        writer.close();

        String content2 = "{ \"name\" : \"Test\" }";
        long time2 = System.currentTimeMillis() + 1;

        //Now writing to backup
        create = new File("./midwayFiles/" + Long.toString(time2) + ".txt");
        create.createNewFile();
        writer = new PrintWriter(new BufferedWriter(new FileWriter("./midwayFiles/" + Long.toString(time2) + ".txt")));

        writer.println(Long.toString(time2));
        writer.println(":2 " + content2);
        writer.close();

        File other = new File("allData.txt");
        assertEquals(other.delete(), true);

        FileParser tester = new FileParser();

        File checkFile = new File("allData.txt");
        assertEquals(checkFile.createNewFile(), false);

        BufferedReader br = new BufferedReader(new FileReader("allData.txt"));

        String line = br.readLine();
        assertEquals(line, "time-" + Long.toString(time1));
        line = br.readLine();
        assertEquals(line, "Myles:1 " + content1);

        assertEquals(br.readLine(), "time-" + Long.toString(time2));
        assertEquals(br.readLine(), "Test:2 " + content2);

        br.close();
    }

    @Test
    public void ReadSingleStringFromFile() throws IOException, Exception
    {
        AddString();

        ArrayList<String> check = parser.ReturnFromFile();

        assertEquals(check.size(), 1);
        assertEquals(check.get(0), "{ \"name\" : \"AddString\" }");
    }   

    @Test
    public void ReadMultipleStringFromFile() throws IOException, Exception
    {
        AddMultipleStrings();

        ArrayList<String> check = parser.ReturnFromFile();

        assertEquals(check.size(), 3);
        assertEquals(check.get(0), "{ \"name\" : \"Test1\" }");
        assertEquals(check.get(1), "{ \"name\" : \"Test2\" }");
        assertEquals(check.get(2), "{ \"name\" : \"Test3\" }");
    }   

    @Test
    public void SkipsOldData() throws IOException, Exception
    {
        String OldContent = "Old Content - won't be in file";
        long time = System.currentTimeMillis() - 30001;

        parser.PlaceInFile(OldContent, time, 1);

        //Read multiple strings should only find the new content it adds
        ReadMultipleStringFromFile();
    }
}