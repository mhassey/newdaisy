package com.iris.activity.splash;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.iris.R;
import com.iris.activity.base.BaseActivity;
import com.iris.activity.editorTool.EditorTool;
import com.iris.activity.onBoarding.slider.OnBoarding;
import com.iris.common.session.SessionManager;
import com.iris.utils.Constraint;
import com.iris.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

//import MongoConnectionByGridle.MongoConection;

//import MongoConnectionByGridle.MongoConection;


/**
 * Purpose - SplashScreen is an activity that show splash data
 * Responsibility - Its use to hold screen to some second and show app logo
 **/
public class SplashScreen extends BaseActivity {
    private SessionManager sessionManager;
    private boolean isInstalled;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initView();
    }


    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        setNoTitleBar(this);
        handleSessionWork();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectToWelcome();
            }
        }, Constraint.FOUR_THOUSAND);

    }

    /**
     * Responsibility - handleSessionWork method is used for perform some session work that will used in app
     * Parameters - No parameter
     **/
    private void handleSessionWork() {
        sessionManager = SessionManager.get();
        sessionManager.setUpdateNotShow(Constraint.FALSE);
        sessionManager.uninstallShow(Constraint.FALSE);

    }

    /**
     * Responsibility - redirectToWelcome method is used to direct screen to Editor tool if onBoard is done id onBoarding is not done then redirect to WelcomeScreen
     * Parameters - No parameter
     **/
    private void redirectToWelcome() {

        Intent intent = null;
        try {
            boolean isInstalled = Utils.getWorkProfile(this);
            SessionManager.get().setDisableSecurity(isInstalled);
            if (sessionManager.getOnBoarding()) {
                intent = new Intent(SplashScreen.this, EditorTool.class);

            } else {
                intent = new Intent(SplashScreen.this, OnBoarding.class);

            }


            try {
                if (Utils.isSystemAlertWindowEnabled(this)) {
                    SessionManager.get().setAppType(Constraint.GO);

                } else {
                    SessionManager.get().setAppType(Constraint.MAIN);


                }
            } catch (Exception e) {
                SessionManager.get().setAppType(Constraint.MAIN);


            }

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }


        finish();

    }

    /**
     * Responsibility - onWindowFocusChanged method is an override function that call when any changes perform on ui
     * Parameters - its take boolean hasFocus that help to know out app is in focused or not
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }


    /**
     * Responsibility - hideSystemUI method is an default method that help to change app ui to full screen when any change perform in activity
     * Parameters - No parameter
     **/

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    /**
     * Responsibility - onBackPressed method is an override method that we use for stop back from splash screen
     * Parameters - No parameter
     **/
    @Override
    public void onBackPressed() {

    }


}
