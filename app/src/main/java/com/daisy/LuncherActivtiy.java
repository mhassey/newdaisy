package com.daisy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;

import com.daisy.daisyGo.utils.Constraint;
import com.daisy.daisyGo.utils.ValidationHelper;
import com.daisy.mainDaisy.activity.splash.SplashScreen;
import com.daisy.optimalPermission.activity.baseUrl.BaseUrlSettings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LuncherActivtiy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher_activtiy);
        initView();


    }

    private void initView() {

        Map value = null;
        try {
            value = CommonUtil.getCPUInfo();

            String hardware = (String) value.get(Constraint.HARDWARE);
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent = new Intent(this, BaseUrlSettings.class);

            } else {
                try {

                    if (hardware.toLowerCase().contains(Constraint.UNISOC)) {

                        intent = new Intent(this, BaseUrlSettings.class);

                    } else if (CommonUtil.isSystemAlertWindowEnabled(this)) {
                        intent = new Intent(this, com.daisy.daisyGo.activity.baseUrl.BaseUrlSettings.class);
                    } else {
                        intent = new Intent(this, com.daisy.mainDaisy.activity.splash.SplashScreen.class);

                    }
                } catch (Exception e) {
                    intent = new Intent(this, SplashScreen.class);

                }
            }
            if (intent != null)
                startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}