package com.daisy.activity.onBoarding.slider.slides.signup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daisy.R;
import com.daisy.activity.base.BaseFragment;
import com.daisy.activity.onBoarding.slider.OnBaording;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpRequest;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.FragmentLoginBinding;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.HashMap;

public class SignUp extends BaseFragment implements View.OnClickListener {
    private static OnBaording baording;
    public static FragmentLoginBinding loginBinding;
    private Context context;
    private SignUpViewModel signUpViewModel;
    private SessionManager sessionManager;
    private SignUpValidationHelper signUpValidationHelper;
    final int sdk = android.os.Build.VERSION.SDK_INT;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return loginBinding.getRoot();
    }

    public static SignUp getInstance(OnBaording onBaording) {
        baording = onBaording;
        return new SignUp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initClick();
    }

    private void initClick() {

        loginBinding.singup.setOnClickListener(this);
        loginBinding.cancel.setOnClickListener(this::onClick);
    }

    private void initView() {
        context=requireContext();
        sessionManager=SessionManager.get();
        signUpValidationHelper=new SignUpValidationHelper(context,loginBinding);
        signUpViewModel=new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singup: {

               doSignUp();

                break;
            }
            case R.id.cancel:
            {
                getActivity().onBackPressed();
                break;
            }
        }
    }

    private void doSignUp() {
        if (Utils.getNetworkState(context))
        {
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
            else{
                baording.counterMinus();
            }
        }
        else
        {
            baording.counterMinus();
            ValidationHelper.showToast(context,getString(R.string.no_internet_available));
        }
    }

    private void handleResponse(SignUpResponse signUpResponse) {
         // handleResponse
        showHideProgressDialog(false);
        if (signUpResponse!=null) {
            if (signUpResponse.isApi_status()) {
                DBCaller.storeLogInDatabase(context,getString(R.string.login_success),"","",Constraint.APPLICATION_LOGS);
                sessionManager.setPasswordForLock(loginBinding.password.getText().toString());
                sessionManager.setSignUpData(signUpResponse.getData());
                baording.counterPlus();
            } else {

                baording.counterMinus();
                ValidationHelper.showToast(context, signUpResponse.getMessage());
            }
        }else
        {

            baording.counterMinus();
            ValidationHelper.showToast(context,getString(R.string.no_internet_available));
        }

    }

    private HashMap<String,String> getSignUpRequest() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put(Constraint.STORE_CODE,loginBinding.storeCode.getText().toString());
        hashMap.put(Constraint.PASSWORD_ID,loginBinding.password.getText().toString());

        return hashMap;
    }
    @Override
    public void onResume() {
        super.onResume();
    desginWork();
    }

    private void desginWork() {
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            baording.mBinding.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_mettle_green) );
        } else {
            baording.mBinding.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_mettle_green));
        }
        baording.mBinding.tabDotsLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.selected_green));
        baording.mBinding.tabDotsLayout.getTabAt(3).setIcon(getResources().getDrawable(R.drawable.default_dot));
    }

}
