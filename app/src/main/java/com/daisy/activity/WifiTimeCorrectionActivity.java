package com.daisy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.daisy.R;
import com.daisy.activity.splash.SplashScreen;
import com.daisy.utils.Constraint;

public class WifiTimeCorrectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_time_correction);
        openAppAfterFewSeconds();
    }

    private void openAppAfterFewSeconds() {

        final Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Intent intent2 = new Intent(getApplicationContext(), SplashScreen.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent2);
                finish();
            }
        }, Constraint.ONE_MINUTE);

    }
}