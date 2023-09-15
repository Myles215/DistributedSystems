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
    public void eraseAll() throws IOException
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

        File newData = new File("./allData.txt");
        newData.createNewFile();

        parser = new FileParser();
    }

    @Test
    public void AddString() throws IOException
    {
        String content = "Basic file content";
        long timeNow = System.currentTimeMillis();

        parser.PlaceInFile(content, timeNow);

        File checkFile = new File("allData.txt");

        assertEquals(checkFile.createNewFile(), false);

        BufferedReader br = new BufferedReader(new FileReader("allData.txt"));

        assertEquals(br.readLine(), "time-" + Long.toString(timeNow));
        assertEquals(br.readLine(), content);

        br.close();
    }

    @Test
    public void AddMultipleStrings() throws IOException
    {
        String content1 = "content 1";
        long time1 = System.currentTimeMillis();

        parser.PlaceInFile(content1, time1);

        String content2 = "content 2";
        long time2 = System.currentTimeMillis();

        parser.PlaceInFile(content2, time2);

        String content3 = "content 3";
        long time3 = System.currentTimeMillis();

        parser.PlaceInFile(content3, time3);

        File checkFile = new File("allData.txt");

        assertEquals(checkFile.createNewFile(), false);

        BufferedReader br = new BufferedReader(new FileReader("allData.txt"));

        assertEquals(br.readLine(), "time-" + Long.toString(time1));
        assertEquals(br.readLine(), content1);

        assertEquals(br.readLine(), "time-" + Long.toString(time2));
        assertEquals(br.readLine(), content2);

        assertEquals(br.readLine(), "time-" + Long.toString(time3));
        assertEquals(br.readLine(), content3);

        br.close();
    }

    @Test
    public void TestRestart() throws IOException
    {
        String content1 = "Old Contents";
        long time1 = System.currentTimeMillis();

        //Now writing to backup
        File create = new File("./midwayFiles/" + Long.toString(time1) + ".txt");
        create.createNewFile();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./midwayFiles/" + Long.toString(time1) + ".txt")));

        writer.println(Long.toString(time1));
        writer.println(content1);
        writer.close();

        String content2 = "Old Contents 2";
        long time2 = System.currentTimeMillis();

        //Now writing to backup
        create = new File("./midwayFiles/" + Long.toString(time2) + ".txt");
        create.createNewFile();
        writer = new PrintWriter(new BufferedWriter(new FileWriter("./midwayFiles/" + Long.toString(time2) + ".txt")));

        writer.println(Long.toString(time2));
        writer.println(content2);
        writer.close();

        FileParser tester = new FileParser();

        File checkFile = new File("allData.txt");

        assertEquals(checkFile.createNewFile(), false);

        BufferedReader br = new BufferedReader(new FileReader("allData.txt"));

        assertEquals(br.readLine(), "time-" + Long.toString(time1));
        assertEquals(br.readLine(), content1);

        assertEquals(br.readLine(), "time-" + Long.toString(time2));
        assertEquals(br.readLine(), content2);

        br.close();
    }

    @Test
    public void ReadSingleStringFromFile() throws IOException, Exception
    {
        AddString();

        ArrayList<String> check = parser.ReturnFromFile();

        assertEquals(check.size(), 1);
        assertEquals(check.get(0), "Basic file content");
    }   

    @Test
    public void ReadMultipleStringFromFile() throws IOException, Exception
    {
        AddMultipleStrings();

        ArrayList<String> check = parser.ReturnFromFile();

        assertEquals(check.size(), 3);
        assertEquals(check.get(0), "content 1");
        assertEquals(check.get(1), "content 2");
        assertEquals(check.get(2), "content 3");
    }   
}