package com.daisy.activity.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.daisy.R;
import com.daisy.app.AppController;
import com.daisy.common.session.SessionManager;

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
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setCancelable(false);
                showHideProgressDialog(iShow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        if (sessionManager.getLang() != null && !sessionManager.getLang().equals(""))
            setLang(sessionManager.getLang());
        super.onResume();
    }
}
