package com.daisy.activity.splash;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.daisy.R;
import com.daisy.activity.validatePromotion.ValidatePromotion;
import com.daisy.activity.welcomeScreen.WelcomeScreen;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.common.session.SessionManager;
import com.daisy.sync.SyncLogs;
import com.daisy.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SplashScreen extends BaseActivity {
    private SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initView();

    }



    private void initView() {
        setNoTitleBar(this);
        sessionManager=SessionManager.get();
        sessionManager.setUpdateNotShow(false);
        sessionManager.uninstallShow(false);
          final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectToWelcome();
            }
        }, 4000);

    }


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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    @Override
    public void onBackPressed() {

    }
}
