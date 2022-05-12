package com.daisy.mainDaisy.broadcast.broadcastforbackgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.daisy.mainDaisy.common.session.SessionManager;
import com.daisy.mainDaisy.utils.Constraint;
import com.daisy.mainDaisy.service.BackgroundService;
import com.daisy.mainDaisy.utils.Utils;

import java.util.concurrent.TimeUnit;


/**
 * AlarmReceiver handles the broadcast message and generates Notification
 */
public class AlarmReceiverForBackground extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SessionManager.get().getLogout()) {
            if (!Utils.isMyServiceRunning(BackgroundService.class, context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, BackgroundService.class));
                } else {
                    context.startService(new Intent(context, BackgroundService.class));
                }
                //  context.startService(new Intent(context, BackgroundService.class));
            }
            long time1 = TimeUnit.SECONDS.toMillis(Constraint.FIVE);
            Utils.constructJobForBackground(time1, context);
        }
    }


}

