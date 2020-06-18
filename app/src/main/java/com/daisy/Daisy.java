package com.daisy;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.provider.Settings;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.daisy.broadcast.ScreenReceiver;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.notification.AlarmReceiver;
import com.daisy.utils.Utils;

import java.util.concurrent.TimeUnit;

public class Daisy extends Application implements LifecycleObserver {
    public static Daisy sInstance;
    public static final String TAG = Daisy.class.getName();
    private SessionManager sessionManager;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sessionManager=SessionManager.get();
        Utils.storeLogInDatabase(this, Constraint.APPLICATION_START, Constraint.APPLICATION_DESCRIPTION, "", Constraint.APPLICATION_LOGS);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        registerBroadCast();
       }

    private void registerBroadCast() {
        AlarmReceiver receiver=new AlarmReceiver();
        IntentFilter filter1 = new IntentFilter("android.intent.action.BOOT_COMPLETED");
        registerReceiver(receiver, filter1);
    }

    public static Daisy getInstance() {
        if (sInstance == null) {
            synchronized (Daisy.class) {
                if (sInstance == null)
                    sInstance = new Daisy();
            }
        }
        // Return the instance
        return sInstance;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onAppForground()
    {
        sessionManager=SessionManager.get();
        sessionManager.setInForground(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Constraint.IS_OVER_APP_SETTING = Utils.hasWriteSettingsPermission(this);
        }
        Constraint.CREENTBRIGHNESS=getScreenBrightness(this);
        setFullBrightNess();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        sessionManager=SessionManager.get();
        sessionManager.setInForground(false);
        if (Constraint.IS_OVER_APP_SETTING)
            screenBrightness(Constraint.CREENTBRIGHNESS);
        long time = TimeUnit.SECONDS.toMillis(Constraint.THIRTY);
        Utils.constructJob(time,getApplicationContext());
    }








    private void setFullBrightNess() {
        if (Constraint.IS_OVER_APP_SETTING) {
            int max = getMaximumScreenBrightnessSetting();
            screenBrightness(max);
        }
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

    public static int getScreenBrightness(Context context) {
        int brightnessValue = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                0
        );
        return brightnessValue;
    }


    public static int getMaximumScreenBrightnessSetting() {
        final Resources res = Resources.getSystem();
         int id = res.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");  // API17+
        if (id != 0) {
            try {
               id= res.getInteger(id);
                int val=((id*70)/100);

                return val;
            } catch (Resources.NotFoundException e) {
                // ignore
                e.printStackTrace();
            }
        }
        return 255;
    }

    private void setSleepBroadCast() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }
}
