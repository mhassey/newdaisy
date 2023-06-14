package com.allyy.activity.onBoarding.slider.slides.signup;

import android.content.Context;
import android.os.Bundle;
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

import com.allyy.R;
import com.allyy.activity.base.BaseFragment;
import com.allyy.activity.onBoarding.slider.OnBoarding;
import com.allyy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.allyy.common.session.SessionManager;
import com.allyy.databinding.FragmentLoginBinding;
import com.allyy.utils.Constraint;
import com.allyy.utils.Utils;
import com.allyy.utils.ValidationHelper;
import com.allyy.utils.WifiPermissionDialog;

import java.util.HashMap;

/**
 * Purpose -  SignUp is an activity that help to sign up with store code and password
 * Responsibility - Its ask for store code and password and fire sign up api and handle response
 **/
public class SignUp extends BaseFragment implements View.OnClickListener {
    private static OnBoarding baording;
    public static FragmentLoginBinding loginBinding;
    private Context context;
    private SignUpViewModel signUpViewModel;
    private SessionManager sessionManager;
    private SignUpValidationHelper signUpValidationHelper;
    final int sdk = android.os.Build.VERSION.SDK_INT;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return loginBinding.getRoot();
    }

    // getInstance method is used for getting signup object
    public static SignUp getInstance(OnBoarding onBoarding) {
        baording = onBoarding;
        return new SignUp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initClick();
    }

    // Initiate clicks
    private void initClick() {

        loginBinding.singup.setOnClickListener(this);
    }

    //  Initiate objects
    private void initView() {
        context = requireContext();
        sessionManager = SessionManager.get();
        signUpValidationHelper = new SignUpValidationHelper(context, loginBinding);
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singup: {

                doSignUp();

                break;
            }
            case R.id.cancel: {
                getActivity().onBackPressed();
                break;
            }
        }
    }

    // Perform signup
    private void doSignUp() {
        if (Utils.getNetworkState(context)) {
            if (signUpValidationHelper.isValid()) {
                showHideProgressDialog(true);
                HashMap<String, String> signUpRequest = getSignUpRequest();
                signUpViewModel.setSignUpRequestMutableLiveData(signUpRequest);
                LiveData<SignUpResponse> liveData = signUpViewModel.getResponseLiveData();
                if (!liveData.hasActiveObservers()) {
                    liveData.observe(this, new Observer<SignUpResponse>() {
                        @Override
                        public void onChanged(SignUpResponse signUpResponse) {
                            handleResponse(signUpResponse);
                        }
                    });
                }
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }
    }

    //  Handle signup response
    private void handleResponse(SignUpResponse signUpResponse) {
        showHideProgressDialog(false);
        if (signUpResponse != null) {
            if (signUpResponse.isApi_status()) {
                sessionManager.setPasswordForLock(loginBinding.password.getText().toString());
                sessionManager.setOpenTime(signUpResponse.getData().getOpen());
                sessionManager.setCloseTime(signUpResponse.getData().getClosed());
                sessionManager.setOffset(signUpResponse.getData().getUTCOffset());
                sessionManager.setSenitized(signUpResponse.getData().getDeviceSanitize());
                sessionManager.setDeviceSecurity(signUpResponse.getData().getDeviceSecurity());
                sessionManager.setPricingPlainId(signUpResponse.getData().getPricingPlanID());
                sessionManager.setServerTime(signUpResponse.getData().getCurrentTime());
                sessionManager.setSignUpData(signUpResponse.getData());
                baording.counterPlus(signUpResponse.getData().getDeviceId());
            } else {
                ValidationHelper.showToast(context, signUpResponse.getMessage());
            }
        } else {

            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }

    }

    //  Create signup request
    private HashMap<String, String> getSignUpRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.STORE_CODE, loginBinding.storeCode.getText().toString());
        hashMap.put(Constraint.PASSWORD_ID, loginBinding.password.getText().toString());
        hashMap.put(Constraint.DEVICENAME, Utils.ModelNumber());

        return hashMap;
    }

    @Override
    public void onResume() {
        super.onResume();
        designWork();
        checkWifi();
    }

    private void checkWifi() {
        boolean b = Utils.checkWifiState(getContext());
        if (!b) {
            WifiPermissionDialog dateTimePermissionDIalog = new WifiPermissionDialog();
            dateTimePermissionDIalog.setCancelable(false);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            dateTimePermissionDIalog.show(ft, null);
        }

    }


    // Change design at run time
    private void designWork() {

        baording.mBinding.tabDotsLayout.getTabAt(Constraint.ZERO).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.ONE).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.TWO).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.THREE).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.FOUR).setIcon(getResources().getDrawable(R.drawable.selected_purple));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.FIVE_INE_REAL).setIcon(getResources().getDrawable(R.drawable.default_dot));

//        baording.mBinding.tabDotsLayout.getTabAt(Constraint.TWO).setIcon(getResources().getDrawable(R.drawable.default_dot));
    }


}
