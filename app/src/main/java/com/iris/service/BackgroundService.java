package com.iris.service;

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
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.iris.BuildConfig;
import com.iris.R;
import com.iris.activity.apkUpdate.UpdateApk;
import com.iris.activity.logs.LogSyncExtra;
import com.iris.activity.mainActivity.MainActivity;
import com.iris.activity.validatePromotion.ValidatePromotion;
import com.iris.checkCardAvailability.CheckCardAvailability;
import com.iris.common.session.SessionManager;
import com.iris.database.DBCaller;
import com.iris.interfaces.SyncLogCallBack;
import com.iris.pojo.Logs;
import com.iris.pojo.response.InternetResponse;
import com.iris.pojo.response.Inversion;
import com.iris.pojo.response.Promotions;
import com.iris.pojo.response.Time;
import com.iris.sync.SyncLogs;
import com.iris.utils.Constraint;
import com.iris.utils.Utils;
import com.rvalerio.fgchecker.AppChecker;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * Main service that handle hole background tasks
 */
public class BackgroundService extends Service implements SyncLogCallBack, SensorEventListener {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    private static final String ACTION_DEBUG = "daichan4649.lockoverlay.action.DEBUG";
    private String TAG = this.getClass().getSimpleName();
    public int count = 0;
    private AppChecker appChecker = new AppChecker();
    private static SessionManager sessionManager;
    private SensorManager sensorMan;
    private Sensor accelerometer;
    public static Timer refreshTimer;
    public static Timer refreshTimer1;
    public static Timer refreshTimer2;
    public static Timer refreshTimer3;
    public static Timer refreshTimer4;
    private Intent securityIntent;
    private PowerManager.WakeLock mWakeLock;

    private Sensor stepDetectorSensor;
    private Sensor stepCounterSensor;
    private Sensor magnetometer;
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
        securityIntent = new Intent(getApplicationContext(), SecurityService.class);
        showNotification();
        registerReceiver();
        setCounter();
        initWakeUpLock();
        if (!SessionManager.get().getAppType().equals(Constraint.GO)) {
            defineSensor();
        }

    }




    public static BackgroundService getServiceObject() {
        return backgroundService;
    }

    public void closeService() {
        unregisterReceiver();
        if (appChecker != null) {
            appChecker.stop();
        }
        stopSelf();
    }

    @SuppressLint("InvalidWakeLockTag")
    private void initWakeUpLock() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int flags = PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        mWakeLock = powerManager.newWakeLock(flags, Constraint.WEAK_UP_TAG);
    }

    void unregisterReceiver() {
        try {
            unregisterReceiver(wifiStateReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(m_timeChangedReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(overlayReceiver);
        } catch (Exception e) {
        }
    }


    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground();
    }




    /**
     * Purpose -  Define all sensors
     */
    private void defineSensor() {
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetectorSensor = sensorMan.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        stepCounterSensor = sensorMan.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        magnetometer = sensorMan.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor mSensor = sensorMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorMan.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        sensorMan.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMan.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMan.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    /**
     * Purpose - set all counters
     */
    private void setCounter() {
        bringApplicationTimer();
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.Q) {
            setDeleteTimer();
        }
        sendLogTimer();
        checkUpdate();
        checkPromotion();
        checkInversion();
        updateAPk();
        validatePromotion();
    }

    /**
     * Purpose - for every defined time app will fire ValidatePromotion checkPromotion method for checking our all promotion are valid or not
     */
    private void validatePromotion() {
        try {
            int hour = Constraint.ONE;
            int minit = Constraint.THIRTY_INT;

            int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;
            refreshTimer4 = new Timer();
            refreshTimer4.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (!SessionManager.get().getLogout()) {
                            ValidatePromotion validatePromotion = new ValidatePromotion();
                            validatePromotion.checkPromotion();
                        }
                    } catch (Exception e) {

                    }
                }
            }, second, second);

        } catch (Exception e) {
        }

    }

    /**
     * Purpose - its checks inversion in every three minutes
     */
    private void checkInversion() {
        try {
            int minit = Constraint.THREE;
            int second = ((minit * Constraint.SIXTY)) * Constraint.THOUSAND;
            refreshTimer1 = new Timer();
            refreshTimer1.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (!SessionManager.get().getLogout()) {
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

                                }


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

    /**
     * Purpose - Check for promotion in every one hour
     */
    private void checkPromotion() {
        try {
            int hour = Constraint.ONE;
            int second = ((hour * Constraint.THIRTY_SIX_HUNDRED)) * Constraint.THOUSAND;
            refreshTimer3 = new Timer();
            refreshTimer3.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!SessionManager.get().getLogout()) {
                        EventBus.getDefault().post(new Promotions());
                    }
                }
            }, second, second);

        } catch (Exception e) {

        }
    }


    /**
     * Purpose - Send logs to server in every six hours
     */
    private void sendLogTimer() {
        Timer logsSync = new Timer();
        int second = ((6 * Constraint.THIRTY_SIX_HUNDRED)) * Constraint.THOUSAND;
        logsSync.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!SessionManager.get().getLogout()) {
                    if (Utils.getNetworkState(getApplicationContext())) {
                        List<Logs> logsVOList = DBCaller.getLogsFromDatabaseNotSync(getApplicationContext());
                        if (logsVOList.isEmpty()) {
                            new LogSyncExtra(getApplicationContext(), false).fireLogExtra();

                        } else {
                            SyncLogs syncLogs = SyncLogs.getLogsSyncing(getApplicationContext());
                            syncLogs.saveContactApi(Constraint.APPLICATION_LOGS, BackgroundService.this::syncDone);
                        }

                    }
                }
            }
        }, second, second);

    }


    /**
     * Purpose - Sync Done start other syncing
     *
     * @param val
     * @param index
     */
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


    /**
     * Purpose - Check for card WIFI_STATE_CHANGED_ACTION availability
     */
    public static void checkUpdate() {
        if (sessionManager == null)
            sessionManager = SessionManager.get();
        Time time = sessionManager.getTimeData();
        int hour = Constraint.FOUR;
        int minit = Constraint.ZERO;
        if (time != null) {
            hour = time.getHour();
            minit = time.getMinit();
        }

        int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!SessionManager.get().getLogout()) {
                    CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
                    checkCardAvailability.checkCard();
                }
            }
        }, second, second);


    }


    /**
     * Purpose - Check for apk update availability
     */
    public static void updateAPk() {
        try {
            int hour = Constraint.FOUR;

            int minit = Constraint.ONE;


            int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;
            refreshTimer2 = new Timer();
            refreshTimer2.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!SessionManager.get().getLogout()) {
                        UpdateApk updateApk = new UpdateApk();
                        updateApk.UpdateApk();
                    }
                }
            }, second, second);

        } catch (Exception e) {

        }
    }

    /**
     * Purpose - Open app in front to user
     */
    private void bringApplicationTimer() {

        try {
            Timer T = new Timer();
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    if (!SessionManager.get().getLogout()) {
                        String value1 = appChecker.getForegroundApp(getApplicationContext());
                        if (value1 != null) {
                            if (!value1.equals(getApplication().getPackageName())) {
                                count++;
                                if (count == Constraint.ONE_TWENTY) {
                                    try {
                                        if (Utils.isPlugged(getApplicationContext())) {
                                            sessionManager.setStepCount(0);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                                checkWifiState();
                            } else {
                                count = 0;
                            }
                        }
                    }

                }
            }, Constraint.THOUSAND, Constraint.THOUSAND);
        } catch (Exception e) {

        }
    }

    /**
     * Purpose - setDeleteTimer method delete data from storage based on os level
     */
    private void setDeleteTimer() {

        int minit = Constraint.TEN;
        int second = ((minit * Constraint.SIXTY)) * Constraint.THOUSAND;
        Timer deletePhoto = new Timer();
        deletePhoto.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!SessionManager.get().getLogout()) {
                    if (!Utils.isMyServiceRunning(DeletePhotoService.class, getApplicationContext())) {
                        startService(new Intent(getApplicationContext(), DeletePhotoService.class));
                    }
                }
            }
        }, second, second);

    }



    /**
     * Purpose - registerReceiver method register all broad cast receiver
     */
    private void registerReceiver() {
        if (SessionManager.get().getAppType().equals(Constraint.GO)) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            registerReceiver(wifiStateReceiver, intentFilter);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_DEBUG);

        registerReceiver(overlayReceiver, filter);
        IntentFilter s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(m_timeChangedReceiver, s_intentFilter);


    }


    /**
     * Purpose - m_timeChangedReceiver receiver handles time change action
     */
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

    /**
     * Purpose - timeChanged method checks any card or promotion is available or not
     */
    private void timeChanged() {

        CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
        checkCardAvailability.checkCard(Constraint.UPDATE_INVERSION);

    }



    /**
     * Purpose - wifiStateReceiver method handles wifi state change
     */
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



    /**
     * Purpose - startMyOwnForeground method handles notification channels
     */
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


    /**
     * Purpose - startForeground method create notification that helps to open main activity
     */
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



    private void initService() {
        long time1 = TimeUnit.SECONDS.toMillis(Constraint.ONE);
        Utils.constructJobForBackground(time1, getApplicationContext());
    }



    /**
     * Purpose - checkWifiState method checks the wifi state and handle the ui accordingly
     */
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


    /**
     * Purpose - onSensorChanged method handles the sensor event
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            switch (event.sensor.getType()) {
                case (Sensor.TYPE_STEP_COUNTER):
                    countSteps();
                    break;

            }
        } catch (Exception e) {

        }


    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * Purpose - countSteps method  helps to start interaction service
     *
     * @param
     */
    private void countSteps() {
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
    }
    /**
     * Purpose - overlayReceiver method handles overlay listener
     */
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


    /**
     * Purpose - Use to wake up phone
     */
    private void wakePhoneUp() {
        mWakeLock.acquire();
        mWakeLock.release();
    }


    /**
     * showOverlayActivity method open main activity
     *
     * @param context
     */
    private void showOverlayActivity(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}