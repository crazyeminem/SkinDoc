package com.dmt.skindoc.utils;

import android.util.Log;

public class Logger {

    public static final String TAG="Asynchronous Test";
    public static final boolean DEBUG=true;
    public static String getMessage(Object msg){
        return msg==null?"null":msg.toString();

    }
    public static void i(Object msg)
    {
        Log.i(TAG,getMessage(msg));

    }
    public static void d(Object msg)
    {
        if (DEBUG)
            Log.d(TAG,getMessage(msg));

    }
    public static void w(Object msg)
    {
        Log.w(TAG,getMessage(msg));

    }

}
