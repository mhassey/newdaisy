package com.daisy.optimalPermission.broadcast.broadcastforbackgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.daisy.optimalPermission.activity.mainActivity.MainActivity;
import com.daisy.optimalPermission.session.SessionManager;


public class AlarmBootBackgroundReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SessionManager.get().getLogout()) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

                //only enabling one type of notifications for demo purposes
                AlaramHelperBackground.scheduleRepeatingElapsedNotification(context);
            }
            try {
                if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                    Intent intent2 = new Intent(context, MainActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
            }
            catch (Exception e)
            {

            }
        }
    }
}
