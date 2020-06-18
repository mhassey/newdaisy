package com.daisy.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.daisy.R;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.notification.NotificationHelper;


public class StickyService extends Service {

    private SessionManager sessionManager;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         sessionManager=SessionManager.get();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //Code here

        if (Constraint.IS_OVER_APP_SETTING)
            screenBrightness(Constraint.CREENTBRIGHNESS);

//        long time = TimeUnit.SECONDS.toMillis(Constraint.THIRTY);
//        constructJob(time);


        stopSelf();
    }

    public void constructJob(long timeMiles) {

        NotificationHelper.scheduleRepeatingRTCNotification(getApplicationContext(), timeMiles);
        NotificationHelper.enableBootReceiver(getApplicationContext());
    }
    private void screenBrightness(int level) {
        try {
            android.provider.Settings.System.putInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, level);
        } catch (Exception e) {
            e.printStackTrace();


        }
    }


}