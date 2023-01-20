package com.android_tv.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android_tv.BuildConfig;
import com.android_tv.R;
import com.android_tv.activity.apkUpdate.UpdateApk;
import com.android_tv.activity.mainActivity.MainActivity;
import com.android_tv.activity.validatePromotion.ValidatePromotion;
import com.android_tv.checkCardAvailability.CheckCardAvailability;
import com.android_tv.common.session.SessionManager;
import com.android_tv.database.DBCaller;
import com.android_tv.interfaces.SyncLogCallBack;
import com.android_tv.pojo.RefreshLayout;
import com.android_tv.pojo.response.ApkDetails;
import com.android_tv.pojo.response.InternetResponse;
import com.android_tv.pojo.response.Inversion;
import com.android_tv.pojo.response.Promotions;
import com.android_tv.pojo.response.Sanitised;
import com.android_tv.pojo.response.Time;
import com.android_tv.sync.SyncLogs;
import com.android_tv.utils.Constraint;
import com.android_tv.utils.Utils;
import com.rvalerio.fgchecker.AppChecker;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * Main service that handle hole background tasks
 */
public class BackgroundService extends Service implements SyncLogCallBack, View.OnTouchListener, SensorEventListener {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    private static final String ACTION_DEBUG = "daichan4649.lockoverlay.action.DEBUG";
    private String TAG = this.getClass().getSimpleName();
    private WindowManager mWindowManager;
    private WindowManager mWindowManagerForCamera;

    private int count = 0;
    private PowerManager.WakeLock mWakeLock;
    private WifiManager wifiManager;
    private AppChecker appChecker = new AppChecker();
    private static SessionManager sessionManager;
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
    public static Timer refreshTimer;
    public static Timer refreshTimer1;
    public static Timer refreshTimer2;
    public static Timer refreshTimer3;
    public static Timer refreshTimer4;
    public static Timer refreshTimer5;
    private Intent securityIntent;
    private Sensor stepDetectorSensor;
    private Sensor stepCounterSensor;
    private Sensor magnetometer;
    private int uninstallIssue = 0;
    private static BackgroundService backgroundService;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sessionManager = SessionManager.get();
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        backgroundService = this;
        //securityIntent = new Intent(getApplicationContext(), SecurityService.class);
        //showNotification();
        initWakeUpLock();
        showNotification();
        // registerReceiver();
        // setWindowManager();
        setCounter();
//        initPassword();
        // initWifi();
         //   initPassword();

    }


    /**
     * show lock screen if browser or message app or play store or settings open
     */
    private void initPassword() {
        appChecker.whenAny(new AppChecker.Listener() {
            @Override
            public void onForeground(String process1) {
                try {
                    bringApplicationToFront(getApplicationContext());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).timeout(1000*60*60).start(getApplicationContext());
    }


    public static BackgroundService getServiceObject() {
        return backgroundService;
    }

    public void closeService() {
        unregisterReceiver();

    }


    @SuppressLint("InvalidWakeLockTag")
    private void initWakeUpLock() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int flags = PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        mWakeLock = powerManager.newWakeLock(flags, Constraint.WEAK_UP_TAG);
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


    // set all main counters
    private void setCounter() {
        //setDeleteTimer();
        // sendLogTimer();
        checkUpdate();
        checkPromotion();
        checkInversion();
        updateAPk();
        validatePromotion();
        refreshLayout();
    }

    // for every defined time app will fire ValidatePromotion checkPromotion method for checking our all promotion are valid or not
    private void validatePromotion() {
        try {
            int hour = Constraint.ONE;
            int minit = Constraint.ZERO;
            int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;
            refreshTimer4 = new Timer();
            refreshTimer4.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        EventBus.getDefault().post(new RefreshLayout());
                        ValidatePromotion validatePromotion = new ValidatePromotion();
                        validatePromotion.checkPromotion();
                    } catch (Exception e) {

                    }
                }
            }, second, second);

        } catch (Exception e) {
        }

    }


    // for every defined time app will fire ValidatePromotion checkPromotion method for checking our all promotion are valid or not
    private void refreshLayout() {
        try {
            int hour = Constraint.ZERO;
            int minit = 30;
            int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;
            refreshTimer4 = new Timer();
            refreshTimer4.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                        EventBus.getDefault().post(new RefreshLayout());
                }
            }, second, second);

        } catch (Exception e) {
        }

    }

    // its checks inversion in every three minutes
    private void checkInversion() {
        try {
            int hour = Constraint.ZERO;
            int minit = Constraint.THREE;
            int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;
            refreshTimer1 = new Timer();
            refreshTimer1.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Inversion inversion = new Inversion();
                        inversion.setInvert(Utils.getInvertedTime());
                        EventBus.getDefault().post(inversion);

                        String mainVersion = sessionManager.getApkVersion();

                        if (mainVersion != null && !mainVersion.equals("")) {


                            try {
                                double olderVersion = Double.parseDouble(mainVersion);
                                double newVersion = Double.parseDouble(BuildConfig.VERSION_NAME);
                                if (newVersion > olderVersion) {
                                    new UpdateAppVersion().UpdateAppVersion();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, second, second);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //  Check for promotion
    private void checkPromotion() {
        try {
            int hour = Constraint.ONE;
            int minit = Constraint.ZERO;

            int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;
            refreshTimer3 = new Timer();
            refreshTimer3.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new Promotions());
                }
            }, second, second);

        } catch (Exception e) {

        }
    }

    //  Send logs
    private void sendLogTimer() {
        Timer logsSync = new Timer();
        int second = ((10 * Constraint.THIRTY_SIX_HUNDRED) + (0 * Constraint.SIXTY)) * Constraint.THOUSAND;

        logsSync.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


                if (Utils.getNetworkState(getApplicationContext())) {
                    SyncLogs syncLogs = SyncLogs.getLogsSyncing(getApplicationContext());
                    syncLogs.saveContactApi(Constraint.APPLICATION_LOGS, BackgroundService.this::syncDone);


                }
            }
            //  }, Constraint.TWO_HOUR, Constraint.TWO_HOUR);
        }, second, second);

    }

    //  Sync Done start other one
    @Override
    public void syncDone(String val, int index) {
        try {
            List<Integer> integers = DBCaller.getPromotionCountByID(getApplicationContext());

            if (val.equals(Constraint.APPLICATION_LOGS)) {
                if (integers != null) {
                    if (integers.size() > 0) {
                        SyncLogs syncLogsPromotion = SyncLogs.getLogsSyncing(getApplicationContext());
                        syncLogsPromotion.saveContactApi(Constraint.PROMOTION, integers.get(0));

                    }
                }

            } else if (val.equals(Constraint.PROMOTION)) {
                if (integers.size() > 0) {
                    SyncLogs syncLogsPromotion = SyncLogs.getLogsSyncing(getApplicationContext());
                    syncLogsPromotion.saveContactApi(Constraint.PROMOTION, integers.get(0));
                }
            }
        } catch (Exception e) {

        }
    }

    //  Check for card update availability
    public static void checkUpdate() {
        if (sessionManager == null)
            sessionManager = SessionManager.get();
        Time time = sessionManager.getTimeData();
        int hour = Constraint.FIVE_INE_REAL;
//        int hour = 0;

        int minit = Constraint.ONE;


        if (time != null) {
            hour = time.getHour();
            minit = time.getMinit();
        }

        int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
                checkCardAvailability.checkCard();

            }
        }, second, second);


    }


    //  Check for apk update availability
    public static void updateAPk() {
        try {
            //int hour = Constraint.FIVE_INE_REAL;
            int hour = Constraint.FOUR;

            int minit = Constraint.ONE;


            int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;
            refreshTimer2 = new Timer();
            refreshTimer2.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    UpdateApk updateApk = new UpdateApk();
                    updateApk.UpdateApk();
                }
            }, second, second);

        } catch (Exception e) {

        }
    }

    //  Open app in front
    private void bringApplicationTimer() {

        try {
            Timer T = new Timer();
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    count++;
                    if (count == Constraint.THIRTY_INT) {
                        try {
                            if (Utils.isPlugged(getApplicationContext())) {
                                sessionManager.setStepCount(0);
                            }
                            String value = appChecker.getForegroundApp(getApplicationContext());
                            if (value != null) {
                                if (!value.equals(getApplication().getPackageName())) {
                                    if (!value.equals(Constraint.PACKAGE_INSTALLER)) {

                                        bringApplicationToFront(getApplicationContext());
                                    }
                                } else {
                                    ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
                                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                                    ComponentName componentInfo = taskInfo.get(0).topActivity;
                                    String name = componentInfo.getClassName();
                                    if (name.contains(Constraint.LOCK_SCREEN)) {
                                        bringApplicationToFront(getApplicationContext());

                                    }
                                }
                                count = Constraint.ZERO;

                            }
                        } catch (Exception e) {

                        }
                    }
                    checkWifiState();

                }
            }, Constraint.THOUSAND, Constraint.THOUSAND);
        } catch (Exception e) {

        }
    }

    //  Set up delete timer
    private void setDeleteTimer() {
        int hour = Constraint.ZERO;
        int minit = Constraint.TEN;


        int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;

        Timer deletePhoto = new Timer();
        deletePhoto.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!Utils.isMyServiceRunning(DeletePhotoService.class, getApplicationContext())) {
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {


                    } else {
                        startService(new Intent(getApplicationContext(), DeletePhotoService.class));
                    }

                }
            }
        }, second, second);

    }


    //  Open your Main to front
    private void bringApplicationToFront(final Context context) {
        try {
            // Get a handler that can be used to post to the main thread
            android.os.Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.e("Kali....","----------");

                    context.startActivity(intent);
                } // This is your code
            };
            mainHandler.post(myRunnable);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // Register receiver
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_DEBUG);
        registerReceiver(overlayReceiver, filter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
        IntentFilter s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(m_timeChangedReceiver, s_intentFilter);


    }

    private void unregisterReceiver() {
        if (overlayReceiver.isInitialStickyBroadcast())
            unregisterReceiver(overlayReceiver);
        if (wifiStateReceiver.isInitialStickyBroadcast())

            unregisterReceiver(wifiStateReceiver);
        if (m_timeChangedReceiver.isInitialStickyBroadcast())

            unregisterReceiver(m_timeChangedReceiver);
    }


    // Time change receiver
    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

                timeChanged();
            }
        }
    };

    // Perform check card when time changed
    private void timeChanged() {

        CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
        checkCardAvailability.checkCard(Constraint.UPDATE_INVERSION);

    }


    // Screen off  receiver

    private BroadcastReceiver overlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                //showOverlayActivity(context);
                // wakePhoneUp();
            } else if (action.equals(ACTION_DEBUG)) {
                // showOverlayActivity(context);
            }
        }
    };


    // Wifi state change receiver

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            InternetResponse internetResponse = new InternetResponse();
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = getPackageName();

        String channelName = Constraint.BACKGROUND_SERVICE;
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(Constraint.APP_IS_RUNNING_IN_BACKGROUND)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(Constraint.SERVICE_RUNNING_IN_BACKGROUND)
                .setContentIntent(pendingIntent)
                .build());
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        initService();
    }


    //  Perform sanitised work
    private void sanitisedWork() {
        try {
            ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            String name = componentInfo.getClassName();
            if (name.contains(Constraint.MAIN_ACTIVITY)) {
                EventBus.getDefault().post(new Sanitised());
            }
        } catch (Exception e) {
        }

    }


    //  Handle touch event on phone
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        count = Constraint.ZERO;
        Inversion inversion = new Inversion();
        inversion.setInvert(Utils.getInvertedTime());
        EventBus.getDefault().post(inversion);
        sanitisedWork();
        boolean value = sessionManager.getUpdateNotShow();
        boolean isDialogOpen = sessionManager.getupdateDialog();
        if (!isDialogOpen) {
            if (!value) {
                ApkDetails apkDetails = sessionManager.getApkDetails();
                if (apkDetails != null) {
                    EventBus.getDefault().post(apkDetails);
                }
            }
        }
        return false;
    }


    private void initService() {
        if (Constraint.IS_OVER_APP_SETTING)
            screenBrightness(Constraint.CREENTBRIGHNESS);
        long time1 = TimeUnit.SECONDS.toMillis(Constraint.ONE);
        Utils.constructJobForBackground(time1, getApplicationContext());
    }


    // Set brightness
    private void screenBrightness(int level) {
        try {
            android.provider.Settings.System.putInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, level);
        } catch (Exception e) {


        }
    }

    //  Check wifi state
    private void checkWifiState() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            InternetResponse internetResponse = new InternetResponse();
            try {
                ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                String name = componentInfo.getClassName();
                if (name.contains(Constraint.MAIN_ACTIVITY)) {
                    if (wifiManager.isWifiEnabled()) {
                        internetResponse.setAvailable(false);
                        EventBus.getDefault().post(internetResponse);
                    } else {
                        internetResponse.setAvailable(true);
                        EventBus.getDefault().post(internetResponse);
                    }
                }
            } catch (Exception e) {
            }

        } catch (Exception e) {

        }
    }

    //  Sensor change event
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case (Sensor.TYPE_STEP_COUNTER):
                break;

            case Sensor.TYPE_GYROSCOPE:
                handleGyro(event);
                break;


        }


    }

    //  Handle gyro
    private void handleGyro(SensorEvent event) {
        mGravity = event.values.clone();

        if (mGravity != null) {
            float z = mGravity[2];
            float x = mGravity[0];
            float y = mGravity[1];
            int z_digree = (int) Math.round(Math.toDegrees(Math.acos(z)));
            if (z_digree == 90) {
                counter++;
                if (counter > 20) {
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

                    }
                } else {
                    if (!isPickedDown) {
                        Inversion inversion = new Inversion();
                        inversion.setInvert(Utils.getInvertedTime());
                        EventBus.getDefault().post(inversion);
                        isPickedDown = true;
                        isPickedUpSucess = false;

                    }
                }
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //  Check count and start security service
    private void countSteps(float step) {
        int stepCount = sessionManager.getSteps();

        //Step count
        if (!Utils.isPlugged(getApplicationContext())) {
            if (sessionManager.getDeviceSecured()) {
                stepCount = stepCount + 1;
                sessionManager.setStepCount(stepCount);
                //Distance calculation
                if (stepCount >= Constraint.FIFTEEN) {

                    startService(securityIntent);
                }

            }
        } else {
            sessionManager.setStepCount(0);
        }
        //Record achievement
    }


}