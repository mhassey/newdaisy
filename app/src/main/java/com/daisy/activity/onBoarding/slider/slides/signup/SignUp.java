package com.daisy.activity.onBoarding.slider.slides.signup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daisy.R;
import com.daisy.activity.onBoarding.slider.OnBaording;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpRequest;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.daisy.databinding.FragmentLoginBinding;
import com.daisy.utils.Utils;

public class SignUp extends Fragment implements View.OnClickListener {
    private static OnBaording baording;
    private FragmentLoginBinding loginBinding;
    private Context context;
    private SignUpViewModel signUpViewModel;

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
    }

    private void initView() {
        context=requireContext();
       // signUpViewModel=new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singup: {
              //  doSignUp();
                baording.counterPlus();
                break;
            }
        }
    }

    private void doSignUp() {
        if (Utils.getNetworkState(context))
        {
            SignUpRequest signUpRequest=getSignUpRequest();
            signUpViewModel.setSignUpRequestMutableLiveData(signUpRequest);
            LiveData<SignUpResponse> liveData=signUpViewModel.getResponseLiveData();
            if (!liveData.hasActiveObservers())
            {
                liveData.observe(this, new Observer<SignUpResponse>() {
                    @Override
                    public void onChanged(SignUpResponse signUpResponse) {
                        handleResponse(signUpResponse);
                    }
                });
            }
        }
    }

    private void handleResponse(SignUpResponse signUpResponse) {
         // handleResponse
    }

    private SignUpRequest getSignUpRequest() {
        SignUpRequest signUpRequest=new SignUpRequest();
        signUpRequest.setStoreCode(loginBinding.storeCode.getText().toString());
        signUpRequest.setPassword(loginBinding.password.getText().toString());
        return signUpRequest;
    }
}
