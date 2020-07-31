package com.daisy.service;

import android.annotation.SuppressLint;
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
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.daisy.ObjectDetection.cam.FrontCameraRetriever;
import com.daisy.R;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.lockscreen.LockScreen;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.pojo.response.OverLayResponse;
import com.daisy.sync.SyncLogs;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.rvalerio.fgchecker.AppChecker;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BackgroundService extends Service implements View.OnTouchListener, SensorEventListener {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    private static final String ACTION_DEBUG = "daichan4649.lockoverlay.action.DEBUG";
    private String TAG = this.getClass().getSimpleName();
    // window manager
    private WindowManager mWindowManager;
    // linear layout will use to detect touch event
    private LinearLayout touchLayout;
    private int count = 0;
    private PowerManager.WakeLock mWakeLock;
    private WifiManager wifiManager;
    private AppChecker appChecker = new AppChecker();
    private SessionManager sessionManager;
    private String[] messages = {"com.google.android.apps.messaging", "com.oneplus.mms", "com.jb.gosms", "com.concentriclivers.mms.com.android.mms", "fr.slvn.mms", "com.android.mms", "com.sonyericsson.conversations"};
    private SensorManager sensorMan;
    private Sensor accelerometer;

    private float[] mGravity;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;
    private boolean isPickedUp = false;
    private int counter = 0;
    private long lastUpdate;
    private int movement = 0;
    private boolean isPickedUpSucess = false;
    private boolean isPickedDown = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sessionManager = SessionManager.get();
        // do your jobs here

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        showNotification();
        initWakeUpLock();
        registerReceiver();
        handleClick();
        setWindowManager();
        setCounter();
        initWifi();
        initPassword();
        defineSensor();
    }


    private void initPassword() {
        appChecker.whenAny(new AppChecker.Listener() {
            @Override
            public void onForeground(String process) {

                if (!sessionManager.getUninstall()) {
                    if (process.equals("com.google.android.packageinstaller")) {
                        Intent intent = new Intent(getApplicationContext(), LockScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra(Constraint.UNINSTALL, Constraint.YES);
                        intent.putExtra(Constraint.PACKAGE, Constraint.current_running_process);
                        startActivity(intent);


                    }
                }
                boolean b = sessionManager.getLock();
                if (!Constraint.current_running_process.equals(process)) {

                    storeProcess(process);
                    if (process.equals(Constraint.PLAY_STORE_PATH)) {
                        if (!b) {

                            return;
                        }
                    }
                    boolean browserLock = sessionManager.getBrowserLock();
                    if (process.equals(Constraint.CROME)) {
                        if (!browserLock) {
                            Log.e("browser..", process);

                            return;
                        }
                    }
                    boolean messageLock = sessionManager.getMessageLock();
                    if (Arrays.asList(messages).contains(process) || process.contains("mms") || process.contains("messaging")) {
                        // true
                        if (!messageLock) {
                            Log.e("mms..", process);

                            return;
                        }
                    }


                    Constraint.current_running_process = process;
                    if (!process.equals(getApplication().getPackageName())) {
                        if (process.equals(Constraint.SETTING_PATH) || process.equals(Constraint.PLAY_STORE_PATH) || process.equals(Constraint.CROME) || Arrays.asList(messages).contains(process) || process.contains("mms") || process.contains("messaging")) {
                            Log.e("password correct check", sessionManager.getPasswordCorrect() + "");
                            if (!sessionManager.getPasswordCorrect()) {

                                Intent intent = new Intent(getApplicationContext(), LockScreen.class);
                                intent.putExtra(Constraint.PACKAGE, process);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                            } else {
                                sessionManager.setPasswordCorrect(false);
                            }
                        } else {
                            sessionManager.setPasswordCorrect(false);
                        }

                    }


                }
            }
        }).timeout(100).start(getApplicationContext());
    }

    private void storeProcess(String process) {
        try {
            String app_name = (String) getPackageManager().getApplicationLabel(
                    getPackageManager().getApplicationInfo(process
                            , PackageManager.GET_META_DATA));
            if (app_name != null) {
                if (!app_name.equals(Constraint.SYSTEM_LUNCHER) && !app_name.equals(Constraint.DAISYY))
                    DBCaller.storeLogInDatabase(getApplicationContext(), Constraint.OPEN + app_name, "", "", Constraint.APPLICATION_LOGS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private void initWakeUpLock() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int flags = PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        mWakeLock = powerManager.newWakeLock(flags, "wake_up_tag");
    }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground();
    }

    private void initWifi() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    }

    private void defineSensor() {
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Sensor mSensor = sensorMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
        sensorMan.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }


    private void wakePhoneUp() {
        mWakeLock.acquire();
        mWakeLock.release();
    }

    private void setCounter() {
        bringApplicationTimer();
        setDeleteTimer();
        sendLogTimer();
    }

    private void sendLogTimer() {
        Timer logsSync = new Timer();
        logsSync.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Utils.getNetworkState(getApplicationContext())) {
                    SyncLogs syncLogs = SyncLogs.getLogsSyncing(getApplicationContext());
                    syncLogs.saveContactApi();
                }
            }
        }, Constraint.TWO_HOUR, Constraint.TWO_HOUR);

    }

    private void bringApplicationTimer() {

        Timer T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                if (count == 30) {
                    String value = appChecker.getForegroundApp(getApplicationContext());

                    if (!value.equals(getApplication().getPackageName())) {
                        //  checkNetwork();
                        if (!value.equals("com.google.android.packageinstaller")) {

                            bringApplicationToFront(getApplicationContext());
                        }
                    }
                    count = 0;

                }
                checkWifiState();

            }
        }, 1000, 1000);
    }

    private void setDeleteTimer() {
        Timer deletePhoto = new Timer();
        deletePhoto.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!Utils.isMyServiceRunning(DeletePhotoService.class, getApplicationContext())) {
                    startService(new Intent(getApplicationContext(), DeletePhotoService.class));

                }
            }
        }, Constraint.TEN_MINUTES, Constraint.TEN_MINUTES);

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


    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_DEBUG);
        registerReceiver(overlayReceiver, filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }


    private BroadcastReceiver overlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                showOverlayActivity(context);
                wakePhoneUp();
            } else if (action.equals(ACTION_DEBUG)) {
                showOverlayActivity(context);
            }
        }
    };


    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.e("wifi state", "changes");
            InternetResponse internetResponse = new InternetResponse();
            OverLayResponse overLayResponse = new OverLayResponse();
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLED: {
                    internetResponse.setAvailable(false);
                    EventBus.getDefault().post(internetResponse);
                    break;
                }
                case WifiManager.WIFI_STATE_DISABLED: {
                    internetResponse.setAvailable(true);
                    EventBus.getDefault().post(internetResponse);
                    break;
                }
            }

        }
    };

    private void showOverlayActivity(Context context) {
        Log.e("kali", "inhance");
        Intent intent = new Intent(context, MainActivity.class);
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
        Log.e("kali", "working");
        initService();
    }

    private void handleClick() {
        touchLayout = new LinearLayout(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        count = 0;
        return true;
    }

    private void setWindowManager() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        touchLayout.setLayoutParams(lp);
        touchLayout.setOnTouchListener(this);
        touchLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("kali", "checking;....");
                return false;
            }
        });
        touchLayout.setLongClickable(true);

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
        } else {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
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
        if (Constraint.IS_OVER_APP_SETTING)
            screenBrightness(Constraint.CREENTBRIGHNESS);
        long time1 = TimeUnit.SECONDS.toMillis(Constraint.ONE);
        Utils.constructJobForBackground(time1, getApplicationContext());
    }

    public void enableWifi() {
        wifiManager.setWifiEnabled(true);
    }


    private void screenBrightness(int level) {
        try {
            android.provider.Settings.System.putInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, level);
        } catch (Exception e) {
            e.printStackTrace();


        }
    }

    private void checkWifiState() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        InternetResponse internetResponse = new InternetResponse();
        OverLayResponse overLayResponse = new OverLayResponse();

        if (wifiManager.isWifiEnabled()) {
            internetResponse.setAvailable(false);
            EventBus.getDefault().post(internetResponse);
        } else {
            internetResponse.setAvailable(true);
            EventBus.getDefault().post(internetResponse);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            mGravity = event.values.clone();

            if (mGravity != null) {
                float z = mGravity[2];
                float x = mGravity[0];
                float y = mGravity[1];
                int z_digree = (int) Math.round(Math.toDegrees(Math.acos(z)));
                int y_digree = (int) Math.round(Math.toDegrees(Math.acos(y)));
                int x_digree = (int) Math.round(Math.toDegrees(Math.acos(x)));

                if (z_digree <= 92 && z_digree > 88) {
                    counter++;
                    if (counter > 10) {
                        isPickedUp = false;
                    }


                } else {
                    movement = 1;
                    counter = 0;
                    isPickedUp = true;
                }
                if (movement == 1) {
                    if (isPickedUp) {
                        if (!isPickedUpSucess) {
                            isPickedDown = false;
                            isPickedUpSucess = true;
                            DBCaller.storeLogInDatabase(getApplicationContext(), "Device picked up", "", "", Constraint.APPLICATION_LOGS);
                        }
                    } else {
                        if (!isPickedDown) {
                            isPickedDown = true;
                            isPickedUpSucess = false;
                            DBCaller.storeLogInDatabase(getApplicationContext(), "Device picked down", "", "", Constraint.APPLICATION_LOGS);

                        }
                    }
                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}