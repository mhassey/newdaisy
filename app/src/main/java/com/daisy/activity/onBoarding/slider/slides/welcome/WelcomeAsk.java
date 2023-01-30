package com.daisy.activity.onBoarding.slider.slides.welcome;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.activity.base.BaseFragment;
import com.daisy.activity.onBoarding.slider.OnBoarding;
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.daisy.activity.welcomeScreen.WelcomeValidationHelper;
import com.daisy.activity.welcomeScreen.WelcomeViewModel;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityWelcomeScreenBinding;
import com.daisy.dialogFragment.DateTimePermissionDIalog;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.KeyToUrlResponse;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;

public class WelcomeAsk extends BaseFragment implements View.OnClickListener {
    private ActivityWelcomeScreenBinding mBinding;
    private static OnBoarding baording;
    private SessionManager sessionManager;
    final int sdk = android.os.Build.VERSION.SDK_INT;
    private WelcomeValidationHelper welcomeValidationHelper;
    private WelcomeViewModel welcomeViewModel;
    private String myBaseUrls[] = {
            "https://id1.mobilepricecards.com",
            "https://id2.mobilepricecards.com",
            "https://id3.mobilepricecards.com",

    };
    private int listIndex = 0;
    private AddScreenViewModel addScreenViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_welcome_screen, container, false);
        welcomeValidationHelper = new WelcomeValidationHelper(requireContext(), mBinding);
        welcomeViewModel = new ViewModelProvider(this).get(WelcomeViewModel.class);
        addScreenViewModel = new ViewModelProvider(this).get(AddScreenViewModel.class);
        defineGlobalSettings();
        initView();
        initClick();
        firebaseConfiguration();
        defineKeyToUrlObserver();
        handleGeneralApiResponse();
        return mBinding.getRoot();
    }



    private void defineGlobalSettings() {

        globalSettingsBoldFont(mBinding.welcomeLabel);
       globalSettingsRegularFont(mBinding.welcomeInnerLabel);
        globalSettingsBoldFont(mBinding.begin);


    }

    private void initView() {
        sessionManager = SessionManager.get();
    }

    // getInstance method is used for getting signup object
    public static WelcomeAsk getInstance(OnBoarding onBoarding) {
        baording = onBoarding;
        return new WelcomeAsk();
    }

    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        mBinding.begin.setOnClickListener(this);
    }

    private void firebaseConfiguration() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }


                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.e("My token", token);

                        SessionManager.get().setFCMToken(token);
                    }
                });
    }


    private void handleGeneralApiResponse() {
        LiveData<GlobalResponse<GeneralResponse>> liveData = addScreenViewModel.getGeneralResponseLiveData();
        if (!liveData.hasActiveObservers()) {
            liveData.observe(getViewLifecycleOwner(), new Observer<GlobalResponse<GeneralResponse>>() {
                @Override
                public void onChanged(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {
                    handleGeneralResponse(generalResponseGlobalResponse);
                }
            });
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
                baording.counterPlus();

            } else {
                sessionManager.removeBaseUrl();
                ValidationHelper.showToast(requireContext(), getString(R.string.enter_valid_url));

            }
        } else {
            sessionManager.removeBaseUrl();
            ValidationHelper.showToast(requireContext(), getString(R.string.enter_valid_url));
        }
    }

    /**
     * Responsibility - defineKeyToUrlObserver handles key to url response
     */
    private void defineKeyToUrlObserver() {
        LiveData<GlobalResponse<KeyToUrlResponse>> globalResponseLiveData = welcomeViewModel.getResponseLiveData();
        if (!globalResponseLiveData.hasActiveObservers()) {
            globalResponseLiveData.observe(getViewLifecycleOwner(), new Observer<GlobalResponse<KeyToUrlResponse>>() {
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

    /**
     * Responsibility - handleKeyToUrlResponse method manipulate server
     *
     * @param result
     */
    private void handleKeyToUrlResponse(GlobalResponse<KeyToUrlResponse> result) {
        if (result != null) {
            if (result.getResult().isMatched_status()) {
                handleGeneralApi(result.getResult().getMatched_url() + Constraint.SLASH);
            } else {
                ValidationHelper.showToast(requireContext(), getResources().getString(R.string.mpc_key_not_correct));
            }
        } else {
            checkLoadedKey();
        }
    }

    private void handleGeneralApi(String s) {
        sessionManager.setBaseUrl(s);
        showHideProgressDialog(true);
        addScreenViewModel.setGeneralRequest(new HashMap<>());


    }

    @Override
    public void onResume() {
        super.onResume();
        boolean permissionAvailable = Utils.isTimeAutomatic(requireContext());
        if (!permissionAvailable) {
            showAlertIfTimeIsNotCorrect();
        }
        designWork();

    }

    private void designWork() {

        if (SessionManager.get().getDisableSecurity()) {
            baording.mBinding.tabDotsLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.selected_dot_red));
            baording.mBinding.tabDotsLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.default_dot));
            baording.mBinding.tabDotsLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.default_dot));
            baording.mBinding.tabDotsLayout.getTabAt(3).setIcon(getResources().getDrawable(R.drawable.default_dot));


        } else {
            baording.mBinding.tabDotsLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.selected_dot_red));
            baording.mBinding.tabDotsLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.default_dot));
            baording.mBinding.tabDotsLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.default_dot));
            baording.mBinding.tabDotsLayout.getTabAt(3).setIcon(getResources().getDrawable(R.drawable.default_dot));
            baording.mBinding.tabDotsLayout.getTabAt(4).setIcon(getResources().getDrawable(R.drawable.default_dot));

        }
    }


    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begin: {
                Utils.hideKeyboard(requireContext());
                listIndex = 0;
                checkLoadedKey();
                break;
            }
        }
    }

    private void checkLoadedKey() {
        if (mBinding.keyName.getText().toString().contains(Constraint.HTTP)) {
            updateBaseUrl();

        } else {
            if (listIndex <= 2) {
                if (welcomeValidationHelper.isValid()) {
                    if (Utils.getNetworkState(requireContext())) {
                        showHideProgressDialog(true);
                        welcomeViewModel.setRequestLiveData(createKeyToUrlRequest());
                    } else {
                        ValidationHelper.showToast(requireContext(), getString(R.string.no_internet_available));
                    }
                }
            } else if (listIndex == 3) {
                ValidationHelper.showToast(requireContext(), getString(R.string.technical_issue));
            }


        }
    }

    private HashMap<String, String> createKeyToUrlRequest() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(Constraint.customerID, mBinding.keyName.getText().toString());
        stringStringHashMap.put(Constraint.TOKEN, SessionManager.get().getDeviceToken());
        stringStringHashMap.put(Constraint.ID_BASE_URL, myBaseUrls[listIndex++]);
        return stringStringHashMap;
    }

    /**
     * Responsibility - goToOnBoarding method redirect screen to OnBaording activity
     * Parameters - No parameter
     **/
    private void goToOnBoarding() {
        Intent intent = new Intent(requireContext(), OnBoarding.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constraint.EXIT_CAPITAL, true);
        startActivity(intent);
    }


    /**
     * show alert if timezone is not correct
     **/
    public void showAlertIfTimeIsNotCorrect() {
        DateTimePermissionDIalog dateTimePermissionDIalog = new DateTimePermissionDIalog();
        dateTimePermissionDIalog.setCancelable(false);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        dateTimePermissionDIalog.show(ft, null);


    }


    /**
     * Responsibility - updateBaseUrl is an method that takes url from ui and call general api with same server url and if its return response then send it to  handleGeneralResponse
     * Parameters - No parameter
     **/
    private void updateBaseUrl() {
        try {

            String url = mBinding.keyName.getText().toString();
            if (url != null && !url.equals("")) {
                String urlLastChar = url.substring(url.length() - 1);
                if (urlLastChar.equals(Constraint.SLASH)) {
                    boolean b = Utils.isValidUrl(url);
                    if (b) {
                        if (Utils.getNetworkState(requireContext())) {
                            sessionManager.setBaseUrl(url);
                            showHideProgressDialog(true);
                            addScreenViewModel.setGeneralRequest(new HashMap<>());
                        } else {
                            ValidationHelper.showToast(requireContext(), getString(R.string.no_internet_available));
                        }
                    } else {
                        ValidationHelper.showToast(requireContext(), getString(R.string.enter_valid_url));
                    }
                } else {
                    ValidationHelper.showToast(requireContext(), getString(R.string.url_must_end_with_slash));
                }
            } else {
                ValidationHelper.showToast(requireContext(), getString(R.string.baseurl_can_not_be_empty));
            }
        } catch (Exception e) {

        }
    }


}
