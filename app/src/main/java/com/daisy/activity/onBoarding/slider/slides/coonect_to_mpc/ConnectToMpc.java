package com.daisy.activity.onBoarding.slider.slides.coonect_to_mpc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.activity.base.BaseFragment;
import com.daisy.activity.onBoarding.slider.OnBoarding;
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ConnectToMpcBinding;
import com.daisy.pojo.ServerConfig;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.HashMap;

/**
 * Purpose - ConnectToMpc class helps to configure base server for ally
 */
public class ConnectToMpc extends BaseFragment implements View.OnClickListener {

    private static OnBoarding baording;
    private ConnectToMpcBinding connectToMpcBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        connectToMpcBinding = DataBindingUtil.inflate(inflater, R.layout.connect_to_mpc, container, false);
        initClick();
        return connectToMpcBinding.getRoot();
    }

    /**
     * Purpose - initClick method initialize click listener
     */
    private void initClick() {
        connectToMpcBinding.connect.setOnClickListener(this);
    }


    // getInstance method is used for getting signup object
    public static ConnectToMpc getInstance(OnBoarding onBoarding) {
        baording = onBoarding;
        return new ConnectToMpc();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect: {
                handleConnectEvent();
                break;
            }
        }
    }

    /**
     * Purpose - handleConnectEvent method checks the code is valid or not and set it to base config server
     */
    private void handleConnectEvent() {

        if (!connectToMpcBinding.code.getText().toString().equals("")) {
            HashMap<String, String> stringStringHashMap = new ServerConfig().getServerConfig();
            String value = stringStringHashMap.get(connectToMpcBinding.code.getText().toString().trim());
            if (value != null) {
                updateBaseUrl(value);
            } else {
                if (connectToMpcBinding.code.getText().toString().contains(Constraint.HTTP)) {
                    updateBaseUrl(connectToMpcBinding.code.getText().toString());
                } else {
                    ValidationHelper.showToast(getContext(), getString(R.string.wrong_server_code));
                }
            }
        } else {
            ValidationHelper.showToast(getContext(), getString(R.string.please_enter_server_code));
        }
    }


    /**
     * Responsibility - updateBaseUrl is an method that takes url from ui and call general api with same server url and if its return response then send it to  handleGeneralResponse
     * Parameters - No parameter
     *
     * @param s
     */
    private void updateBaseUrl(String url) {

        if (url != null) {
            String urlLastChar = url.substring(url.length() - 1);
            if (urlLastChar.equals(Constraint.SLASH)) {
                boolean b = Utils.isValidUrl(url);
                if (b) {
                    if (Utils.getNetworkState(getActivity())) {
                        SessionManager.get().setBaseUrl(url);
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
                        ValidationHelper.showToast(getContext(), getString(R.string.no_internet_available));
                    }
                } else {
                    ValidationHelper.showToast(getContext(), getString(R.string.enter_valid_url));
                }
            } else {
                ValidationHelper.showToast(getContext(), getString(R.string.url_must_end_with_slash));
            }
        } else {
            ValidationHelper.showToast(getContext(), getString(R.string.baseurl_can_not_be_empty));
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
                SessionManager.get().setBaseUrlChange(true);
                baording.counterPlus();
            } else {
                SessionManager.get().removeBaseUrl();
                ValidationHelper.showToast(getContext(), getString(R.string.enter_valid_url));

            }
        } else {
            SessionManager.get().removeBaseUrl();
            ValidationHelper.showToast(getContext(), getString(R.string.enter_valid_url));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        designWork();
    }

    // Change design at run time
    private void designWork() {

        baording.mBinding.tabDotsLayout.getTabAt(Constraint.ZERO).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.ONE).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.TWO).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.THREE).setIcon(getResources().getDrawable(R.drawable.select_dark_gray));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.FOUR).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.FIVE_INE_REAL).setIcon(getResources().getDrawable(R.drawable.default_dot));

    }
}
