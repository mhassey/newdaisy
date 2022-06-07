package com.daisy.mainDaisy.activity.configSettings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.BuildConfig;
import com.daisy.R;
import com.daisy.mainDaisy.activity.apkUpdate.ApkUpdateViewModel;
import com.daisy.mainDaisy.activity.base.BaseActivity;
import com.daisy.mainDaisy.activity.baseUrl.BaseUrlSettings;
import com.daisy.mainDaisy.activity.feedBack.FeedBackActivity;
import com.daisy.mainDaisy.activity.langSupport.LangSelectionActivity;
import com.daisy.mainDaisy.activity.logs.LogsMainActivity;
import com.daisy.mainDaisy.activity.mainActivity.MainActivity;
import com.daisy.mainDaisy.activity.refreshTimer.RefreshTimer;
import com.daisy.mainDaisy.activity.socketConnection.SocketConnection;
import com.daisy.mainDaisy.activity.updateBaseUrl.UpdateBaseUrl;
import com.daisy.mainDaisy.activity.updatePosition.UpdatePosition;
import com.daisy.mainDaisy.activity.updateProduct.UpdateProduct;
import com.daisy.mainDaisy.activity.welcomeScreen.WelcomeScreen;
import com.daisy.mainDaisy.broadcast.broadcastforbackgroundservice.AlaramHelperBackground;
import com.daisy.mainDaisy.common.session.SessionManager;
import com.daisy.databinding.ActivityConfigSettingsBinding;
import com.daisy.mainDaisy.pojo.response.ApkDetails;
import com.daisy.mainDaisy.pojo.response.GeneralResponse;
import com.daisy.mainDaisy.pojo.response.GlobalResponse;
import com.daisy.mainDaisy.service.BackgroundService;
import com.daisy.mainDaisy.utils.Constraint;
import com.daisy.mainDaisy.utils.LogoutDialog;
import com.daisy.mainDaisy.utils.Utils;
import com.daisy.mainDaisy.utils.ValidationHelper;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * Purpose -  ConfigSettings is an activity that contains all setting level works
 * Responsibility - Its show app version and last updated time
 * link for go to logs
 * link for go to UpdatePosition
 * link for go to SetRefreshTimer
 * Enable and disable sanitised feature
 * link for go to Change language
 * Handle apk update
 * Handle security feature
 * Handle alarm feature
 * Handle Update product
 **/
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
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        context = this;
        viewModel = new ViewModelProvider(this).get(ApkUpdateViewModel.class);
        setNoTitleBar(this);
        sessionWork();
        mBinding.appVersion.setText(" " + BuildConfig.VERSION_NAME);
        getDefaultUpdateTime();
    }

    /**
     * Responsibility - getDefaultUpdateTime method is used for print last apk update time
     * Parameters - No parameter
     **/
    private void getDefaultUpdateTime() {
        try {
            String val = Utils.getLastUpdateDate(ConfigSettings.this);
            mBinding.updatetime.setText(" " + val);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Responsibility -  sessionWork is an method that perform session work all items visibility maintained here
     * Parameters - No parameter
     **/
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

        if (sessionManager.getDeviceSecurity().equals(Constraint.TRUE_STR)) {
            mBinding.securityHeader.setVisibility(View.VISIBLE);
            mBinding.alarmHeader.setVisibility(View.VISIBLE);

        } else {
            mBinding.securityHeader.setVisibility(View.GONE);
            mBinding.alarmHeader.setVisibility(View.GONE);

            sessionManager.setDeviceSecurity(Constraint.FALSE_STR);
        }
        if (sessionManager.getDeviceSecured()) {
            mBinding.securitySwitch.setChecked(Constraint.TRUE);
        } else {
            mBinding.securitySwitch.setChecked(Constraint.FALSE);

        }
        if (!sessionManager.getAlaramSecurity()) {
            mBinding.alramSwitch.setChecked(Constraint.TRUE);
        } else {
            mBinding.alramSwitch.setChecked(Constraint.FALSE);

        }
    }


    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        mBinding.logs.setOnClickListener(this::onClick);
        mBinding.setRefreshRate.setOnClickListener(this::onClick);
        mBinding.updateBaseUrl.setOnClickListener(this::onClick);
        mBinding.updatePosition.setOnClickListener(this::onClick);
        mBinding.changeLanguage.setOnClickListener(this::onClick);
        mBinding.logout.setOnClickListener(this::onClick);
        mBinding.cancel.setOnClickListener(this::onClick);
        mBinding.feedBack.setOnClickListener(this::onClick);
        mBinding.lunchApp.setOnClickListener(this::onClick);
        mBinding.sanitisedHeader.setOnClickListener(this::onClick);
        mBinding.directApkUpdate.setOnClickListener(this::onClick);
        mBinding.updateProduct.setOnClickListener(this::onClick);
        mBinding.sanitisedMain.setOnCheckedChangeListener(getCheckedListener());
        mBinding.securitySwitch.setOnCheckedChangeListener(getSecuritySwitch());
        mBinding.alramSwitch.setOnCheckedChangeListener(getAlarmSwitch());
        mBinding.logoutApp.setOnClickListener(this::onClick);
        mBinding.socketConnection.setOnClickListener(this::onClick);


    }


    /**
     * Responsibility - getAlarmSwitch is an method that works when we switch alarm and here we store its state to session for future work
     * Parameters - No parameter
     **/
    private CompoundButton.OnCheckedChangeListener getAlarmSwitch() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sessionManager.alaramSecuried(Constraint.FALSE);
                } else {
                    sessionManager.alaramSecuried(Constraint.TRUE);
                }
            }
        };
    }


    /**
     * Responsibility - getSecuritySwitch is an method that works when we switch security switch and here we store its state to session for future work
     * Parameters - No parameter
     **/
    private CompoundButton.OnCheckedChangeListener getSecuritySwitch() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sessionManager.setStepCount(Constraint.ZERO);
                    sessionManager.deviceSecuried(Constraint.TRUE);
                    finish();
                } else {
                    sessionManager.deviceSecuried(Constraint.FALSE);
                    finish();

                }
            }
        };
    }


    /**
     * Responsibility - getCheckedListener is an method that works when  sanitised switch changes and here we store its state to session for future work
     * Parameters - No parameter
     **/
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
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logs: {
                openLogActivity();
                break;
            }
            case R.id.socket_connection: {
                handleSocketConnectionCall();
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
                startLangSupportActivity();
                //changeLanguage();
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
            case R.id.update_product: {
                openUpdateProductActivity();
                break;
            }

            case R.id.direct_apk_update: {
                handleApkUpdateDirectly();
                break;
            }
            case R.id.logout_app: {
                logoutAlert();
                break;
            }
        }
    }

    /**
     * Purpose - handleSocketConnectionCall method open socket connection page
     */
    private void handleSocketConnectionCall() {
        Intent intent = new Intent(this, SocketConnection.class);
        startActivity(intent);
    }


    /**
     * Purpose - logoutAlert method is used for open logout alert dialog
     */
    public void logoutAlert() {
        LogoutDialog dateTimePermissionDIalog = new LogoutDialog();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dateTimePermissionDIalog.show(ft, null);


    }


    /**
     * Responsibility - startLangSupportActivity is an method to redirect page to  LangSelectionActivity
     * Parameters - No parameter
     **/
    private void startLangSupportActivity() {
        Intent intent = new Intent(context, LangSelectionActivity.class);
        startActivity(intent);

    }

    /**
     * Responsibility - openUpdateProductActivity is an method to redirect page to  UpdateProduct
     * Parameters - No parameter
     **/
    private void openUpdateProductActivity() {
        Intent intent = new Intent(context, UpdateProduct.class);
        startActivity(intent);
    }


    /**
     * Responsibility - handleApkUpdateDirectly is an method that check any apk update is available or not
     * Parameters - No parameter
     **/
    private void handleApkUpdateDirectly() {
        showHideProgressDialog(true);
        viewModel.setRequest(new HashMap());
        LiveData<GlobalResponse<GeneralResponse>> liveData = viewModel.getResponseLiveData();
        liveData.observe(this, new Observer<GlobalResponse<GeneralResponse>>() {
            @Override
            public void onChanged(GlobalResponse<GeneralResponse> response) {
                showHideProgressDialog(false);
                handleAPkUpdateResponse(response);
            }
        });
    }

    /**
     * Responsibility - handleAPkUpdateResponse is an method that takes response from handleApkUpdateDirectly and if any new version comes then change session value and redirect  to main
     * Parameters - Its take GlobalResponse<GeneralResponse> response
     **/
    private void handleAPkUpdateResponse(GlobalResponse<GeneralResponse> response) {
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
                            sessionManager.setApkVersion(BuildConfig.VERSION_NAME);
                            sessionManager.setVersionDetails(apkDetails);
                            openMainActivity();
                        } else {
                            ValidationHelper.showToast(context, getString(R.string.no_update_available));
                        }
                    }

                }


            }
        }
    }


    /**
     * Responsibility - launchApp method is used for launch other application its just an demo
     * Parameters - No parameter
     **/
    private void launchApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            ValidationHelper.showToast(ConfigSettings.this, "There is no package available in android");
        }
    }


    /**
     * Responsibility - feedBack method is used for transfer controll to FeedBackActivity page
     * Parameters - No parameter
     **/
    private void feedBack() {
        Intent intent = new Intent(ConfigSettings.this, FeedBackActivity.class);
        startActivity(intent);
    }


    /**
     * Responsibility - logout method is used for logout the app but not in used
     * Parameters - No parameter
     **/
    private void logout() {
        sessionManager.removeSession();
        Intent intent = new Intent(ConfigSettings.this, BaseUrlSettings.class);
        ProcessPhoenix.triggerRebirth(ConfigSettings.this, intent);
    }


    /**
     * Responsibility - openRefreshRate method is used for open RefreshTimer activity
     * Parameters - No parameter
     **/
    private void openRefreshRate() {
        Intent intent = new Intent(ConfigSettings.this, RefreshTimer.class);
        startActivity(intent);
    }


    /**
     * Responsibility - updateBaseUrl method is used for open UpdateBaseUrl activity
     * Parameters - No parameter
     **/
    private void updateBaseUrl() {
        Intent intent = new Intent(ConfigSettings.this, UpdateBaseUrl.class);
        startActivity(intent);

    }


    /**
     * Responsibility - openUpdatePositionActivity method is used for open UpdatePosition activity
     * Parameters - No parameter
     **/
    private void openUpdatePositionActivity() {
        Intent intent = new Intent(ConfigSettings.this, UpdatePosition.class);
        startActivity(intent);
    }


    /**
     * Responsibility - openLogActivity method is used for open LogsMainActivity activity
     * Parameters - No parameter
     **/
    private void openLogActivity() {
        Intent intent = new Intent(ConfigSettings.this, LogsMainActivity.class);
        startActivity(intent);
    }


    /**
     * Responsibility - openMainActivity method is used for open MainActivity activity
     * Parameters - No parameter
     **/
    private void openMainActivity() {
        Intent intent = new Intent(ConfigSettings.this, MainActivity.class);
        startActivity(intent);
    }

}
