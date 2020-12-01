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
import android.widget.CompoundButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.BuildConfig;
import com.daisy.R;
import com.daisy.activity.apkUpdate.ApkUpdateViewModel;
import com.daisy.activity.apkUpdate.UpdateApk;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.baseUrl.BaseUrlSettings;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.feedBack.FeedBackActivity;
import com.daisy.activity.logs.LogsMainActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.refreshTimer.RefreshTimer;
import com.daisy.activity.updateBaseUrl.UpdateBaseUrl;
import com.daisy.activity.updatePosition.UpdatePosition;
import com.daisy.activity.updateProduct.UpdateProduct;
import com.daisy.activity.welcomeScreen.WelcomeScreen;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityConfigSettingsBinding;
import com.daisy.pojo.response.ApkDetails;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.service.BackgroundService;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.HashMap;
import java.util.Locale;

public class ConfigSettings extends BaseActivity implements View.OnClickListener {

    private ActivityConfigSettingsBinding mBinding;
    private Context context;
    private SessionManager sessionManager;
    private ApkUpdateViewModel viewModel;

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
        viewModel=new ViewModelProvider(this).get(ApkUpdateViewModel.class);
        setNoTitleBar(this);
        sessionWork();
        mBinding.appVersion.setText(BuildConfig.VERSION_NAME);
    }

    /**
     * Done all session work
     */
    private void sessionWork() {
        sessionManager = SessionManager.get();
        if (sessionManager.getDeviceSanitised().equals(Constraint.TRUE_STR)) {
            mBinding.sanitisedHeader.setVisibility(View.VISIBLE);
        } else {
            mBinding.sanitisedHeader.setVisibility(View.GONE);

        }
        if (sessionManager.getSanitized()) {
            mBinding.sanitisedMain.setChecked(Constraint.TRUE);
        } else {
            mBinding.sanitisedMain.setChecked(Constraint.FALSE);
        }
        if (!sessionManager.getDeviceSecured())
        {
            mBinding.securitySwitch.setChecked(Constraint.TRUE);
        }
        else
        {
            mBinding.securitySwitch.setChecked(Constraint.FALSE);

        }
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
        mBinding.directApkUpdate.setOnClickListener(this::onClick);
        mBinding.updateProduct.setOnClickListener(this::onClick);
        mBinding.sanitisedMain.setOnCheckedChangeListener(getCheckedListener());
        mBinding.securitySwitch.setOnCheckedChangeListener(getSecuritySwich());

    }


    private CompoundButton.OnCheckedChangeListener getSecuritySwich() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    sessionManager.setStepCount(0);
                    sessionManager.deviceSecuried(Constraint.FALSE);

                    finish();

                }
                else
                {
                    sessionManager.deviceSecuried(Constraint.TRUE);
                    finish();

                }
            }
        };
    }



    /**
     * Sanitised switch listener
     */
    private CompoundButton.OnCheckedChangeListener getCheckedListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sessionManager.setSanitized(Constraint.TRUE);
                    sessionManager.setComeFromConfig(Constraint.TRUE);
                    finish();
                    ValidationHelper.showToast(context, getString(R.string.sanitised));

                }


            }
        };
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
            case R.id.feedBack: {
                feedBack();
                break;
            }
            case R.id.lunchApp: {
                launchApp();
                break;
            }
            case R.id.update_product:
            {
                openUpdateProductActivity();
                break;
            }
//            case R.id.sanitisedHeader: {
//                sessionManager.setSanitized(true);
//                finish();
//                ValidationHelper.showToast(context, getString(R.string.sanitised));
//                break;
//            }
            case R.id.direct_apk_update: {
                handleApkUpdateDirectly();
            }
        }
    }

    private void openUpdateProductActivity() {
    Intent intent=new Intent(context, UpdateProduct.class);
    startActivity(intent);
    }

    /**
     *
     */
    private void handleApkUpdateDirectly() {
        showHideProgressDialog(true);
        viewModel.setRequest(new HashMap());
        LiveData<GlobalResponse<GeneralResponse>> liveData = viewModel.getResponseLiveData();
        liveData.observe(this, new Observer<GlobalResponse<GeneralResponse>>() {
            @Override
            public void onChanged(GlobalResponse<GeneralResponse> response) {
                showHideProgressDialog(false);
                if (response != null) {

                    GlobalResponse<GeneralResponse> globalResponse = response;
                    if (globalResponse.isApi_status()) {
                        ApkDetails apkDetails = globalResponse.getResult().getApkDetails();
                        if (apkDetails != null) {
                            if (apkDetails.getAndroid().getVersion() != null) {
                                if (sessionManager == null)
                                    sessionManager = SessionManager.get();
                                double apkVersion = Double.parseDouble(apkDetails.getAndroid().getVersion());
                                double ourVersion = Double.parseDouble(BuildConfig.VERSION_NAME);
                                if (apkVersion > ourVersion) {
                                    sessionManager.setVersionDetails(apkDetails);
                                    openMainActivity();
                                }
                                else
                                {
                                    ValidationHelper.showToast(context,getString(R.string.no_update_available));
                                }
                            }

                        }


                    }
                }
            }
        });
    }



    /**
     * Launch other app
     */
    private void launchApp () {
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
    private void feedBack () {
        Intent intent = new Intent(ConfigSettings.this, FeedBackActivity.class);
        startActivity(intent);
    }


    /**
     * Do logout
     */
    private void logout () {
        sessionManager.removeSession();

        Intent intent = new Intent(ConfigSettings.this, BaseUrlSettings.class);
        ProcessPhoenix.triggerRebirth(ConfigSettings.this, intent);

    }


    /**
     * Open refresh timer activity
     */
    private void openRefreshRate () {
        Intent intent = new Intent(ConfigSettings.this, RefreshTimer.class);
        startActivity(intent);
    }


    /**
     * Open UpdateBaseUrl activity
     */
    private void updateBaseUrl () {
        Intent intent = new Intent(ConfigSettings.this, UpdateBaseUrl.class);
        startActivity(intent);

    }


    /**
     * Open UpdatePosition activity
     */
    private void openUpdatePositionActivity () {
        Intent intent = new Intent(ConfigSettings.this, UpdatePosition.class);
        startActivity(intent);
    }


    /**
     * Open logs activity
     */
    private void openLogActivity () {
        Intent intent = new Intent(ConfigSettings.this, LogsMainActivity.class);
        startActivity(intent);
    }


    /**
     * Open main activity
     */
    private void openMainActivity () {
        Intent intent = new Intent(ConfigSettings.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Handle change language popup
     */
    private void changeLanguage () {
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
    private void setLang (String s){
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
