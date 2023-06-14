package com.allyy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.allyy.R;
import com.allyy.activity.splash.SplashScreen;
import com.allyy.utils.Constraint;

/**
 * Purpose - WifiTimeCorrectionActivity opens when system reboot and wait for 1 minute to launch the splash activity
 */
public class WifiTimeCorrectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_time_correction);
        openAppAfterFewSeconds();
    }

    /**
     * Purpose - openAppAfterFewSeconds method delay code for 1 minute and then open splash screen
     */
    private void openAppAfterFewSeconds() {

        final Handler handler = new android.os.Handler();
        handler.postDelayed(() -> {
            Intent intent2 = new Intent(getApplicationContext(), SplashScreen.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent2);
            finish();
        }, Constraint.ONE_MINUTE);

    }
}