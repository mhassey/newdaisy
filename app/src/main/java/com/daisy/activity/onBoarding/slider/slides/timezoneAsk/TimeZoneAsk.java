package com.daisy.activity.onBoarding.slider.slides.timezoneAsk;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.daisy.R;
import com.daisy.activity.base.BaseFragment;
import com.daisy.activity.onBoarding.slider.OnBoarding;
import com.daisy.activity.onBoarding.slider.slides.welcome.WelcomeAsk;
import com.daisy.databinding.TimezoneAskBinding;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;

public class TimeZoneAsk extends BaseFragment implements View.OnClickListener {

    private TimezoneAskBinding timezoneAskBinding;
    private static OnBoarding baording;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        timezoneAskBinding = DataBindingUtil.inflate(inflater, R.layout.timezone_ask, container, false);
        initClick();
        return timezoneAskBinding.getRoot();
    }

    private void initClick() {
        timezoneAskBinding.next.setOnClickListener(this::onClick);
        timezoneAskBinding.timeZoneSelection.setOnClickListener(this);
    }

    // getInstance method is used for getting signup object
    public static TimeZoneAsk getInstance(OnBoarding onBoarding) {
        baording = onBoarding;
        return new TimeZoneAsk();
    }


    @Override
    public void onResume() {
        super.onResume();
        designWork();
        handleResumePermission();
    }


    private void handleResumePermission() {
        boolean permissionAvailable = Utils.isTimeAutomatic(getActivity());


        if (permissionAvailable) {
            timezoneAskBinding.next.setVisibility(View.VISIBLE);
            timezoneAskBinding.timeZoneSelection.setVisibility(View.VISIBLE);
        } else {
            timezoneAskBinding.timeZoneSelection.setVisibility(View.VISIBLE);
            timezoneAskBinding.next.setVisibility(View.GONE);


        }
    }

    // Change design at run time
    private void designWork() {

        baording.mBinding.tabDotsLayout.getTabAt(Constraint.ZERO).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.ONE).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.TWO).setIcon(getResources().getDrawable(R.drawable.selected_dot_purple));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.THREE).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.FOUR).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.FIVE_INE_REAL).setIcon(getResources().getDrawable(R.drawable.default_dot));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next: {
                handleNext();
                break;
            }
            case R.id.time_zone_selection: {
                handleClickEvent();
                break;
            }
        }
    }


    private void handleClickEvent() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
    }

    private void handleNext() {
        baording.counterPlus();
    }


}
