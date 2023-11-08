package test;

import paxos.Server;

public class ThreadedServer extends Thread {
    
    private int port;
    public Server server;

    public ThreadedServer(int p)
    {   
        port = p;
        server = new Server();
    }

    public void run()
    {
        String[] args = {Integer.toString(port)};

        server.main(args);
    }   
}
