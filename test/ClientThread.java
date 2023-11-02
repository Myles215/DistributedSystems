package test;

import paxos.PaxosClient;

public class ClientThread extends Thread
    {
        PaxosClient client;
        String clientRole = null;
        int port;
        int ID;
        String value;

        public ClientThread(int p, int id, String val)
        {
            client = new PaxosClient();
            value = val;
            ID = id;
            port = p;
        }

        public ClientThread(int p, int id, String val, int role)
        {
            String r = "normal";

            if (role == 1)
            {
                r = "M1";
            }
            else if (role == 2)
            {
                r = "M2";
            }
            else if (role == 3)
            {
                r = "M3";
            }
            else if (role >= 4 && role <= 9)
            {
                r = "M4t9";
            }

            client = new PaxosClient();
            value = val;
            ID = id;
            port = p;
            clientRole = r;
        }

        public void run()
        {
            if (value != null)
            {
                if (clientRole != null)
                {
                    String[] args = {Integer.toString(ID), Integer.toString(port), value, clientRole};
                    client.main(args);
                }
                else
                {
                    String[] args = {Integer.toString(ID), Integer.toString(port), value};
                    client.main(args);
                }
            }
            else
            {
                if (clientRole != null)
                {
                    String[] args = {Integer.toString(ID), Integer.toString(port), "", clientRole};
                    client.main(args);
                }
                else
                {
                    String[] args = {Integer.toString(ID), Integer.toString(port)};
                    client.main(args);
                }
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