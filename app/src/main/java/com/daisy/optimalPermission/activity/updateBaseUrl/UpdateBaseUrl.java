package com.daisy.optimalPermission.activity.updateBaseUrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.optimalPermission.activity.base.BaseActivity;
import com.daisy.optimalPermission.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.daisy.optimalPermission.activity.splash.SplashScreen;
import com.daisy.optimalPermission.session.SessionManager;
import com.daisy.databinding.ActivityUpdateBaseUrlBinding;
import com.daisy.optimalPermission.pojo.response.GeneralResponse;
import com.daisy.optimalPermission.pojo.response.GlobalResponse;
import com.daisy.optimalPermission.utils.Constraint;
import com.daisy.optimalPermission.utils.Utils;
import com.daisy.optimalPermission.utils.ValidationHelper;

import java.util.HashMap;

/**
 * Purpose -  UpdateBaseUrl is an activity that help to change baseurl of app if we need to change server url just add our app will open with new baseurl
 * Responsibility - Its ask for Url if its valid then change its to main server url and start welcome screen again
 **/
public class UpdateBaseUrl extends BaseActivity implements View.OnClickListener {

    private ActivityUpdateBaseUrlBinding mBinding;
    private Context context;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_base_url);
        initView();
        initClick();
    }


    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        context = this;
        sessionManager = SessionManager.get();
    }

    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        mBinding.saveAndLoad.setOnClickListener(this);
    }

    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveAndLoad: {
                updateBaseUrl();
                break;
            }
        }
    }

    /**
     * Responsibility - updateBaseUrl method is used when user change server url its check that url is valid by firing general api
     * Parameters - No parameter
     **/
    private void updateBaseUrl() {
        String url = mBinding.baseUrl.getText().toString();
        if (url != null) {
            String urlLastChar = url.substring(url.length() - 1);
            if (urlLastChar.equals("/")) {
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

    /**
     * Responsibility - handleGeneralResponse method is called by updateBaseUrl method its check if response is correct then delete all session and redirect the screen to welcome screen
     * Parameters - Its contains GlobalResponse<GeneralResponse> object
     **/
    private void handleGeneralResponse(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {
        showHideProgressDialog(false);
        if (generalResponseGlobalResponse != null) {
            if (generalResponseGlobalResponse.isApi_status()) {
                sessionManager.deleteAllSession();
                sessionManager.setBaseUrl(mBinding.baseUrl.getText().toString());
                Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constraint.EXIT_UPPER, Constraint.TRUE);
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
