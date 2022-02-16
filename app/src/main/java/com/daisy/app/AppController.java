package com.daisy.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.configSettings.ConfigSettings;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;

import java.util.Locale;

/**
 * AppController is an application class
 **/
public class AppController extends Application implements LifecycleObserver {
    public static AppController sInstance;
    public static final String TAG = AppController.class.getName();
    private SessionManager sessionManager;
    private BaseActivity baseActivity;

    @Override
    public void onCreate() {

        instanceCreation();
        DBCaller.storeLogInDatabase(this, Constraint.APPLICATION_START, Constraint.APPLICATION_DESCRIPTION, "", Constraint.APPLICATION_LOGS);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        setLang();
    }


    /**
     * Create instance
     */
    private void instanceCreation() {
        sInstance = this;
        sessionManager = SessionManager.get();
    }


    /**
     * Get App Controller instance
     */
    public static AppController getInstance() {
        if (sInstance == null) {
            synchronized (AppController.class) {
                if (sInstance == null)
                    sInstance = new AppController();
            }
        }
        // Return the instance
        return sInstance;
    }


    /**
     * When app come in fore ground
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onAppForeground() {

        sessionManager = SessionManager.get();
        sessionManager.setInForground(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Constraint.IS_OVER_APP_SETTING = Utils.hasWriteSettingsPermission(this);
        }
        Constraint.CREENTBRIGHNESS = getScreenBrightness(this);
        setFullBrightNess();


    }

    /**
     * Set language
     */
    private void setLang() {
        if (!sessionManager.getLang().equals("")) {
            Log.e("working", sessionManager.getLang());
            Locale locale = new Locale(sessionManager.getLang());
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
//            sessionManager.setLang(s);

        } else {
//            Locale locale = new Locale(s);
//            Locale.setDefault(locale);
//            Configuration configuration = new Configuration();
//            configuration.locale = locale;
//            getActivity().getResources().updateConfiguration(configuration, getActivity().getResources().getDisplayMetrics());
        }

    }


    /**
     * When application level on stop call
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        sessionManager = SessionManager.get();
        sessionManager.setInForground(false);
        if (Constraint.IS_OVER_APP_SETTING)
            Utils.screenBrightness(Constraint.CREENTBRIGHNESS, getApplicationContext());
    }


    /**
     * Set full brightness of phone
     */
    private void setFullBrightNess() {
        if (Constraint.IS_OVER_APP_SETTING) {
            int max = Utils.getMaximumScreenBrightnessSetting();
            Utils.screenBrightness(max, getApplicationContext());
        }
    }


    /**
     * Get current brightness of phone
     */
    public static int getScreenBrightness(Context context) {
        int brightnessValue = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                0
        );
        return brightnessValue;
    }


    /**
     * get current running activity
     */
    public BaseActivity getActivity() {
        return baseActivity;
    }

    public void setBaseActivity(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public void setContext(Context applicationContext) {
    }
}
