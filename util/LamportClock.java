package util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.*;
import java.io.*;

public class LamportClock
{
    private int time = 0;
    private ArrayList<Integer> mNextUp = new ArrayList<Integer>();

    synchronized public int newTime(int t)
    {
        return time = Math.max(t, time) + 1;
    }

    synchronized public int increment()
    {
        return time++;
    }

    //We want to do jobs in order so whenever we start a job, defer it for a bit before we start. This deferall
    //Involves waiting for some time while we queue the job. Queue it thread safely with this synchronised code
    synchronized public void addTime(int t)
    {
        int i = 0;

        while (i < mNextUp.size() && mNextUp.get(i) < t) i++;
        
        mNextUp.add(i, t);
    }

    synchronized public Boolean checkForContinue(int t)
    {
        if (mNextUp.get(0) == t)
        {
            return true;
        }

        return false;
    }

    synchronized public void checkForFinish(int t)
    {
        if (mNextUp.get(0) == t)
        {
            mNextUp.remove(0);
        }
    }
}