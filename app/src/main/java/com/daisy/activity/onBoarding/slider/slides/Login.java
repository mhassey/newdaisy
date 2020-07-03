package com.daisy.activity.onBoarding.slider.slides;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daisy.R;
import com.daisy.activity.onBoarding.slider.OnBaording;
import com.daisy.databinding.FragmentLoginBinding;

public class Login extends Fragment implements View.OnClickListener {
    private static OnBaording baording;
    private FragmentLoginBinding loginBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return loginBinding.getRoot();
    }

    public static Login getInstance(OnBaording onBaording) {
        baording = onBaording;
        return new Login();
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
   //     baording.mBinding.nextSlide.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singup: {
                baording.counterPlus();
                break;
            }
        }
    }
}
