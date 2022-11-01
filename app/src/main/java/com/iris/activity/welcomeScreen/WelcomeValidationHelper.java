package com.iris.activity.welcomeScreen;

import android.content.Context;

import com.iris.R;
import com.iris.databinding.ActivityWelcomeScreenBinding;
import com.iris.utils.ValidationHelper;

public class WelcomeValidationHelper {
    private Context context;
    private ActivityWelcomeScreenBinding welcomeScreenBinding;

    public WelcomeValidationHelper(Context context, ActivityWelcomeScreenBinding welcomeScreenBinding) {
        this.welcomeScreenBinding = welcomeScreenBinding;
        this.context = context;
    }

    public boolean isValid() {
        if (!ValidationHelper.isBlank(welcomeScreenBinding.keyName, context.getString(R.string.key_can_not_be_empty))) {
            return true;
        }
        return false;
    }
}

