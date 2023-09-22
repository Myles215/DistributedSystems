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

        Startup(System.currentTimeMillis());
    }

    public Boolean PlaceInFile(String s, long timestamp) throws IOException
    {
        String timeStampS = Long.toString(timestamp);

        // Open a temporary file to write to.
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./midwayFiles/" + timeStampS + ".txt")));
        writer.println(timeStampS);
        writer.println(s);

        writer.close();

        Boolean ret = AddToData(s, timestamp);

        //Delete midway file too
        File midway = new File("./midwayFiles/" + timeStampS + ".txt");
        midway.delete();

        return ret;
    }

    public Boolean AddToData(String s, long timestamp) throws IOException
    {
        //Check if the data is being created for the first time
        File oldData = new File("./allData.txt");
        Boolean ret = oldData.createNewFile();

        //Check if any data is old
        BufferedReader br = new BufferedReader(new FileReader("./allData.txt"));
        String line;

        //Now writing to main data file
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./temp.txt")));

        while ((line = br.readLine()) != null && !line.isEmpty()) 
        {
            if (line.contains("time")) 
            {
                long time = Long.parseLong(line.substring(line.indexOf("-") + 1));

                if (time > timestamp - timeAllowed)
                {
                    writer.println(line);
                    line = br.readLine();
                    writer.println(line);
                }
                else
                {
                    br.readLine();
                }
            }
        }

        writer.println("time-" + Long.toString(timestamp));
        writer.println(s);

        br.close();
        writer.close();

        // remove the old file
        oldData.delete();

        new File("./temp.txt").renameTo(oldData); // Rename temp file
        return ret;
    }

    public void Startup(long timestamp) throws IOException
    {
        File dir = new File("./midwayFiles");
        File[] midways = dir.listFiles();

        for (File midway : midways) 
        {
            //read out data
            BufferedReader br = new BufferedReader(new FileReader(midway));

            long time = Long.parseLong(br.readLine());

            if (time > timestamp - timeAllowed)
            {
                String s = br.readLine();
                AddToData(s, time);
            }

            br.close();
            midway.delete();
        }
    }

    public ArrayList<String> ReturnFromFile() throws FileNotFoundException, IOException, Exception
    {
        ArrayList<String> allJson = new ArrayList<String>();

        //read out data
        BufferedReader br;
        long currentTime = System.currentTimeMillis();

        try
        {
            br = new BufferedReader(new FileReader("./allData.txt"));
        }
        catch (FileNotFoundException e)
        {
            return allJson;
        }

        String line;

        while ((line = br.readLine()) != null && line.length() != 0)
        {
            if (line.contains("time"))
            {
                long time = Long.parseLong(line.substring(line.indexOf('-') + 1));
                if (time > currentTime - timeAllowed) 
                {
                    System.out.println("Hmmmm");
                    allJson.add(br.readLine());
                }
                else br.readLine();
            }
            else
            {
                throw new Exception("Incorrect file format for agg server allData.txt");
            }
        }

        br.close();

        return allJson;
    }

}