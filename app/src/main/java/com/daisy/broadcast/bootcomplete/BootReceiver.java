package com.daisy.broadcast.bootcomplete;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.daisy.activity.splash.SplashScreen;

/**
 * Broadcast receiver
 */
public class BootReceiver extends BroadcastReceiver {

    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes

    /**
     * Handle phone reboot
     */
    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            final Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent intent2 = new Intent(context, SplashScreen.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
            }, 60000);

        }

    }

}
