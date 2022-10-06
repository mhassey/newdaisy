package com.daisy.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.daisy.activity.base.BaseActivity;
import com.daisy.common.session.SessionManager;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;

import java.util.Locale;

/**
 * AppController is an application class
 **/
public class AppController extends Application implements LifecycleObserver {
    public static final String SECURITY = "Security";
    public static AppController sInstance;
    public static final String TAG = AppController.class.getName();
    private SessionManager sessionManager;
    private static BaseActivity baseActivity;

    public static void setActivity(BaseActivity baseActivity1) {
        baseActivity = baseActivity1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instanceCreation();

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
    }



    /**
     * Get current brightness of phone
     */
    public static int getScreenBrightness(Context context) {
        int brightnessValue = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                0
        );
        brightnessValue=brightnessValue/2;
        return brightnessValue;
    }


    /**
     * get current running activity
     */
    public BaseActivity getActivity() {
        return baseActivity;
    }

    public void setContext(Context applicationContext) {
    }


}
