package test;

import client.GETClient;

import java.util.*;

public class GETClientThread extends Thread
    {   
        int ID;
        ArrayList<Long> time;

        public GETClientThread(int id, ArrayList<Long> times)
        {
            ID = id;
            time = times;
        }

        public void run()
        {
            long start = System.currentTimeMillis();

            GETClient client = new GETClient();

            String[] args = {"localhost:4567"};

            try { client.main(args);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }

            System.out.println("Done");

            time.set(ID, System.currentTimeMillis() - start);
        }
    }
