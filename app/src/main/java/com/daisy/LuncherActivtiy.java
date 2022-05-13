package com.daisy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;

import com.daisy.daisyGo.utils.Constraint;
import com.daisy.daisyGo.utils.ValidationHelper;
import com.daisy.optimalPermission.activity.baseUrl.BaseUrlSettings;
import com.daisy.optimalPermission.activity.splash.SplashScreen;

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
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent = new Intent(this, BaseUrlSettings.class);

        } else {
            try {
                Map value = getCPUInfo();
                String hardware = (String) value.get(Constraint.HARDWARE);
                if (hardware.toLowerCase().contains(Constraint.UNISOC)) {
                    ValidationHelper.showToast(this, "Unisoc device");
                    intent = new Intent(this, BaseUrlSettings.class);

                } else if (CommonUtil.isSystemAlertWindowEnabled(this)) {
                    intent = new Intent(this, com.daisy.daisyGo.activity.baseUrl.BaseUrlSettings.class);
                } else {
                    try {
                        final UserManager um = (UserManager) getSystemService(Context.USER_SERVICE);
                        if (um.hasUserRestriction("WORK_PROFILE_RESTRICTION")) {
                            ValidationHelper.showToast(this, "MDM Device");
                        } else {
                            ValidationHelper.showToast(this, "Not an MDM Device");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent = new Intent(this, com.daisy.mainDaisy.activity.baseUrl.BaseUrlSettings.class);

                }
            } catch (Exception e) {
                intent = new Intent(this, com.daisy.mainDaisy.activity.baseUrl.BaseUrlSettings.class);

            }
        }
        if (intent != null)
            startActivity(intent);


    }


    public static Map<String, String> getCPUInfo() throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"));

        String str;

        Map<String, String> output = new HashMap<>();

        while ((str = br.readLine()) != null) {

            String[] data = str.split(":");

            if (data.length > 1) {

                String key = data[0].trim().replace(" ", "_");
                if (key.equals("model_name")) key = "cpu_model";

                output.put(key, data[1].trim());

            }

        }

        br.close();

        return output;

    }
}