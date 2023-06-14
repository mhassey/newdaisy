package com.ally.activity.onBoarding.slider.slides.welcome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.ally.R;
import com.ally.activity.base.BaseFragment;
import com.ally.activity.onBoarding.slider.OnBoarding;
import com.ally.databinding.WelcomeAskBinding;
import com.ally.utils.Constraint;

public class WelcomeAsk extends BaseFragment implements View.OnClickListener {
    private WelcomeAskBinding mBinding;
    private static OnBoarding baording;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.welcome_ask, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mBinding.getStarted.setOnClickListener(this);
    }

    // getInstance method is used for getting signup object
    public static WelcomeAsk getInstance(OnBoarding onBoarding) {
        baording = onBoarding;
        return new WelcomeAsk();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // Change design at run time
    private void designWork() {

        baording.mBinding.tabDotsLayout.getTabAt(Constraint.ZERO).setIcon(getResources().getDrawable(R.drawable.selected_dot_red));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.ONE).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.TWO).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.THREE).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.FOUR).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.FIVE_INE_REAL).setIcon(getResources().getDrawable(R.drawable.default_dot));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_started: {
                handleGetStartEvent();
                break;
            }
        }
    }

    private void handleGetStartEvent() {
        baording.counterPlus();
    }
}
