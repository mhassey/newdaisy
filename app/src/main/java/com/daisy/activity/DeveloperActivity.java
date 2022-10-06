package com.daisy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daisy.BuildConfig;
import com.daisy.R;
import com.daisy.activity.apkUpdate.ApkUpdateViewModel;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.configSettings.ConfigSettings;
import com.daisy.activity.feedBack.FeedBackActivity;
import com.daisy.activity.langSupport.LangSupportViewModel;
import com.daisy.activity.logs.LogsMainActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.refreshTimer.RefreshTimer;
import com.daisy.activity.socketConnection.SocketConnection;
import com.daisy.adapter.LangSupportAdaptor;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityDeveloperBinding;
import com.daisy.databinding.ActivityLangSelectionBinding;
import com.daisy.interfaces.LangSupportCallBack;
import com.daisy.pojo.LangPojo;
import com.daisy.pojo.response.ApkDetails;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.HashMap;
import java.util.Locale;

public class DeveloperActivity extends BaseActivity implements View.OnClickListener, LangSupportCallBack {
    private ActivityDeveloperBinding mBinding;
    private SessionManager sessionManager;
    private Context context;
    private ApkUpdateViewModel viewModel;
    private ActivityLangSelectionBinding alertBinding;
    private AlertDialog dialog;
    private LangSupportViewModel langSupportViewModel;
    private LangSupportAdaptor langSupportAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_developer);
        alertBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_lang_selection, null, false);
        langSupportViewModel = new ViewModelProvider(this).get(LangSupportViewModel.class);
        langSupportViewModel.setLangData(this);
        context = this;
        setNoTitleBar(this);
        initView();
        initClick();
    }

    void initView() {
        viewModel = new ViewModelProvider(this).get(ApkUpdateViewModel.class);
        langSupportAdaptor = new LangSupportAdaptor(langSupportViewModel.getLangPojosForAdaptor(), this);
        alertBinding.lang.setLayoutManager(new LinearLayoutManager(context));
        alertBinding.lang.setAdapter(langSupportAdaptor);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertBinding.getRoot());
        alert.setCancelable(false);
        dialog = alert.create();
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
            String val = Utils.getLastUpdateDate(DeveloperActivity.this);
            mBinding.updatetime.setText(" " + val);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch ((view.getId())) {
            case R.id.socket_connection: {
                handleSocketConnectionCall();
                break;
            }
            case R.id.setRefreshRate: {
                openRefreshRate();
                break;
            }
            case R.id.logs: {
                openLogActivity();
                break;
            }

            case R.id.feedBack: {
                feedBack();
                break;
            }

            case R.id.changeLanguage: {
                startLangSupportActivity();
                //changeLanguage();
                break;
            }
            case R.id.direct_apk_update: {
                handleApkUpdateDirectly();
                break;
            }
            case R.id.ac_close: {
                onBackPressed();
                break;
            }
        }
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
     * Responsibility - openMainActivity method is used for open MainActivity activity
     * Parameters - No parameter
     **/
    private void openMainActivity() {
        Intent intent = new Intent(DeveloperActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void initClick() {

        mBinding.sanitisedMain.setOnCheckedChangeListener(getCheckedListener());
        mBinding.securitySwitch.setOnCheckedChangeListener(getSecuritySwitch());
        mBinding.alramSwitch.setOnCheckedChangeListener(getAlarmSwitch());
        mBinding.sanitisedHeader.setOnClickListener(this::onClick);
        mBinding.directApkUpdate.setOnClickListener(this::onClick);
        mBinding.feedBack.setOnClickListener(this::onClick);
        mBinding.changeLanguage.setOnClickListener(this::onClick);
        mBinding.logs.setOnClickListener(this::onClick);
        mBinding.setRefreshRate.setOnClickListener(this::onClick);
        mBinding.acClose.setOnClickListener(this);
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
     * Responsibility - startLangSupportActivity is an method to redirect page to  LangSelectionActivity
     * Parameters - No parameter
     **/
    private void startLangSupportActivity() {
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.START | Gravity.BOTTOM;
        wmlp.x = 100;   //x position
        wmlp.y = 120;   //y position
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.getWindow().setLayout((width - (width / 2)), (((height / 2)))); //Controlling width and height.
        alertBinding.closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    /**
     * Responsibility - openLogActivity method is used for open LogsMainActivity activity
     * Parameters - No parameter
     **/
    private void openLogActivity() {
        Intent intent = new Intent(DeveloperActivity.this, LogsMainActivity.class);
        startActivity(intent);
    }


    /**
     * Responsibility - feedBack method is used for transfer controll to FeedBackActivity page
     * Parameters - No parameter
     **/
    private void feedBack() {
        Intent intent = new Intent(DeveloperActivity.this, FeedBackActivity.class);
        startActivity(intent);
    }


    /**
     * Purpose - handleSocketConnectionCall method open socket connection page
     */
    private void handleSocketConnectionCall() {
        Intent intent = new Intent(this, SocketConnection.class);
        startActivity(intent);
    }


    /**
     * Responsibility - openRefreshRate method is used for open RefreshTimer activity
     * Parameters - No parameter
     **/
    private void openRefreshRate() {
        Intent intent = new Intent(DeveloperActivity.this, RefreshTimer.class);
        startActivity(intent);
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

    @Override
    public RecyclerView.ViewHolder getHolder() {
        return langSupportViewModel.getViewHolder();
    }

    @Override
    public void setLangPojo(LangPojo langPojo, RecyclerView.ViewHolder viewHolder) {
        langSupportViewModel.setViewHolder(viewHolder);
        langSupportViewModel.setSelectedLanguage(langPojo);
        LangPojo selectedLang = langSupportViewModel.getSelectedLanguage();
        if (selectedLang != null) {
            dialog.dismiss();
            setLang(selectedLang.getKey());
        } else {
            ValidationHelper.showToast(context, getString(R.string.please_select_lang));
        }
    }

    @Override
    public void setDefaultLang(LangPojo langPojo, RecyclerView.ViewHolder holder) {
        langSupportViewModel.setViewHolder(holder);

    }

    /**
     * Responsibility - setLang method help to set app language
     * Parameters - No parameter
     **/
    private void setLang(String s) {
        s = s.trim();
        Locale locale = new Locale(s);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SessionManager.get().setLang(s);
        Intent i = new Intent(DeveloperActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

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
}