package com.allyy.activity.onBoarding.slider.slides.coonect_to_mpc;

import android.content.Context;

import com.allyy.R;
import com.allyy.databinding.ActivityWelcomeScreenBinding;
import com.allyy.databinding.ConnectToMpcBinding;
import com.allyy.utils.ValidationHelper;

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
