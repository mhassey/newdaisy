package com.ally.broadcast.bootcomplete;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
            Log.e("reciever", "Boot complete");


//            Intent intent2 = new Intent(context, WifiTimeCorrectionActivity.class);
//            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent2);

        }

    }

}
