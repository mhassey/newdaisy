package com.daisy.broadcast.broadcastforbackgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.common.session.SessionManager;
import com.daisy.utils.ValidationHelper;


public class AlarmBootBackgroundReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ValidationHelper.showToast(context, "dhusuadshuidas");
        if (!SessionManager.get().getLogout()) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")) {
                try {
                    //only enabling one type of notifications for demo purposes
                    AlaramHelperBackground.scheduleRepeatingElapsedNotification(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            ValidationHelper.showToast(context, "dhusua0------------dshuidas");

            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);

        }
    }
}
