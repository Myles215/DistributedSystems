package test;
import server.ContentServer;

import java.util.*;

public class ContentServerThread extends Thread
    {
        int ID;
        ArrayList<Long> time;

        public ContentServerThread(int id, ArrayList<Long> times)
        {
            time = times;
            ID = id;
        }

        public void run()
        {
            long start = System.currentTimeMillis();

            ContentServer client = new ContentServer();

            String[] args = {"localhost:4567", "./adelaide.txt"};

            try { client.main(args);
            } catch (Exception e) {}

            time.set(ID, System.currentTimeMillis() - start);
        }
    }