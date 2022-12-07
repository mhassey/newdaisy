package com.daisy.service;

import static com.daisy.utils.Constraint.messages;

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
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.daisy.BuildConfig;
import com.daisy.ObjectDetection.CameraSurfaceView;
import com.daisy.ObjectDetection.cam.FaceDetectionCamera;
import com.daisy.ObjectDetection.cam.FrontCameraRetriever;
import com.daisy.R;
import com.daisy.activity.apkUpdate.UpdateApk;
import com.daisy.activity.lockscreen.LockScreen;
import com.daisy.activity.logs.LogSyncExtra;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.splash.SplashScreen;
import com.daisy.activity.validatePromotion.ValidatePromotion;
import com.daisy.checkCardAvailability.CheckCardAvailability;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.interfaces.SyncLogCallBack;
import com.daisy.pojo.Logs;
import com.daisy.pojo.response.Interactor;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.pojo.response.Inversion;
import com.daisy.pojo.response.Promotions;
import com.daisy.pojo.response.Sanitised;
import com.daisy.sync.SyncLogs;
import com.daisy.utils.CameraLoad;
import com.daisy.utils.Constraint;
import com.daisy.utils.TimeWork;
import com.daisy.utils.Utils;
import com.rvalerio.fgchecker.AppChecker;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * Main service that handle hole background tasks
 */
public class BackgroundService extends Service implements SyncLogCallBack, View.OnTouchListener, SensorEventListener, FrontCameraRetriever.Listener, FaceDetectionCamera.Listener {
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    private static final String ACTION_DEBUG = "daichan4649.lockoverlay.action.DEBUG";
    private String TAG = this.getClass().getSimpleName();
    private WindowManager mWindowManager;
    private WindowManager mWindowManagerForCamera;
    private LinearLayout touchLayoutforCamera;
    private LinearLayout touchLayout;

    public int count = 0;
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
    private long lastFaceDetect = 0;
    private Intent securityIntent;
    private Sensor stepDetectorSensor;
    private Sensor stepCounterSensor;
    private Sensor magnetometer;
    private int uninstallIssue = 0;
    private long mLastClickTime = 0;
    private static BackgroundService backgroundService;
    private TimeWork timeWork = TimeWork.getTimer();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sessionManager = SessionManager.get();
        setCounter();

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        timeWork.setUpdateTiming();
        backgroundService = this;
        securityIntent = new Intent(getApplicationContext(), SecurityService.class);
        showNotification();
        initWakeUpLock();
        handleClick();

        if (SessionManager.get().getDisplayOverTheAppAvailable())
            setWindowManager();

        registerReceiver();
        if (!SessionManager.get().getDisableSecurity())
            initPassword();

        if (!SessionManager.get().getAppType().equals(Constraint.GO)) {
            initWifi();
            defineSensor();
            loadCamera();
        }

    }

  public   void loadCamera()
    {
        try {

            FrontCameraRetriever.retrieveFor(this);
            FrontCameraRetriever.getInstance().load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * show lock screen if browser or message app or play store or settings open
     */
    private void initPassword() {
        appChecker.whenAny(new AppChecker.Listener() {
            @Override
            public void onForeground(String process1) {
                try {
                    if (sessionManager==null)
                        sessionManager=SessionManager.get();
                    if (process1 != null) {
                        if (!sessionManager.getUninstallShow()) {
                            String process = process1 + "";
                            if (sessionManager == null) {
                                sessionManager = SessionManager.get();
                            }

                            if (process.equals(Constraint.Extra_pass_screen)) {
                                return;
                            }


                            boolean b = sessionManager.getLock();
                            if (!Constraint.current_running_process.equals(process)) {
                                Constraint.current_running_process = process;
                                if (!sessionManager.getUninstall()) {

                                    if (process.equals(Constraint.PACKAGE_INSTALLER)) {
                                        if (!process.equals(getApplication().getPackageName())) {
                                            Intent intent = new Intent(getApplicationContext(), LockScreen.class);
                                            intent.putExtra(Constraint.PACKAGE, process);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra(Constraint.UNINSTALL, Constraint.YES);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);

                                        }
                                    } else {
                                        uninstallIssue = Constraint.ZERO;
                                        sessionManager.setDefaultDownload(Constraint.FALSE);
                                    }
                                }

                                if (process.equals(Constraint.PLAY_STORE_PATH) || process.contains(Constraint.SUMSUNG_BROWSER_NAME)) {
                                    if (!b) {

                                        return;
                                    }
                                }
                                boolean browserLock = sessionManager.getBrowserLock();
                                if (process.equals(Constraint.CROME) || process.contains(Constraint.SUMSUNG_BROWSER_NAME)) {
                                    if (!browserLock) {

                                        return;
                                    }
                                }
                                boolean messageLock = sessionManager.getMessageLock();
                                if (Arrays.asList(messages).contains(process) || process.contains(Constraint.MESSENGING)) {
                                    if (!messageLock) {

                                        return;
                                    }
                                }


                                if (!process.equals(getApplication().getPackageName())) {
                                    storeProcess(process);
                                    if (process.equals(Constraint.SETTING_PATH) || process.contains(Constraint.SUMSUNG_BROWSER_NAME) || process.equals(Constraint.PLAY_STORE_PATH) || process.equals(Constraint.CROME) || Arrays.asList(Constraint.messages).contains(process) || process.contains(Constraint.MMS) || process.contains(Constraint.MESSENGING)) {
                                        if (!sessionManager.getPasswordCorrect()) {
                                            sessionManager.setPasswordCorrect(Constraint.TRUE);
                                            Intent intent = new Intent(getApplicationContext(), LockScreen.class);
                                            intent.putExtra(Constraint.PACKAGE, process);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                        } else {
                                            sessionManager.setPasswordCorrect(Constraint.FALSE);
                                        }
                                    } else {
                                        sessionManager.setPasswordCorrect(Constraint.FALSE);
                                    }

                                } else {
                                    sessionManager.setPasswordCorrect(Constraint.FALSE);

                                }


                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).timeout(200).start(getApplicationContext());
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
        }
    }

    public static BackgroundService getServiceObject() {
        return backgroundService;
    }

    public void closeService() {
        unregisterReceiver();
        if (touchLayoutforCamera != null)
            mWindowManager.removeView(touchLayoutforCamera);
        if (touchLayout != null)
            mWindowManager.removeView(touchLayout);
        if (appChecker != null) {
            appChecker.stop();
        }
        stopSelf();
    }

    void unregisterReceiver() {
        try {
            unregisterReceiver(overlayReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(wifiStateReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(m_timeChangedReceiver);
        } catch (Exception e) {
        }
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
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
        sensorMan.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        sensorMan.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMan.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMan.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }


    /**
     * Purpose - Use to wake up phone
     */
    private void wakePhoneUp() {
        mWakeLock.acquire();
        mWakeLock.release();
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
            if (timeWork == null)
                timeWork = TimeWork.getTimer();
            timeWork.validatePromotionTimer.cancel();
            timeWork.validatePromotionTimer = new Timer();
            timeWork.validatePromotionTimer.scheduleAtFixedRate(new TimerTask() {
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
            }, timeWork.validatePromotionTiming, timeWork.validatePromotionTiming);

        } catch (Exception e) {
        }

    }

    /**
     * Purpose - its checks inversion in every three minutes
     */
    private void checkInversion() {
        try {
            if (timeWork == null)
                timeWork = TimeWork.getTimer();
            timeWork.inversionTimer.cancel();

            timeWork.inversionTimer = new Timer();
            timeWork.inversionTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        try {
                            if (!CameraLoad.getInstance().isLoaded)
                                BackgroundService.getServiceObject().loadCamera();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
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
            }, timeWork.checkInversionTiming, timeWork.checkInversionTiming);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Purpose - Check for promotion in every one hour
     */
    private void checkPromotion() {
        try {
            if (timeWork == null)
                timeWork = TimeWork.getTimer();
            timeWork.promotionTimer.cancel();

            timeWork.promotionTimer = new Timer();
            timeWork.promotionTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!SessionManager.get().getLogout()) {
                        EventBus.getDefault().post(new Promotions());
                    }
                }
            }, timeWork.checkPromotionTiming, timeWork.checkPromotionTiming);

        } catch (Exception e) {

        }
    }


    /**
     * Purpose - Send logs to server in every six hours
     */
    private void sendLogTimer() {
        if (timeWork == null)
            timeWork = TimeWork.getTimer();

        timeWork.logTimer.cancel();
        timeWork.logTimer = new Timer();
        int second = timeWork.logTiming;

        timeWork.logTimer.scheduleAtFixedRate(new TimerTask() {
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
        TimeWork timeWork = TimeWork.getTimer();
        timeWork.updateTimer.cancel();
        timeWork.updateTimer = new Timer();
        timeWork.updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!SessionManager.get().getLogout()) {
                    CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
                    checkCardAvailability.checkCard();
                }
            }
        }, timeWork.checkUpdateTiming, timeWork.checkUpdateTiming);


    }


    /**
     * Purpose - Check for apk update availability
     */
    public static void updateAPk() {
        try {
            TimeWork timeWork = TimeWork.getTimer();
            timeWork.updateApkTimer.cancel();
            timeWork.updateApkTimer = new Timer();
            timeWork.updateApkTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!SessionManager.get().getLogout()) {
                        UpdateApk updateApk = new UpdateApk();
                        updateApk.UpdateApk();
                    }
                }
            }, timeWork.updateApkTiming, timeWork.updateApkTiming);

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
        if (timeWork == null)
            timeWork = TimeWork.getTimer();
        timeWork.deleteTimer.cancel();
        int second = timeWork.deleteTiming;
        timeWork.deleteTimer = new Timer();
        timeWork.deleteTimer.scheduleAtFixedRate(new TimerTask() {
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
     * Purpose -  Open your Activity to front
     *
     * @param context
     */
    private void bringApplicationToFront(final Context context) {
        try {
            // Get a handler that can be used to post to the main thread
            android.os.Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } // This is your code
            };
            mainHandler.post(myRunnable);

        } catch (Exception e) {
        }

    }

    /**
     * Purpose - registerReceiver method register all broad cast receiver
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_DEBUG);
        registerReceiver(overlayReceiver, filter);
        if (SessionManager.get().getAppType().equals(Constraint.GO)) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            registerReceiver(wifiStateReceiver, intentFilter);
        }
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

    /**
     * Purpose - handleClick method initialize clicks listener
     */
    private void handleClick() {
        touchLayoutforCamera = new LinearLayout(this);
        touchLayout = new LinearLayout(this);


    }


    /**
     * Purpose - sanitisedWork check the top activity and according to that perform sanitization work
     */
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


    /**
     * Purpose - setWindowManager method initialize invisible ui that helps for face detect and interactions
     */
    private void setWindowManager() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams(0, 0);
        touchLayoutforCamera.setLayoutParams(lp1);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManagerForCamera = (WindowManager) getSystemService(WINDOW_SERVICE);
        touchLayout.setLayoutParams(lp);
        touchLayout.setOnTouchListener(this);
        touchLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return Constraint.FALSE;
            }
        });
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
            params.x = Constraint.ZERO;
            params.y = Constraint.ZERO;
            mWindowManager.addView(touchLayout, params);
        } else {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);


            params.gravity = Gravity.START | Gravity.TOP;
            params.x = Constraint.ZERO;
            params.y = Constraint.ZERO;
            mWindowManager.addView(touchLayout, params);

        }
        // set layout parameter of window manager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    0,
                    0,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.START | Gravity.TOP;
            params.x = Constraint.ZERO;
            params.y = Constraint.ZERO;
            mWindowManagerForCamera.addView(touchLayoutforCamera, params);
        } else {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    //      WindowManager.LayoutParams.WRAP_CONTENT,
                    //    WindowManager.LayoutParams.WRAP_CONTENT,
                    10, 10,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);


            params.gravity = Gravity.START | Gravity.TOP;
            params.x = Constraint.ZERO;
            params.y = Constraint.ZERO;

            mWindowManagerForCamera.addView(touchLayoutforCamera, params);

        }
    }


    private void initService() {
        if (Constraint.IS_OVER_APP_SETTING)
            screenBrightness(Constraint.CREENTBRIGHNESS);
        long time1 = TimeUnit.SECONDS.toMillis(Constraint.ONE);
        Utils.constructJobForBackground(time1, getApplicationContext());
    }

    /**
     * Purpose - screenBrightness method handles brightness level
     *
     * @param level
     */
    private void screenBrightness(int level) {
        try {
            android.provider.Settings.System.putInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, level);
        } catch (Exception e) {


        }
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
                    countSteps(event.values[0]);
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    handleGyro(event);
                    break;


            }
        } catch (Exception e) {

        }


    }

    //  Handle gyro
    private void handleGyro(SensorEvent event) {

        mGravity = event.values.clone();


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * Purpose - countSteps method  helps to start interaction service
     *
     * @param step
     */
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


    /**
     * Purpose - onFaceDetected method handles face detection event
     */
    @Override
    public void onFaceDetected() {
        try {

            if (SystemClock.elapsedRealtime() - lastFaceDetect < Constraint.FIVE_SECOND) {
                return;
            }
            lastFaceDetect = SystemClock.elapsedRealtime();
            ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            String name = componentInfo.getClassName();
            if (name.contains(Constraint.MAIN_ACTIVITY)) {
                EventBus.getDefault().post(new Interactor());

                DBCaller.storeFaceDetectionLogInDatabase(getApplicationContext(), Constraint.USER_SEEN_PRICECARD__, "", "", Constraint.APPLICATION_LOGS);

            }
        } catch (Exception e) {
        }


    }


    //  Face out handler
    @Override
    public void onFaceTimedOut() {


    }

    @Override
    public void onFaceDetectionNonRecoverableError() {

    }

    FaceDetectionCamera camera;

    /**
     * Purpose - onLoaded method load camera on invisible screen
     *
     * @param camera
     */
    @Override
    public void onLoaded(FaceDetectionCamera camera) {
        try {

            // When the front facing camera has been retrieved we still need to ensure our display is ready
            // so we will let the camera surface view initialise the camera i.e turn face detection on
            if (touchLayoutforCamera != null) {
                try {
                    SurfaceView cameraSurface = new CameraSurfaceView(this, camera, this);
                    this.camera = camera;
                    touchLayoutforCamera.addView(cameraSurface);
                    CameraLoad.getInstance().isLoaded = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFailedToLoadFaceDetectionCamera() {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        count = Constraint.ZERO;
        return true;

    }
}