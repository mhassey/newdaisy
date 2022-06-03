package com.daisy.activity.onBoarding.slider.slides.coonect_to_mpc;

import android.content.Context;

import com.daisy.R;
import com.daisy.databinding.ActivityWelcomeScreenBinding;
import com.daisy.databinding.ConnectToMpcBinding;
import com.daisy.utils.ValidationHelper;

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
