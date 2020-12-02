package com.daisy.service;

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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.validatePromotion.ValidatePromotion;
import com.daisy.checkCardAvailability.CheckCardAvailability;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.pojo.response.ApkDetails;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.pojo.response.Inversion;
import com.daisy.pojo.response.Promotions;
import com.daisy.pojo.response.Sanitised;
import com.daisy.pojo.response.Time;
import com.daisy.sync.SyncLogs;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;
import com.rvalerio.fgchecker.AppChecker;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.daisy.utils.Constraint.LOG;
import static com.daisy.utils.Constraint.messages;

public class BackgroundService extends Service implements View.OnTouchListener, SensorEventListener , FrontCameraRetriever.Listener, FaceDetectionCamera.Listener {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    private static final String ACTION_DEBUG = "daichan4649.lockoverlay.action.DEBUG";
    private String TAG = this.getClass().getSimpleName();
    private WindowManager mWindowManager;
    private LinearLayout touchLayout;
    private LinearLayout touchLayoutforCamera;

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

    //Variables used in calculations

    private long stepTimestamp = 0;
    private double distance = 0;
    private int uninstallIssue=0;


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
        securityIntent = new Intent(getApplicationContext(), SecurityService.class);
        FrontCameraRetriever.retrieveFor(this);

        securityService();
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


    /**
     * Start security service
     */
    private void securityService() {

    //    startService(securityIntent);
    }


    /**
     * show lock screen if browser or message app or play store or settings open
     */
    private void initPassword() {
        appChecker.whenAny(new AppChecker.Listener() {
            @Override
            public void onForeground(String process1) {
                try {
                    if (process1 != null) {
                        if (!sessionManager.getUninstallShow()) {
                            String process = process1 + "";
                            if (sessionManager == null) {
                                sessionManager = SessionManager.get();
                            }


                            if (!sessionManager.getUninstall()) {

                                if (process.equals(Constraint.PACKAGE_INSTALLER)) {

                                    if (uninstallIssue==0)
                                    {
                                    uninstallIssue++;
                                    }
                                    else {
                                        uninstallIssue=0;
                                        Intent intent = new Intent(getApplicationContext(), LockScreen.class);
                                        intent.putExtra(Constraint.PACKAGE, process);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra(Constraint.UNINSTALL, Constraint.YES);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);

                                    }

                                } else {
                                    uninstallIssue=0;
                                    sessionManager.setDefaultDownload(false);
                                }
                            }

                            boolean b = sessionManager.getLock();
                            if (!Constraint.current_running_process.equals(process)) {

                                storeProcess(process);
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
                                    // true
                                    if (!messageLock) {

                                        return;
                                    }
                                }


                                Constraint.current_running_process = process;

                                if (!process.equals(getApplication().getPackageName())) {
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

                                }


                            }
                        }
                    }
                } catch (Exception e) {
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
                    DBCaller.storeLogInDatabase(getApplicationContext(), getApplicationContext().getString(R.string.open) + app_name, "", "", Constraint.APPLICATION_LOGS);
            }
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


    private void wakePhoneUp() {
        mWakeLock.acquire();
        mWakeLock.release();
    }

    private void setCounter() {
        bringApplicationTimer();
        setDeleteTimer();
        sendLogTimer();
        checkUpdate();
        checkPromotion();
        checkInversion();
        //stopUninstall();
        updateAPk();
        validatePromotion();
    }

    private boolean checkSensorAvailability() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (sensor == null) {
            return false;
        }

        return true;
    }

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
                        ValidatePromotion validatePromotion = new ValidatePromotion();
                        validatePromotion.checkPromotion();
                    } catch (Exception e) {

                    }
                }
            }, second, second);

        } catch (Exception e) {
        }

    }

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

                        String mainVersion=sessionManager.getApkVersion();
                        if (mainVersion!=null && !mainVersion.equals(""))
                        {
                            Log.e("kali","mainVersion Not empty");
                            try
                            {
                                double olderVersion=Double.parseDouble(mainVersion);
                                double newVersion=Double.parseDouble(BuildConfig.VERSION_NAME);
                                if (newVersion>olderVersion)
                                {
                                    new UpdateAppVersion().UpdateAppVersion();
                                }
                            }
                            catch (Exception e)
                            {
                            }
                        }

                    } catch (Exception e) {

                    }
                }
            }, second, second);

        } catch (Exception e) {
        }
    }

    private void stopUninstall() {
        try {


            int second = Constraint.FIVE_HUNDRED;
            refreshTimer5 = new Timer();
            refreshTimer5.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        sendBroadcast(closeDialog);


                    } catch (Exception e) {

                    }
                }
            }, second, second);

        } catch (Exception e) {
        }
    }

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

    public static void checkUpdate() {
        if (sessionManager == null)
            sessionManager = SessionManager.get();
        Time time = sessionManager.getTimeData();
        int hour = Constraint.FIVE_INE;
//        int hour = Constraint.ZERO;

        int minit = Constraint.TWO;
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


    public static void updateAPk() {
        try {
            int hour = Constraint.FIVE_INE;
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

    private void bringApplicationTimer() {

        try {
            Timer T = new Timer();
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    count++;
                    if (count == Constraint.THIRTY_INT) {
                        try {
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

    private void setDeleteTimer() {
        int hour = Constraint.ZERO;
        int minit = Constraint.TEN;


        int second = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;

        Timer deletePhoto = new Timer();
        deletePhoto.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!Utils.isMyServiceRunning(DeletePhotoService.class, getApplicationContext())) {
                    startService(new Intent(getApplicationContext(), DeletePhotoService.class));

                }
            }
        }, second, second);

    }


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

    private void timeChanged() {

        CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
        checkCardAvailability.checkCard(Constraint.UPDATE_INVERSION);

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

    private void showOverlayActivity(Context context) {
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
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        initService();
    }

    private void handleClick() {
        touchLayout = new LinearLayout(this);
        touchLayoutforCamera = new LinearLayout(this);

    }

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
        return true;
    }

    private void setWindowManager() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        touchLayout.setLayoutParams(lp);
        touchLayout.setOnTouchListener(this);
        touchLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return Constraint.FALSE;
            }
        });
        WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams(0, 0);
        touchLayoutforCamera.setLayoutParams(lp1);
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
            params.x = Constraint.ZERO;
            params.y = Constraint.ZERO;
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
            params.x = Constraint.ZERO;
            params.y = Constraint.ZERO;
            mWindowManager.addView(touchLayout, params);

        }
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
            mWindowManager.addView(touchLayoutforCamera, params);
        } else {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT,
                    10,10,
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

                mWindowManager.addView(touchLayoutforCamera, params);

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



        }
    }

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

private double MagnitudePrevious;
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case (Sensor.TYPE_STEP_COUNTER):
                countSteps(event.values[0]);
               break;

            case Sensor.TYPE_GYROSCOPE:
                handleGyro(event);
                break;

        }


    }

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
                        DBCaller.storeLogInDatabase(getApplicationContext(), "Device picked up", "", "", Constraint.APPLICATION_LOGS);

                    }
                } else {
                    if (!isPickedDown) {
                        Inversion inversion = new Inversion();
                        inversion.setInvert(Utils.getInvertedTime());
                        EventBus.getDefault().post(inversion);
                        isPickedDown = true;
                        isPickedUpSucess = false;
                        DBCaller.storeLogInDatabase(getApplicationContext(), "Device put down", "", "", Constraint.APPLICATION_LOGS);

                    }
                }
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void countSteps(float step) {
        int stepCount = sessionManager.getSteps();
        //Step count
         if (!Utils.isPlugged(getApplicationContext())) {
             if (!sessionManager.getDeviceSecured()) {
            stepCount =stepCount+1;
            ValidationHelper.showToast(getApplicationContext(), stepCount + "");
            sessionManager.setStepCount(stepCount);
            //Distance calculation
            if (stepCount >= Constraint.FIFTEEN) {

                    startService(securityIntent);
                }

            }
        }
        //Record achievement
    }




    @Override
    public void onFaceDetected() {
        ValidationHelper.showToast(getApplicationContext(),"Face comes");


    }

    @Override
    public void onFaceTimedOut() {

        ValidationHelper.showToast(getApplicationContext(),"Face goes out");
        Log.e("kali","face time out");

    }

    @Override
    public void onFaceDetectionNonRecoverableError() {

    }
    FaceDetectionCamera camera;

    @Override
    public void onLoaded(FaceDetectionCamera camera) {
        try {

            // When the front facing camera has been retrieved we still need to ensure our display is ready
            // so we will let the camera surface view initialise the camera i.e turn face detection on
            SurfaceView cameraSurface = new CameraSurfaceView(this, camera, this);
            this.camera=  camera;
                    touchLayoutforCamera.addView(cameraSurface);

        } catch (Exception e) {

        }
    }

    @Override
    public void onFailedToLoadFaceDetectionCamera() {

    }
}