package com.daisy.activity.lockscreen;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.security.Admin;
import com.daisy.utils.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityLockScreenBinding;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.Locale;

/**
 * Purpose -  LockScreen is an activity that help to show when need to ask password in various conditions
 * Responsibility - Its ask for password when user open play store ,settings ,browser and when we are going to uninstall the app
 **/
public class LockScreen extends BaseActivity implements View.OnClickListener {
    private ActivityLockScreenBinding binding;
    private Context context;
    private String current_running_path;
    private SessionManager sessionManager;
    private boolean comeFromUninstall = false;
    final int sdk = android.os.Build.VERSION.SDK_INT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initClick();
    }

    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        context = this;
        setNoTitleBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen);
        sessionManager = SessionManager.get();
        extraTaskForMakeAppWorkable();
    }

    /**
     * Responsibility - extraTaskForMakeAppWorkable method do some extra stuff that help service to run perfectly
     * Parameters - No para            AddScreen addScreen = null;
meter
     **/
    private void extraTaskForMakeAppWorkable() {
        Constraint.current_running_process = "";
        current_running_path = String.valueOf(getIntent().getStringExtra(Constraint.PACKAGE));
        String uninstall = getIntent().getStringExtra(Constraint.UNINSTALL);
        if (uninstall != null && !uninstall.equals("")) {
            comeFromUninstall = false;
        } else {
            comeFromUninstall = true;
        }

    }

    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        binding.unlock.setOnClickListener(this);
        binding.cancel.setOnClickListener(this::onClick);
    }

    /**
     * Responsibility - onBackPressed method is an override method that we use for stop back from lock screen
     * Parameters - No parameter
     **/
    @Override
    public void onBackPressed() {

    }

    /**
     * Responsibility - onStop method is an override method that we use for finish lock screen
     * Parameters - No parameter
     **/
    @Override
    protected void onStop() {
        super.onStop();
        LockScreen.this.finish();
    }

    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unlock: {
                Utils.hideKeyboard(context);
                unlockPassword();
                break;
            }
            case R.id.cancel: {
                sessionManager.setPasswordCorrect(Constraint.FALSE);

                redirectToMain();
                break;
            }
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
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ViewGroup.LayoutParams params = binding.rootLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.rootLayout.requestLayout();

    }


    /**
     * Responsibility - redirectToMain method is an method that helps to open MainActivity class
     * Parameters - No parameter
     **/
    private void redirectToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Responsibility - unlockPassword method is used when we need to verify the fill password is correct or not if correct then open the last running page
     * Parameters - No parameter
     **/
    private void unlockPassword() {
        String password = binding.password.getText().toString();
        if (password != null) {
            String realPassword = sessionManager.getPasswordLock();
            if (password.equals(realPassword)) {
                if (!comeFromUninstall) {
                    try {
                        ComponentName devAdminReceiver = new ComponentName(this, Admin.class);
                        DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                        mDPM.removeActiveAdmin(devAdminReceiver);
                    } catch (Exception e) {

                    }
                    sessionManager.setUninstall(Constraint.TRUE);
                }
                finish();
                startLastActivity(current_running_path);

            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_password));

            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.enter_password));
        }
    }
    /**
     * Responsibility - startLastActivity method is used when we need to open the last running page its takes packageName
     * Parameters - Its takes packageName which help to redirect to last open app
     **/
    private void startLastActivity(String packageName) {
        if (comeFromUninstall) {
            sessionManager.setPasswordCorrect(true);
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        } else {

            onBackToHome();
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse(Constraint.DAISY_PACKAGE));
            startActivity(intent);
        }
    }

    /**
     * Responsibility - onBackToHome method is used when we need to go on home page
     * Parameters - No parameter
     **/
    public void onBackToHome() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onStart() {
        if (Locale.getDefault().getLanguage().equals(Constraint.AR)) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                binding.unlock.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_light_red_rtl));
            } else {
                binding.unlock.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_light_red_rtl));
            }
        }
        super.onStart();

    }

}
