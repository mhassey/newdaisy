package com.daisy.activity.welcomeScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.onBoarding.slider.OnBoarding;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityWelcomeScreenBinding;
import com.daisy.dialogFragment.DateTimePermissionDIalog;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;

import java.util.Locale;

/**
 * Purpose -  WelcomeScreen is an activity that show some content as welcome page to user
 * Responsibility - Its show some useful content for user and its also has begin method that redirect screen to on boarding
 **/
public class WelcomeScreen extends BaseActivity implements View.OnClickListener {

    private ActivityWelcomeScreenBinding mBinding;
    private SessionManager sessionManager;
    final int sdk = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome_screen);
        initView();
        initClick();
        registerWifiReceiver();
    }

    private void registerWifiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }


    /**
     * Purpose - wifiStateReceiver method handles wifi state change
     */
    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            InternetResponse internetResponse = new InternetResponse();
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLED:
                case WifiManager.WIFI_STATE_DISABLED: {
                    handleResumePermission();
                    break;
                }
            }

        }
    };

    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        sessionManager = SessionManager.get();
        setNoTitleBar(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        handleResumePermission();
    }

    private void handleResumePermission() {
        boolean permissionAvailable = Utils.isTimeAutomatic(this);
        boolean wifiPermission = Utils.checkWifiState(this);

        if (!permissionAvailable) {
            showAlertIfTimeIsNotCorrect();
        }
        if (!permissionAvailable || !wifiPermission) {
            mBinding.curveLayout.setVisibility(View.GONE);
        } else {
            mBinding.curveLayout.setVisibility(View.VISIBLE);

        }
    }


    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        mBinding.begin.setOnClickListener(this);
        mBinding.connectToWifi.setOnClickListener(this);
        mBinding.setTimeZone.setOnClickListener(this);
    }

    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begin: {
                goToOnBoarding();
                break;
            }
            case R.id.connect_to_wifi: {
                goToWifi();
                break;
            }
            case R.id.set_time_zone: {
                handleTimeZoneCorrection();
                break;
            }
        }
    }

    /**
     * Purpose - handleTimeZoneCorrection method handles the timezone redirection
     */
    private void handleTimeZoneCorrection() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
    }


    /**
     * Ge to wifi screen
     */
    private void goToWifi() {

        Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        if (Utils.getDeviceName().contains(getString(R.string.onePlus))) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        }
        this.startActivityForResult(intent, Constraint.NINE_THOUSANT_NINE_HUNDRED);

    }

    /**
     * Responsibility - goToOnBoarding method redirect screen to OnBaording activity
     * Parameters - No parameter
     **/
    private void goToOnBoarding() {
        Intent intent = new Intent(WelcomeScreen.this, OnBoarding.class);
        startActivity(intent);
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

    @Override
    protected void onStart() {
        if (Locale.getDefault().getLanguage().equals(Constraint.AR)) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mBinding.curveLayout.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_purple_rtl));
            } else {
                mBinding.curveLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_purple_rtl));
            }
        }
        super.onStart();

    }


    /**
     * show alert if timezone is not correct
     **/
    public void showAlertIfTimeIsNotCorrect() {
        DateTimePermissionDIalog dateTimePermissionDIalog = new DateTimePermissionDIalog();
        dateTimePermissionDIalog.setCancelable(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dateTimePermissionDIalog.show(ft, null);


    }

}
