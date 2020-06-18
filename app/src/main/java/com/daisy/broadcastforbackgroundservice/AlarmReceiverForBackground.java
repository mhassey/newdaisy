package com.daisy.broadcastforbackgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.daisy.R;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.service.BackgroundService;
import com.daisy.utils.Utils;
import com.rvalerio.fgchecker.AppChecker;

import java.util.concurrent.TimeUnit;

/**
 * Created by ptyagi on 4/17/17.
 */

/**
 * AlarmReceiver handles the broadcast message and generates Notification
 */
public class AlarmReceiverForBackground extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {

        if (!Utils.isMyServiceRunning(BackgroundService.class, context)) {
            context.startService(new Intent(context, BackgroundService.class));
        }
        long time1 = TimeUnit.SECONDS.toMillis(Constraint.FIVE);
        Utils.constructJobForBackground(time1, context);
    }


}

