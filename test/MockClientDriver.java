package test;

import java.io.IOError;
import java.io.IOException;

public class MockClientDriver {
    public static void main(String[] args) throws IOException, InterruptedException
    {

        if (args.length < 2) 
        {
            System.out.println("needs 2 args");
        }

        int port = Integer.parseInt(args[0]);
        String message = args[1];

        MockClient m = new MockClient(port);

        m.Message(port, port, message, message);

        Thread.sleep(100);

        m.HeartBeat();

        System.out.println(m.Read());

    }
}
