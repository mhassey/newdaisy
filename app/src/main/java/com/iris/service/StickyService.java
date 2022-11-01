package com.iris.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.iris.common.session.SessionManager;


public class StickyService extends Service {

    private SessionManager sessionManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sessionManager = SessionManager.get();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {


    }


}