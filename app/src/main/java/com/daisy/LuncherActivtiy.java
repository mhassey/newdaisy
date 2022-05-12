package com.daisy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.daisy.mainDaisy.activity.splash.SplashScreen;

public class LuncherActivtiy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher_activtiy);
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent = new Intent(this, SplashScreen.class);

        } else {
            intent = new Intent(this, com.daisy.daisyGo.activity.splash.SplashScreen.class);

        }
        startActivity(intent);


    }
}