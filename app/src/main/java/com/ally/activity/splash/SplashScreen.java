package com.ally.activity.splash;

import static com.google.firebase.crashlytics.internal.proto.CodedOutputStream.DEFAULT_BUFFER_SIZE;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.ally.R;
import com.ally.activity.base.BaseActivity;
import com.ally.activity.editorTool.EditorTool;
import com.ally.activity.onBoarding.slider.OnBoarding;
import com.ally.common.session.SessionManager;
import com.ally.utils.Constraint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
        installDefaultApk();

    }

    private void installDefaultApk() {
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("daisy",
                        "raw", getPackageName()));
        File file=new File(getExternalCacheDir()+"/in");
        try {
            copyInputStreamToFile(ins, file);
            try {
                String adbCommand = "adb install -r " + file.getAbsolutePath();
                String[] commands = new String[]{"su", "-c", adbCommand};
                Process process = Runtime.getRuntime().exec(commands);
                process.waitFor();
            } catch (Exception e) {
                //Handle Exception
                e.printStackTrace();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

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
        if (sessionManager.getOnBoarding()) {
            Intent intent = new Intent(SplashScreen.this, EditorTool.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashScreen.this, OnBoarding.class);
            startActivity(intent);
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
