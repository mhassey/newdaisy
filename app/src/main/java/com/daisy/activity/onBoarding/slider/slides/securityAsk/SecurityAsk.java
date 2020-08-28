package com.daisy.activity.onBoarding.slider.slides.securityAsk;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daisy.R;
import com.daisy.database.DBCaller;
import com.daisy.databinding.FragmentSecurityAskBinding;
import com.daisy.utils.Constraint;


public class SecurityAsk extends Fragment {

    public FragmentSecurityAskBinding securityAskBinding;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        securityAskBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_security_ask, container, false);
        return securityAskBinding.getRoot();
    }

    public static SecurityAsk getInstance() {
        return new SecurityAsk();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        context=requireContext();

    }
}
