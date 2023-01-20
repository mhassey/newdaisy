package com.android_tv.activity.onBoarding.slider.slides.coonect_to_mpc;

import android.content.Context;

import com.android_tv.R;
import com.android_tv.databinding.ActivityWelcomeScreenBinding;
import com.android_tv.databinding.ConnectToMpcBinding;
import com.android_tv.utils.ValidationHelper;

public class ConnectToMpcValidationHelper {
    private Context context;
    private ConnectToMpcBinding welcomeScreenBinding;

    public ConnectToMpcValidationHelper(Context context, ConnectToMpcBinding activityWelcomeScreenBinding) {
        this.context = context;
        this.welcomeScreenBinding = activityWelcomeScreenBinding;
    }


    public boolean isValid() {
        if (!ValidationHelper.isBlank(welcomeScreenBinding.code, context.getString(R.string.key_can_not_be_empty))) {
            return true;
        }
        return false;
    }
}
