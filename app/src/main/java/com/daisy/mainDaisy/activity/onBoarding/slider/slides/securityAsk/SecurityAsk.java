package com.daisy.mainDaisy.activity.onBoarding.slider.slides.securityAsk;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.daisy.R;
import com.daisy.databinding.ActivityOnBaordingBinding;
import com.daisy.databinding.FragmentSecurityAskBinding;
import com.daisy.mainDaisy.utils.Constraint;

import java.util.Locale;

/**
 * Purpose -  SecurityAsk is an fragment that ask for security checks
 * Responsibility - Its ask for some security check example delete photo or not
 **/
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

    /**
     * Purpose - getInstance method set binding data and return object of SecurityAsk
     *
     * @param onBaordingBinding
     * @return
     */
    public static SecurityAsk getInstance(ActivityOnBaordingBinding onBaordingBinding) {
        ActivityOnBaordingBinding = onBaordingBinding;
        return new SecurityAsk();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initClick();
    }

    /**
     * Purpose - initClick method initiate the click listener
     */
    private void initClick() {
        securityAskBinding.cancel.setOnClickListener(this);
    }

    private void initView() {
        context = requireContext();

    }

    @Override
    public void onResume() {
        super.onResume();
        designWork();

    }


    /**
     * Purpose - designWork method set background of next button ui
     */
    private void designWork() {

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            if (Locale.getDefault().getLanguage().equals(Constraint.AR))
                ActivityOnBaordingBinding.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_blue_rtl));
            else

                ActivityOnBaordingBinding.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_blue));
        } else {
            if (Locale.getDefault().getLanguage().equals(Constraint.AR))
                ActivityOnBaordingBinding.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_blue_rtl));
            else
                ActivityOnBaordingBinding.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_blue));
        }
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.default_dot));
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.selected_blue));
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.default_dot));
        ActivityOnBaordingBinding.tabDotsLayout.getTabAt(3).setIcon(getResources().getDrawable(R.drawable.default_dot));
    }

    /**
     * Purpose - onClick method is handle all click listener
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel: {
                getActivity().onBackPressed();
                break;
            }
        }
    }

}
