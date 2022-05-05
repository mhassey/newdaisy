package com.daisy.broadcast.broadcastforbackgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.daisy.utils.Constraint;
import com.daisy.service.BackgroundService;
import com.daisy.utils.Utils;

import java.util.concurrent.TimeUnit;


/**
 * AlarmReceiver handles the broadcast message and generates Notification
 */
public class AlarmReceiverForBackground extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Utils.isMyServiceRunning(BackgroundService.class, context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startService(new Intent(context, BackgroundService.class));
            } else {
                context.startService(new Intent(context, BackgroundService.class));
            }
            //  context.startService(new Intent(context, BackgroundService.class));
        }
        long time1 = TimeUnit.SECONDS.toMillis(Constraint.FIVE);
        Utils.constructJobForBackground(time1, context);
    }


}

