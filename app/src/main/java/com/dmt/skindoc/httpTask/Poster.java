package com.dmt.skindoc.httpTask;

import android.os.Handler;
import android.os.Looper;

public class Poster extends Handler {

    private static Poster instance;

    public static Poster getInstance() {
        if(instance==null) {
            synchronized (Poster.class){
                if (instance==null){
                    instance=new Poster();
                }
            }

        }
        return instance;
    }
    private Poster(){
        super(Looper.getMainLooper());
    }
}
