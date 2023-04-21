package com.daisy.activity.splash;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.daisy.BuildConfig;
import com.daisy.R;
import com.daisy.activity.AutoOnboardingWithPermission;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.welcomeScreen.WelcomeScreen;
import com.daisy.common.session.SessionManager;
import com.daisy.utils.Constraint;
import com.daisy.utils.PermissionManager;
import com.daisy.utils.Utils;

/**
 * Purpose - SplashScreen is an activity that show splash data
 * Responsibility - Its use to hold screen to some second and show app logo
 **/
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
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        SessionManager.get().isDisplayOverTheAppAvailable(true);

        setNoTitleBar(this);
        if (getIntent() != null) {
            SessionManager.get().setImeiNumber(getIntent().getStringExtra("extra_imei"));
//            SessionManager.get().setBaseUrl(getIntent().getStringExtra("base_url"));
//            SessionManager.get().setStoreCode(getIntent().getStringExtra("store_code"));
        }
        SessionManager.get().setBaseUrl("https://tmobile.mobilepricecards.com");
        SessionManager.get().setStoreCode(getIntent().getStringExtra("1store"));
        wakeUp();
        setDefaultBrightness();
        handleSessionWork();


    }


    private void wakeUp() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            int flags = PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
            PowerManager.WakeLock mWakeLock = powerManager.newWakeLock(flags, Constraint.WEAK_UP_TAG);
            mWakeLock.acquire();
            mWakeLock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Responsibility - handleSessionWork method is used for perform some session work that will used in app
     * Parameters - No parameter
     **/
    private void handleSessionWork() {
        sessionManager = SessionManager.get();
        sessionManager.setUpdateNotShow(Constraint.FALSE);
        sessionManager.uninstallShow(Constraint.FALSE);
        if (sessionManager.getDefaultTiming() == 0)
            sessionManager.setDefaultTiming(System.currentTimeMillis());

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
            try {
                if (Utils.isSystemAlertWindowEnabled(this)) {
                    SessionManager.get().setAppType(Constraint.GO);

                } else {
                    SessionManager.get().setAppType(Constraint.MAIN);


                }
            } catch (Exception e) {
                SessionManager.get().setAppType(Constraint.MAIN);


            }

            if (sessionManager.getOnBoarding()) {

                handleStoragePermission();
            }
            else
            {
                if (SessionManager.get().getBaseUrl() != null && !SessionManager.get().getBaseUrl().equals("")) {
                    intent = new Intent(SplashScreen.this, AutoOnboardingWithPermission.class);
                } else {
                    intent = new Intent(SplashScreen.this, WelcomeScreen.class);

                }
                startActivity(intent);
            }






        } catch (Exception e) {
            e.printStackTrace();
        }


        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectToWelcome();
            }
        }, Constraint.FOUR_THOUSAND);

    }


    private void handleStoragePermission() {
        if (!Utils.isAllAccessPermissionGiven(this)) {
            try {
                SessionManager.get().setPasswordCorrect(true);
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception ex) {
                try {
                    Intent intent = new  Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch ( Exception ex1) {

                }
            }
        }
        else {
            handleNewPermissionIfNotGiven();

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > Constraint.ZERO) {
            if (grantResults[Constraint.ZERO] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = shouldShowRequestPermissionRationale(permissions[Constraint.ZERO]);
                if (!showRationale) {
                } else {
                    boolean b;

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                        // Do something for lollipop and above versions
                        PermissionManager.checkPermission(SplashScreen.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, Constraint.RESPONSE_CODE);
                    }

                }
            } else {
                if (grantResults[Constraint.ZERO] == PackageManager.PERMISSION_GRANTED) {
                    handleStoragePermission();

                }
            }
        }
        return;

    }

    private void handleNewPermissionIfNotGiven() {


            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
             if (PermissionManager.checkPermission(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, Constraint.PUSH_CODE))
             {
                 Intent   intent = new Intent(SplashScreen.this, EditorTool.class);
                 startActivity(intent);
             }

            }
            else {
                Intent   intent = new Intent(SplashScreen.this, EditorTool.class);
                startActivity(intent);
            }


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
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
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
