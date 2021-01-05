package com.daisy.activity.splash;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.welcomeScreen.WelcomeScreen;
import com.daisy.common.session.SessionManager;
import com.daisy.utils.Constraint;

import java.util.Locale;

public class SplashScreen extends BaseActivity {
    private SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initView();

    }



    /**
     * Initial data setup
     */
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
     * handle session work
     */
    private void handleSessionWork() {
        sessionManager=SessionManager.get();
        sessionManager.setUpdateNotShow(Constraint.FALSE);
        sessionManager.uninstallShow(Constraint.FALSE);

    }

    /**
     * Redirect to welcome or editor tool
     */
    private void redirectToWelcome() {
        if (sessionManager.getOnBoarding())
        {
            Intent intent = new Intent(SplashScreen.this, EditorTool.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(SplashScreen.this, WelcomeScreen.class);
            startActivity(intent);
        }
        finish();
    }

    /**
     * Change system ui to full screen when any change perform in activity
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }


    /**
     * Handle full screen mode
     */
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

    @Override
    public void onBackPressed() {

    }
}
