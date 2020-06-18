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
public class AlarmReceiver extends BroadcastReceiver {
    private SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        AppChecker appChecker = new AppChecker();
        String packageName = appChecker.getForegroundApp(context);
        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.addCategory(Intent.CATEGORY_HOME);
        Log.e("a[[---------","ap already open");

        sessionManager = SessionManager.get();
        ResolveInfo defaultLauncher = context.getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY);
        String nameOfLauncherPkg = defaultLauncher.activityInfo.packageName;
        if (packageName!=null && packageName.equals(nameOfLauncherPkg))
        {
                bringApplicationToFront(context);
        }
        else if (packageName!=null && packageName.equals(context.getString(R.string.packageName)))
        {
        Log.e("a[[","ap already open");
        }
        else
        {
            long time = TimeUnit.SECONDS.toMillis(Constraint.THIRTY);
            Utils.constructJob(time,context);
        }
    }

    private void bringApplicationToFront(final Context context) {
        try {
            sessionManager = SessionManager.get();

                // Get a handler that can be used to post to the main thread
                android.os.Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, EditorTool.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent);
                    } // This is your code
                };
                mainHandler.post(myRunnable);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

