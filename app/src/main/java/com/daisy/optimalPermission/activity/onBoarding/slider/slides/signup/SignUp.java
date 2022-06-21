package com.daisy.optimalPermission.activity.onBoarding.slider.slides.signup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.databinding.FragmentLoginBinding;
import com.daisy.optimalPermission.activity.base.BaseFragment;
import com.daisy.optimalPermission.activity.onBoarding.slider.OnBoarding;
import com.daisy.optimalPermission.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.daisy.optimalPermission.database.DBCaller;
import com.daisy.optimalPermission.session.SessionManager;
import com.daisy.optimalPermission.utils.Constraint;
import com.daisy.optimalPermission.utils.Utils;
import com.daisy.optimalPermission.utils.ValidationHelper;

import java.util.HashMap;
import java.util.Locale;

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
        loginBinding.cancel.setOnClickListener(this::onClick);
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
            } else {
                baording.counterMinus();
            }
        } else {
            baording.counterMinus();
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }
    }

    //  Handle signup response
    private void handleResponse(SignUpResponse signUpResponse) {
        showHideProgressDialog(false);
        if (signUpResponse != null) {
            if (signUpResponse.isApi_status()) {
                DBCaller.storeLogInDatabase(context, Constraint.LOGIN_SUCCESSFULL, "", "", Constraint.APPLICATION_LOGS);
                sessionManager.setPasswordForLock(loginBinding.password.getText().toString());
                sessionManager.setOpenTime(signUpResponse.getData().getOpen());
                sessionManager.setCloseTime(signUpResponse.getData().getClosed());
                sessionManager.setOffset(signUpResponse.getData().getUTCOffset());
                sessionManager.setSenitized(signUpResponse.getData().getDeviceSanitize());
                sessionManager.setDeviceSecurity(signUpResponse.getData().getDeviceSecurity());
                sessionManager.setPricingPlainId(signUpResponse.getData().getPricingPlanID());
                sessionManager.setServerTime(signUpResponse.getData().getCurrentTime());
                sessionManager.setSignUpData(signUpResponse.getData());
                baording.counterPlus();
            } else {

                baording.counterMinus();
                ValidationHelper.showToast(context, signUpResponse.getMessage());
            }
        } else {

            baording.counterMinus();
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }

    }

    //  Create signup request
    private HashMap<String, String> getSignUpRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.STORE_CODE, loginBinding.storeCode.getText().toString());
        hashMap.put(Constraint.PASSWORD_ID, loginBinding.password.getText().toString());

        return hashMap;
    }

    @Override
    public void onResume() {
        super.onResume();
        designWork();
    }

    // Change design at run time
    private void designWork() {
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            if (Locale.getDefault().getLanguage().equals(Constraint.AR)) {
                baording.mBinding.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_mettle_green_rtl));
            } else

                baording.mBinding.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_mettle_green));
        } else {
            if (Locale.getDefault().getLanguage().equals(Constraint.AR))
                baording.mBinding.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_mettle_green_rtl));
            else

                baording.mBinding.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_mettle_green));
        }
        if (!SessionManager.get().getDeviceSecured()) {
            baording.mBinding.tabDotsLayout.getTabAt(Constraint.ZERO).setIcon(getResources().getDrawable(R.drawable.default_dot));
//            baording.mBinding.tabDotsLayout.getTabAt(Constraint.ONE).setIcon(getResources().getDrawable(R.drawable.default_dot));
            baording.mBinding.tabDotsLayout.getTabAt(Constraint.ONE).setIcon(getResources().getDrawable(R.drawable.selected_green));
            baording.mBinding.tabDotsLayout.getTabAt(Constraint.TWO).setIcon(getResources().getDrawable(R.drawable.default_dot));

        } else {
            baording.mBinding.tabDotsLayout.getTabAt(Constraint.ZERO).setIcon(getResources().getDrawable(R.drawable.default_dot));
            baording.mBinding.tabDotsLayout.getTabAt(Constraint.ONE).setIcon(getResources().getDrawable(R.drawable.default_dot));
            baording.mBinding.tabDotsLayout.getTabAt(Constraint.TWO).setIcon(getResources().getDrawable(R.drawable.selected_green));
            baording.mBinding.tabDotsLayout.getTabAt(Constraint.THREE).setIcon(getResources().getDrawable(R.drawable.default_dot));
        }
    }


}
