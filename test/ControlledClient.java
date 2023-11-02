package test;

import paxos.PaxosClient;

public class ControlledClient extends Thread
    {
        PaxosClient client;
        int port;
        int ID;
        String value;

        public ControlledClient(int p, int id, String val)
        {
            client = new PaxosClient();
            value = val;
            ID = id;
            port = p;
        }

        public void run()
        {
            if (value != null)
            {
                String[] args = {Integer.toString(ID), Integer.toString(port), value};
                client.main(args);
            }
            else
            {
                String[] args = {Integer.toString(ID), Integer.toString(port)};
                client.main(args);
            }
        }

        public String CommittedValue()
        {
            return client.committed;
        }

        public void SetCommittedValue()
        {
            while (CommittedValue() == null) client.committed = "STOP";
        }
    }