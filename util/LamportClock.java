package util;

import java.util.*;
import java.lang.*;
import java.io.*;

public class LamportClock
{
    int time = 1;

    public int newTime(int t)
    {
        return time = Math.max(t, time) + 1;
    }

    public void update()
    {
        time++;
    }
}