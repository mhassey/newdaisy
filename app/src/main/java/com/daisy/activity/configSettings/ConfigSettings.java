package com.daisy.activity.configSettings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.daisy.BuildConfig;
import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.baseUrl.BaseUrlSettings;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.feedBack.FeedBackActivity;
import com.daisy.activity.logs.LogsMainActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.refreshTimer.RefreshTimer;
import com.daisy.activity.updateBaseUrl.UpdateBaseUrl;
import com.daisy.activity.updatePosition.UpdatePosition;
import com.daisy.activity.welcomeScreen.WelcomeScreen;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityConfigSettingsBinding;
import com.daisy.service.BackgroundService;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.Locale;

public class ConfigSettings extends BaseActivity implements View.OnClickListener {

    private ActivityConfigSettingsBinding mBinding;
    private Context context;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_config_settings);
        initView();
        initClick();
    }

    /**
     * Initial data setup
     */
    private void initView() {
        context = this;
        setNoTitleBar(this);
        sessionManager = SessionManager.get();
        mBinding.appVersion.setText(BuildConfig.VERSION_NAME);
    }


    /**
     * Button clicks initializing
     */
    private void initClick() {
        mBinding.logs.setOnClickListener(this);
        mBinding.setRefreshRate.setOnClickListener(this);
        mBinding.updateBaseUrl.setOnClickListener(this);
        mBinding.updatePosition.setOnClickListener(this);
        mBinding.changeLanguage.setOnClickListener(this::onClick);
        mBinding.logout.setOnClickListener(this::onClick);
        mBinding.cancel.setOnClickListener(this::onClick);
        mBinding.feedBack.setOnClickListener(this::onClick);
        mBinding.lunchApp.setOnClickListener(this::onClick);
        mBinding.sanitisedHeader.setOnClickListener(this::onClick);
    }


    /**
     * Change system ui to full screen when any change perform in activity
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }


    /**
     * Handle full screen mode
     */
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ViewGroup.LayoutParams params = mBinding.rootLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mBinding.rootLayout.requestLayout();

    }



    /**
     * Handle Clicks listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logs: {
                openLogActivity();
                break;
            }
            case R.id.setRefreshRate: {
                openRefreshRate();
                break;
            }
            case R.id.updateBaseUrl: {
                updateBaseUrl();
                break;
            }
            case R.id.updatePosition: {
                openUpdatePositionActivity();
                break;
            }
            case R.id.changeLanguage: {
                changeLanguage();
                break;
            }
            case R.id.cancel: {
                onBackPressed();
                break;
            }
            case R.id.logout: {
                logout();
                break;
            }
            case R.id.feedBack:
            {
                feedBack();
                break;
            }
            case R.id.lunchApp:
            {
             launchApp();
                break;
            }
            case R.id.sanitisedHeader: {
                sessionManager.setSanitized(true);
                 break;
            }
        }
    }


    /**
     * Launch other app
     */
    private void launchApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            ValidationHelper.showToast(ConfigSettings.this, "There is no package available in android");
        }
    }


    /**
     * Go to feedback page
     */
    private void feedBack() {
        Intent intent = new Intent(ConfigSettings.this, FeedBackActivity.class);
        startActivity(intent);
    }


    /**
     * Do logout
     */
    private void logout() {
        sessionManager.removeSession();

        Intent intent = new Intent(ConfigSettings.this, BaseUrlSettings.class);
        ProcessPhoenix.triggerRebirth(ConfigSettings.this,intent);

    }


    /**
     * Open refresh timer activity
     */
    private void openRefreshRate() {
        Intent intent = new Intent(ConfigSettings.this, RefreshTimer.class);
        startActivity(intent);
    }



    /**
     * Open UpdateBaseUrl activity
     */
    private void updateBaseUrl() {
        Intent intent = new Intent(ConfigSettings.this, UpdateBaseUrl.class);
        startActivity(intent);

    }


    /**
     * Open UpdatePosition activity
     */
    private void openUpdatePositionActivity() {
        Intent intent = new Intent(ConfigSettings.this, UpdatePosition.class);
        startActivity(intent);
    }


    /**
     * Open logs activity
     */
    private void openLogActivity() {
        Intent intent = new Intent(ConfigSettings.this, LogsMainActivity.class);
        startActivity(intent);
    }


    /**
     * Handle change language popup
     */
    private void changeLanguage() {
        String[] lang = {getString(R.string.english), getString(R.string.french), getString(R.string.spanish), getString(R.string.postigues)};
        String loadedLang = sessionManager.getLang();
        int pos = 0;
        if (loadedLang != null && !loadedLang.equals("")) {
            if (loadedLang.equals(Constraint.EN)) {
                pos = Constraint.ZERO;
            } else if (loadedLang.equals(Constraint.FR)) {
                pos = Constraint.ONE;
            } else if (loadedLang.equals(Constraint.ES)) {
                pos = Constraint.TWO;
            } else if (loadedLang.equals(Constraint.PT)) {
                pos = Constraint.THREE;
            }
        }

        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.choose_lang))
                .setSingleChoiceItems(lang, pos, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selectedPosition == Constraint.ZERO) {
                            setLang(Constraint.EN);
                        } else if (selectedPosition == Constraint.ONE) {
                            setLang(Constraint.FR);
                        } else if (selectedPosition == Constraint.TWO) {
                            setLang(Constraint.ES);
                        } else if (selectedPosition == Constraint.THREE) {
                            setLang(Constraint.PT);
                        }
                        dialog.dismiss();
                    }
                })
                .show();


    }


    /**
     * Set language
     */
    private void setLang(String s) {
        Locale locale = new Locale(s);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        sessionManager.setLang(s);
        Intent i = new Intent(ConfigSettings.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}
