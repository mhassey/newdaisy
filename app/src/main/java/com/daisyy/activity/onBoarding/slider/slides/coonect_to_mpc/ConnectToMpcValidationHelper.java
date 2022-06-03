package com.daisyy.activity.onBoarding.slider.slides.coonect_to_mpc;

import android.content.Context;

import com.daisyy.R;
import com.daisyy.databinding.ActivityWelcomeScreenBinding;
import com.daisyy.databinding.ConnectToMpcBinding;
import com.daisyy.utils.ValidationHelper;

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
