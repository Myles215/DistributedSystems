package parsers;

import java.util.*;
import java.lang.*;
import java.io.*;

public class FileParser
{

    //We delete data older than 30 seconds
    private long timeAllowed = 30000;

    public FileParser() throws IOException
    {
        File dir = new File("./midwayFiles");
        dir.mkdir();

        File data = new File("./allData.txt");
        data.createNewFile();

        Startup(System.currentTimeMillis());
    }

    public void PlaceInFile(String s, long timestamp) throws IOException
    {
        String timeStampS = Long.toString(timestamp);

        // Open a temporary file to write to.
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./midwayFiles/" + timeStampS + ".txt")));
        writer.println(timeStampS);
        writer.println(s);

        writer.close();

        AddToData(s, timestamp);

        //Delete midway file too
        File midway = new File("./midwayFiles/" + timeStampS + ".txt");
        midway.delete();
    }

    public void AddToData(String s, long timestamp) throws IOException
    {
        //Check if any data is old
        BufferedReader br = new BufferedReader(new FileReader("./allData.txt"));
        String line;

        //Now writing to main data file
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./temp.txt")));

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            if (line.contains("time")) {
                int i = line.indexOf("-");

                long time = Long.parseLong(line.substring(line.indexOf("-") + 1));

                if (time > timestamp - timeAllowed)
                {
                    writer.println(line);
                    line = br.readLine();
                    writer.println(line);
                }
            }
        }

        writer.println("time-" + Long.toString(timestamp));
        writer.println(s);

        br.close();
        writer.close();

        File oldData = new File("./allData.txt");

        // remove the old file
        if (oldData.delete()) 
        {
            System.out.println("Good");
        }
        else System.out.println("Bad");

        new File("./temp.txt").renameTo(realName); // Rename temp file
    }

    public void Startup(long timestamp) throws IOException
    {
        File dir = new File("./midwayFiles");
        File[] midways = dir.listFiles();

        for (File midway : midways) 
        {
            //read out data
            FileReader reader = new FileReader(midway);
            BufferedReader br = new BufferedReader(reader);

            long time = Long.parseLong(br.readLine());

            if (time > timestamp - timeAllowed)
            {
                String s = br.readLine();
                AddToData(s, time);
            }

            midway.delete();
        }
    }
}