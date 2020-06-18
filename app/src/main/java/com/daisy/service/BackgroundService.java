package com.daisy.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.daisy.R;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.lockscreen.OverlayActivity;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.notification.NotificationHelper;
import com.daisy.utils.Utils;
import com.rvalerio.fgchecker.AppChecker;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BackgroundService extends Service implements View.OnTouchListener{

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    private static final String ACTION_DEBUG = "daichan4649.lockoverlay.action.DEBUG";
    private String TAG = this.getClass().getSimpleName();
    // window manager
    private WindowManager mWindowManager;
    // linear layout will use to detect touch event
    private LinearLayout touchLayout;
    private int count=0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // do your jobs here

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground();
        registerOverlayReceiver();
        handleClick();
        setWindowManager();
        setCounter();

    }

    private void setCounter() {
        Timer T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                count++;
                Log.e("kali",count+"");
                if (count==30)
                {
                    AppChecker appChecker = new AppChecker();
                    String packageName = appChecker.getForegroundApp(getApplicationContext());
                    Intent intent1 = new Intent(Intent.ACTION_MAIN);
                    intent1.addCategory(Intent.CATEGORY_HOME);
                     ResolveInfo defaultLauncher = getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY);
                    String nameOfLauncherPkg = defaultLauncher.activityInfo.packageName;
                    if (packageName!=null && packageName.equals(nameOfLauncherPkg))
                    {

                        bringApplicationToFront(getApplicationContext());
                    }
                    count=0;

                }
            }
        }, 1000, 1000);
    }

    private void bringApplicationToFront(final Context context) {
        try {
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


    private void registerOverlayReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(ACTION_DEBUG);
        registerReceiver(overlayReceiver, filter);
    }


    private BroadcastReceiver overlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("TAG", "[onReceive]" + action);
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                 showOverlayActivity(context);
            } else if (action.equals(ACTION_DEBUG)) {
                showOverlayActivity(context);
            }
        }
    };

    private void showOverlayActivity(Context context) {
        Intent intent = new Intent(context, OverlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, EditorTool.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e("nali", "jjjjjj");
        long time = TimeUnit.SECONDS.toMillis(Constraint.THIRTY);
        constructJob(time);
        initService();
    }

    public void constructJob(long timeMiles) {
        NotificationHelper.scheduleRepeatingRTCNotification(getApplicationContext(), timeMiles);
        NotificationHelper.enableBootReceiver(getApplicationContext());
        // This ensures application killed either by clearing recent or by anyother means
        Log.e("checking", "clear");

    }


    private void handleClick() {
        touchLayout = new LinearLayout(this);
    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {
        count=0;
        return false;
    }

    private void setWindowManager() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        touchLayout.setLayoutParams(lp);
        touchLayout.setOnTouchListener(this);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // set layout parameter of window manager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.START | Gravity.TOP;
            params.x = 0;
            params.y = 0;
            mWindowManager.addView(touchLayout, params);
        }
        else {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);


            params.gravity = Gravity.START | Gravity.TOP;
            params.x = 0;
            params.y = 0;
            mWindowManager.addView(touchLayout, params);
        }
    }

    private void initService() {
        long time1 = TimeUnit.SECONDS.toMillis(Constraint.ONE);
        Utils.constructJobForBackground(time1,getApplicationContext());
    }



}