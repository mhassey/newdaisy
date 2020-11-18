package com.daisy.activity.updateBaseUrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.daisy.activity.splash.SplashScreen;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityUpdateBaseUrlBinding;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.HashMap;

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
     * Initial data setup
     */
    private void initView() {
        context = this;
        sessionManager = SessionManager.get();
    }

    /**
     * Button clicks initializing
     */
    private void initClick() {
        mBinding.saveAndLoad.setOnClickListener(this);
    }

    /**
     * Handle Clicks listener
     */
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
     * Update base url
     */
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
     * Handle General response
     */
    private void handleGeneralResponse(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {
        showHideProgressDialog(false);
        if (generalResponseGlobalResponse != null) {
            if (generalResponseGlobalResponse.isApi_status()) {
                sessionManager.deleteAllSession();
                sessionManager.setBaseUrl(mBinding.baseUrl.getText().toString());
                Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
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
