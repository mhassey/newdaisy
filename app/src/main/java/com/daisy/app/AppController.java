package com.daisy.app;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.daisy.activity.base.BaseActivity;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;

public class AppController extends Application implements LifecycleObserver {
    public static AppController sInstance;
    public static final String TAG = AppController.class.getName();
    private SessionManager sessionManager;
    private BaseActivity baseActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sessionManager=SessionManager.get();
        DBCaller.storeLogInDatabase(this, Constraint.APPLICATION_START, Constraint.APPLICATION_DESCRIPTION, "", Constraint.APPLICATION_LOGS);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
       }



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
            Utils.screenBrightness(Constraint.CREENTBRIGHNESS,getApplicationContext());
        }








    private void setFullBrightNess() {
        if (Constraint.IS_OVER_APP_SETTING) {
            int max = Utils.getMaximumScreenBrightnessSetting();
            Utils.screenBrightness(max,getApplicationContext());
        }
    }



    public static int getScreenBrightness(Context context) {
        int brightnessValue = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                0
        );
        return brightnessValue;
    }



    public BaseActivity getActivity() {
        return baseActivity;
    }

}
