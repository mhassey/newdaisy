package com.daisy;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.daisy.daisyGo.utils.Constraint;
import com.daisy.mainDaisy.activity.splash.SplashScreen;
import com.daisy.optimalPermission.activity.baseUrl.BaseUrlSettings;
import com.daisy.optimalPermission.session.SessionManager;

import java.util.List;
import java.util.Map;

public class LuncherActivtiy extends AppCompatActivity {
    Boolean isInstalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher_activtiy);
        initView();


    }

    private void initView() {

        getWorkProfile();
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
                        if (isInstalled)
                            SessionManager.get().disableSecurity(true);

                        intent = new Intent(this, BaseUrlSettings.class);

                    } else if (CommonUtil.isSystemAlertWindowEnabled(this)) {
                        if (isInstalled)
                            com.daisy.daisyGo.session.SessionManager.get().disableSecurity(true);
                        intent = new Intent(this, com.daisy.daisyGo.activity.baseUrl.BaseUrlSettings.class);
                    } else {
                        if (isInstalled)
                            com.daisy.mainDaisy.common.session.SessionManager.get().disableSecurity(true);

                        intent = new Intent(this, com.daisy.optimalPermission.activity.splash.SplashScreen.class);

                    }
                } catch (Exception e) {
                    if (isInstalled)
                        com.daisy.mainDaisy.common.session.SessionManager.get().disableSecurity(true);

                    intent = new Intent(this, SplashScreen.class);

                }
            }
            if (intent != null)
                startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getWorkProfile() {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.contains("hmdm")) {
                isInstalled = true;
            }
        }


    }


}