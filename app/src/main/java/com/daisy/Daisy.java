package com.daisy;

import android.app.Application;

public class Daisy extends Application {
    public static Daisy sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

    }

    public static Daisy getInstance(){
        if (sInstance== null) {
            synchronized(Daisy.class) {
                if (sInstance == null)
                    sInstance = new Daisy();
            }
        }
        // Return the instance
        return sInstance;
    }
}
