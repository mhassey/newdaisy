package com.daisyy.broadcast.bootcomplete;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.daisyy.activity.splash.SplashScreen;

/**
 * Broadcast receiver
 */
public class BootReceiver extends BroadcastReceiver {


    /**
     * Handle phone reboot
     */
    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intent2 = new Intent(context, SplashScreen.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }

    }

}
