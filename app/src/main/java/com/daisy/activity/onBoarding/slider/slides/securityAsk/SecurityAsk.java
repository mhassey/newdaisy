package com.daisy.activity.onBoarding.slider.slides.securityAsk;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daisy.R;
import com.daisy.databinding.ActivityOnBaordingBinding;
import com.daisy.databinding.FragmentSecurityAskBinding;


public class SecurityAsk extends Fragment implements View.OnClickListener {

    public FragmentSecurityAskBinding securityAskBinding;
    private Context context;
    final int sdk = android.os.Build.VERSION.SDK_INT;
    private static ActivityOnBaordingBinding ActivityOnBaordingBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        securityAskBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_security_ask, container, false);

        return securityAskBinding.getRoot();
    }

    public static SecurityAsk getInstance(ActivityOnBaordingBinding onBaordingBinding) {
        ActivityOnBaordingBinding=onBaordingBinding;
        return new SecurityAsk();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initClick();
    }

    private void initClick() {
    securityAskBinding.cancel.setOnClickListener(this);
    }

    private void initView() {
        context=requireContext();

    }

    @Override
    public void onResume() {
        super.onResume();
        designWork();

    }

    private void designWork() {

        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOnBaordingBinding.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_blue) );
        } else {
            ActivityOnBaordingBinding.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_blue));
        }
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.default_dot));
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.selected_blue));
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.default_dot));
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(3).setIcon(getResources().getDrawable(R.drawable.default_dot));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cancel:
            {
                getActivity().onBackPressed();
                break;
            }
        }
    }
}
