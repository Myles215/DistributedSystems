package paxos;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.Object;
import paxos.LamportClock;

public class ServerThread extends Thread {
    private LamportClock mLamportClock = new LamportClock();
    private Socket connection;

    ServerThread(Socket socket)
    {
        connection = socket;
    }

    public void run()
    {
        
    }
}
