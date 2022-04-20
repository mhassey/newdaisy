package com.vzwmdm.daisy.activity.baseUrl;

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

import com.vzwmdm.daisy.R;
import com.vzwmdm.daisy.activity.base.BaseActivity;
import com.vzwmdm.daisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.vzwmdm.daisy.activity.splash.SplashScreen;
import com.vzwmdm.daisy.common.session.SessionManager;
import com.vzwmdm.daisy.databinding.ActivityBaseUrlSettingsBinding;
import com.vzwmdm.daisy.pojo.response.GeneralResponse;
import com.vzwmdm.daisy.pojo.response.GlobalResponse;
import com.vzwmdm.daisy.pojo.response.Url;
import com.vzwmdm.daisy.utils.Constraint;
import com.vzwmdm.daisy.utils.Utils;
import com.vzwmdm.daisy.utils.ValidationHelper;

import java.util.HashMap;

/**
 * Purpose -  BaseUrlSettings is an activity that helps to select an an server
 * Responsibility - BaseUrlSettings takes url as input check that url is valid and giving response if yes makes it default url
 **/
public class BaseUrlSettings extends BaseActivity implements View.OnClickListener {

    private SessionManager sessionManager;
    private ActivityBaseUrlSettingsBinding mBinding;
    private Context context;
    private BaseUrlSettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_base_url_settings);
        viewModel = new ViewModelProvider(this).get(BaseUrlSettingsViewModel.class);
        initView();
        initClick();
    }

    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        sessionManager = SessionManager.get();
        setNoTitleBar(this);
        context = this;
        boolean b = sessionManager.getBaseUrlAdded();
        if (b) {
            Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
            startActivity(intent);
            overridePendingTransition(Constraint.ZERO, Constraint.ZERO);
            finish();
        }
        viewModel.setDefaultUrls();
        setDefaultUrlData();
    }


    /**
     * Responsibility - setDefaultUrlData method takes default url from view model and pass to adaptor that will reflect when user try to type similar url
     * Parameters - No parameter
     **/
    private void setDefaultUrlData() {

        ArrayAdapter<Url> orientationAdapter = new ArrayAdapter<Url>(context, R.layout.spinner_item, viewModel.getUrls());
        orientationAdapter.setDropDownViewResource(R.layout.spinner_item);
        mBinding.baseUrl.setAdapter(orientationAdapter);
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
     * Responsibility - initClick is an method that initiate all clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        mBinding.nextSlide.setOnClickListener(this::onClick);
        mBinding.spinnerOpen.setOnClickListener(this::onClick);
    }


    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextSlide: {
                updateBaseUrl();
                break;
            }
            case R.id.spinnerOpen: {
                mBinding.baseUrl.showDropDown();
                break;
            }


        }

    }

    /**
     * Responsibility - updateBaseUrl is an method that takes url from ui and call general api with same server url and if its return response then send it to  handleGeneralResponse
     * Parameters - No parameter
     **/
    private void updateBaseUrl() {
        try {

            String url = mBinding.baseUrl.getText().toString();
            if (url != null && !url.equals("")) {
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
        } catch (Exception e) {

        }
    }

    /**
     * Responsibility - handleGeneralResponse is an method check  response is correct or not if response is good  that redirect screen to splash else its show error message
     * Parameters - Its take GlobalResponse<GeneralResponse> generalResponseGlobalResponse that help to know url is correct or not
     **/
    private void handleGeneralResponse(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {
        showHideProgressDialog(false);
        if (generalResponseGlobalResponse != null) {
            if (generalResponseGlobalResponse.isApi_status()) {
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
