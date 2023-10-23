package parsers;

import java.util.*;
import java.lang.*;
import java.io.*;

public class FileParser
{
    //We delete data older than 30 seconds
    private long timeAllowed = 30000;
    private JsonObject mJsonParser = new JsonObject();

    public FileParser() throws IOException
    {
        File dir = new File("./midwayFiles");
        dir.mkdir();

        Startup(System.currentTimeMillis());
    }

    //Place string in file with timestamp
    public Boolean PlaceInFile(String s, long timestamp, int lamportTime) throws IOException
    {
        String timeStampS = Long.toString(timestamp);

        // Open a temporary file to write to.
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./midwayFiles/" + timeStampS + ".txt")));
        writer.println(timeStampS);
        writer.println(":" + Integer.toString(lamportTime) + " " + s);

        writer.close();

        Boolean ret = AddToData(s, timestamp, lamportTime);

        //Delete midway file too
        File midway = new File("./midwayFiles/" + timeStampS + ".txt");
        midway.delete();

        return ret;
    }

    //Add string to data file, as we go, delete data older than 30s
    private Boolean AddToData(String s, long timestamp, int lamportTime) throws IOException
    {
        //Check if the data is being created for the first time
        File oldData = new File("./allData.txt");
        Boolean ret = oldData.createNewFile();

        //Check if any data is old
        BufferedReader br = new BufferedReader(new FileReader("./allData.txt"));
        String line;
        Boolean written = false;

        //Now writing to main data file
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./temp.txt")));

        while ((line = br.readLine()) != null && !line.isEmpty()) 
        {
            if (line.contains("time")) 
            {
                long time = Long.parseLong(line.substring(line.indexOf("-") + 1));

                if (time > timestamp - timeAllowed)
                {
                    line = br.readLine();

                    String loc = line.substring(0, line.indexOf(':'));
                    String timeS = line.substring(line.indexOf(':'));
                    int lam = Integer.parseInt(timeS.substring(1, timeS.indexOf(' ')));

                    try
                    {
                        if (loc.equals(mJsonParser.getDataName(s.substring(s.indexOf('{')), "name")) && lamportTime > lam)
                        {
                            writer.println("time-" + Long.toString(timestamp));
                            writer.println(mJsonParser.getDataName(s.substring(s.indexOf('{')), "name") + ":" + lamportTime + " " + s);
                            written = true;
                        }
                        else if (loc.equals(mJsonParser.getDataName(s.substring(s.indexOf('{')), "name")))
                        {
                            written = true;
                        }
                        else
                        {
                            writer.println("time-" + Long.toString(time));
                            writer.println(line);
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println("Error translating file, omitting corrupted entry: " + e);
                    }
                }
                else
                {
                    br.readLine();
                }
            }
        }

        if (!written)
        {
            try
            {   
                writer.println("time-" + Long.toString(timestamp));
                writer.println(mJsonParser.getDataName(s, "name") + ":" + lamportTime + " " + s);
            }
            catch (Exception e)
            {
                System.out.println("Exception reading Json: " + e);
            }
        }

        br.close();
        writer.close();

        // remove the old file
        oldData.delete();

        new File("./temp.txt").renameTo(oldData); // Rename temp file
        return ret;
    }

    //Check for files that may have been left on restart
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
                AddToData(s.substring(s.indexOf(' ') + 1), time, Integer.parseInt(s.substring(1, s.indexOf(' '))));
            }

            br.close();
            midway.delete();
        }
    }

    //Get saved data from file, if older than 30s don't read
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
            throw new Exception("503 data not available yet");
        }

        String line;

        while ((line = br.readLine()) != null && line.length() != 0)
        {
            if (line.contains("time"))
            {
                long time = Long.parseLong(line.substring(line.indexOf('-') + 1));
                if (time > currentTime - timeAllowed) 
                {
                    line = br.readLine();
                    allJson.add(line.substring(line.indexOf(' ') + 1));
                }
                else br.readLine();
            }
            else
            {
                br.close();
                throw new Exception("Incorrect file format for agg server allData.txt");
            }
        }

        br.close();

        return allJson;
    }

}