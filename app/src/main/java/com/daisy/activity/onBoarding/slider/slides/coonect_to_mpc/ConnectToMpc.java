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
import com.daisy.pojo.response.KeyToUrlResponse;
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
    private ConnectToMpcViewModel connectToMpcViewModel;
    private ConnectToMpcValidationHelper connectToMpcValidationHelper;
    private String myBaseUrls[] = {
            "https://id1.mobilepricecards.com",
            "https://id2.mobilepricecards.com",
            "https://id3.mobilepricecards.com",

    };
    private int listIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        connectToMpcBinding = DataBindingUtil.inflate(inflater, R.layout.connect_to_mpc, container, false);
        connectToMpcViewModel = new ViewModelProvider(this).get(ConnectToMpcViewModel.class);
        connectToMpcValidationHelper = new ConnectToMpcValidationHelper(getContext(), connectToMpcBinding);
        initClick();
        defineKeyToUrlObserver();
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

    /**
     * Responsibility - defineKeyToUrlObserver handles key to url response
     */
    private void defineKeyToUrlObserver() {
        LiveData<GlobalResponse<KeyToUrlResponse>> globalResponseLiveData = connectToMpcViewModel.getResponseLiveData();
        if (!globalResponseLiveData.hasActiveObservers()) {
            globalResponseLiveData.observe(this, new Observer<GlobalResponse<KeyToUrlResponse>>() {
                @Override
                public void onChanged(GlobalResponse<KeyToUrlResponse> keyToUrlResponseGlobalResponse) {
                    showHideProgressDialog(false);
                    if (keyToUrlResponseGlobalResponse != null) {
                        handleKeyToUrlResponse(keyToUrlResponseGlobalResponse);
                    } else {
                        checkLoadedKey();
                    }
                }
            });
        }
    }

    private void checkLoadedKey() {
        if (connectToMpcBinding.code.getText().toString().contains(Constraint.HTTP)) {
            updateBaseUrl(connectToMpcBinding.code.getText().toString());

        } else {
            if (listIndex <= 2) {
                if (connectToMpcValidationHelper.isValid()) {
                    if (Utils.getNetworkState(getContext())) {
                        showHideProgressDialog(true);
                        connectToMpcViewModel.setRequestLiveData(createKeyToUrlRequest());
                    } else {
                        ValidationHelper.showToast(getContext(), getString(R.string.no_internet_available));
                    }
                }
            } else if (listIndex == 3) {
                ValidationHelper.showToast(getContext(), getString(R.string.technical_issue));
            }


        }
    }

    private HashMap<String, String> createKeyToUrlRequest() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(Constraint.customerID, connectToMpcBinding.code.getText().toString());
        stringStringHashMap.put(Constraint.TOKEN, SessionManager.get().getDeviceToken());
        stringStringHashMap.put(Constraint.ID_BASE_URL, myBaseUrls[listIndex++]);
        return stringStringHashMap;
    }

    /**
     * Responsibility - handleKeyToUrlResponse method manipulate server
     *
     * @param result
     */
    private void handleKeyToUrlResponse(GlobalResponse<KeyToUrlResponse> result) {
        if (result != null) {
            if (result.getResult().isMatched_status()) {
                handleConnectEvent(result.getResult().getMatched_url() + Constraint.SLASH);
            } else {
                ValidationHelper.showToast( getActivity(), getResources().getString(R.string.mpc_key_not_correct));
            }
        } else {
            checkLoadedKey();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect: {
                listIndex = 0;
                checkLoadedKey();
                break;
            }
        }
    }

    /**
     * Purpose - handleConnectEvent method checks the code is valid or not and set it to base config server
     */
    private void handleConnectEvent(String url) {

        if (!url.equals("")) {
            if (url != null) {
                updateBaseUrl(url);
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
