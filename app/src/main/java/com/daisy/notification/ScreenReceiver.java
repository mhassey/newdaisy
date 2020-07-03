package com.daisy.notification;

import android.app.ActivityManager;
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
import com.daisy.utils.Utils;
import com.rvalerio.fgchecker.AppChecker;

import java.util.concurrent.TimeUnit;

/**
 * Created by ptyagi on 4/17/17.
 */

/**
 * AlarmReceiver handles the broadcast message and generates Notification
 */
public class ScreenReceiver extends BroadcastReceiver {
    private SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here


        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here

        }
    }


}

