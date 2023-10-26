package test;

import server.AggregationServer;
import client.GETClient;
import server.ContentServer;

import java.util.*;

public class MylesTest {

    public static class AggServerThread extends Thread
    {
        public void run()
        {
            AggregationServer aggServer = new AggregationServer();

            String[] args = {};

            aggServer.main(args);
        }
    }

    public static void main(String[] args)
    {
        ArrayList<Long> allTimes = new ArrayList<Long>();
        int timesID = 0;

        AggServerThread agg = null;
        agg.start();

        for (int i = 1;i<=5;i++)
        {
            agg = new AggServerThread();
            agg.start();

            ArrayList<GETClientThread> clients = new ArrayList<GETClientThread>();
            ArrayList<ContentServerThread> contents = new ArrayList<ContentServerThread>();
            
            for (int j = 0;j<i;j++)
            {
                long a = 0;
                allTimes.add(a);
                clients.add(new GETClientThread(timesID, allTimes));
                contents.add(new ContentServerThread(timesID, allTimes));
                timesID++;
            }

            for (int j = 0;j<i;j++)
            {
                contents.get(j).start();
                clients.get(j).start();
            }

            agg = null;
        }

        for (int i = 0;i<allTimes.size();i++)
        {
            System.out.println(allTimes.get(i));
        }
    }
}
