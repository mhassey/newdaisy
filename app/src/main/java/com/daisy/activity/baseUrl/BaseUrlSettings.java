package com.daisy.activity.baseUrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.BuildConfig;
import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.daisy.activity.splash.SplashScreen;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityBaseUrlSettingsBinding;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Url;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseUrlSettings extends BaseActivity implements View.OnClickListener {

    private SessionManager sessionManager;
    private ActivityBaseUrlSettingsBinding mBinding;
    private Context context;
    private BaseUrlSettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_base_url_settings);
        viewModel=new ViewModelProvider(this).get(BaseUrlSettingsViewModel.class);

        initView();
        initClick();


    }


    private void initView() {
        sessionManager = SessionManager.get();

        setNoTitleBar(this);
        context = this;
        boolean b = sessionManager.getBaseUrlAdded();
        if (b) {
            Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
        viewModel.setDefaultUrls();
        setDefaultUrlData();
    }


    private void setDefaultUrlData() {

        ArrayAdapter<Url> orientationAdapter = new ArrayAdapter<Url>(context, android.R.layout.simple_spinner_item, viewModel.getUrls());
        orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.baseUrl.setAdapter(orientationAdapter);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }

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


    private void initClick() {
        mBinding.nextSlide.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextSlide: {
                updateBaseUrl();
                break;
            }
        }

    }

    private void updateBaseUrl() {
        Url mainUrl = (Url) mBinding.baseUrl.getSelectedItem();
        String url=mainUrl.getUrl();
        if (url != null) {
            String urlLastChar = url.substring(url.length() - 1);
            if (urlLastChar.equals(Constraint.SLASH)) {
                boolean b = Utils.isValidUrl(url);
                if (b) {
                    if (Utils.getNetworkState(context)) {
                        sessionManager.setBaseUrl(url);
                        AddScreenViewModel addScreenViewModel = new ViewModelProvider(this).get(AddScreenViewModel.class);
                        showHideProgressDialog(true);
                        addScreenViewModel.setGeneralRequest(new HashMap<>());
                        LiveData<GlobalResponse<GeneralResponse>> liveData = addScreenViewModel.getGeneralResponseLiveData();
                        if (!liveData.hasActiveObservers()) {
                            liveData.observe(this, new Observer<GlobalResponse<GeneralResponse>>() {
                                @Override
                                public void onChanged(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {
                                    handleGeneralResponse(generalResponseGlobalResponse);
                                }
                            });
                        }
                    } else {
                        ValidationHelper.showToast(context, getString(R.string.no_internet_available));
                    }
                } else {
                    ValidationHelper.showToast(context, getString(R.string.enter_valid_url));
                }
            } else {
                ValidationHelper.showToast(context, getString(R.string.url_must_end_with_slash));
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.baseurl_can_not_be_empty));
        }
    }

    private void handleGeneralResponse(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {
        showHideProgressDialog(false);
        if (generalResponseGlobalResponse != null) {
            if (generalResponseGlobalResponse.isApi_status()) {
                //sessionManager.deleteAllSession();
                 sessionManager.setBaseUrlChange(true);
                Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constraint.EXIT_CAPITAL, true);
                startActivity(intent);
            } else {
                sessionManager.removeBaseUrl();
                ValidationHelper.showToast(context, getString(R.string.enter_valid_url));

            }
        } else {
            sessionManager.removeBaseUrl();
            ValidationHelper.showToast(context, getString(R.string.enter_valid_url));
        }
    }

}
