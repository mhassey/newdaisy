package com.iris.activity.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.iris.R;
import com.iris.activity.onBoarding.slider.OnBoarding;
import com.iris.app.AppController;
import com.iris.broadcast.broadcastforbackgroundservice.AlaramHelperBackground;
import com.iris.common.session.SessionManager;
import com.iris.security.Admin;
import com.iris.service.BackgroundService;
import com.iris.utils.ValidationHelper;

import java.util.Locale;

/**
 * Purpose -  BaseActivity is an activity that have some common method and function that need to or nice to call from every activity
 * Responsibility - All the basic code that will used in every activity is written in BaseActivity
 **/
public class BaseActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ProgressDialog progressDialog;


    /**
     * Responsibility - Its an predefine function  that calls when activity created here we just pass current running activity object to AppController and call initView function that help to
     * initiate variables
     * Parameters - Bundle savedInstanceState its pass from os
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppController.getInstance().setContext(getApplicationContext());
        initView();

    }


    /**
     * Responsibility - handleLogout is an method that help to logout the app with stop all services
     * Parameters - No parameter
     **/
    public void handleLogout() {
        if (BackgroundService.getServiceObject() != null) {
            AlaramHelperBackground.cancelAlarmElapsed();
            AlaramHelperBackground.disableBootReceiver(getApplicationContext());
            AlaramHelperBackground.cancelAlarmRTC();
            BackgroundService.getServiceObject().closeService();
            getApplicationContext().stopService(new Intent(getApplicationContext(), BackgroundService.class));
            SessionManager.get().clear();
            SessionManager.get().logout(true);
            Intent intent = new Intent(getApplicationContext(), OnBoarding.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            ValidationHelper.showToast(getApplicationContext(), getString(R.string.please_wait_service_is_not_register_yet));
        }
    }

    /**
     * Responsibility - removeAdminRightPermission is an method that help to remove admin right permission
     * Parameters - No parameter
     **/
    public void removeAdminRightPermission() {
        try {
            ComponentName devAdminReceiver = new ComponentName(this, Admin.class);
            DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mDPM.removeActiveAdmin(devAdminReceiver);
        } catch (Exception e) {

        }
    }


    /**
     * Responsibility - initView helps app to always keep screen on and initialize work for all activity
     * Parameters - No parameter
     **/
    private void initView() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sessionManager = SessionManager.get();

        boolean b = sessionManager.getDarkTheme();
        if (b)
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_editor_tool);
    }


    /**
     * Responsibility -  Define no title bar for all activity
     * Parameters - Activity object to know from which activity we want to remove title bar
     **/
    public void setNoTitleBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }


    /**
     * Responsibility -  setLang method need to set language of the app if customized from app
     * Parameters - its takes langName string value that contains selected language key
     **/
    private void setLang(String langName) {
        Locale locale = new Locale(langName);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        sessionManager.setLang(langName);
    }


    /**
     * Responsibility -  showHideProgressDialog method help to show progress bar from all activity when app needed
     * Parameters - its takes iShow boolean value that define progress should display or hide
     **/
    public void showHideProgressDialog(boolean iShow) {
        try {
            if (progressDialog != null) {
                if (iShow)
                    progressDialog.show();
                else {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }
            } else {

                progressDialog = new ProgressDialog(this, R.style.ProgressTheme);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                progressDialog.setCancelable(false);
                showHideProgressDialog(iShow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Responsibility -  When ever any activity got resumed then wer set our own lang
     * Parameters - No argument
     **/
    @Override
    protected void onResume() {
        AppController.setActivity(this);
        if (sessionManager.getLang() != null && !sessionManager.getLang().equals(""))
            setLang(sessionManager.getLang());
   //     Utils.setFullBrightNess();

        super.onResume();
    }
}
