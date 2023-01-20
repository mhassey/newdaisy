package com.android_tv.activity.onBoarding.slider.slides.signup;

import android.content.Context;

import com.android_tv.R;
import com.android_tv.databinding.FragmentLoginBinding;
import com.android_tv.utils.ValidationHelper;

public class SignUpValidationHelper {
    private Context context;
    private FragmentLoginBinding loginBinding;
    public  SignUpValidationHelper(Context context,FragmentLoginBinding fragmentLoginBinding)
    {
        this.context=context;
        this.loginBinding=fragmentLoginBinding;
    }
    public boolean isValid()
    {
        if (!ValidationHelper.isBlank(loginBinding.storeCode,context.getString(R.string.please_enter_store_code)))
        {
            if (!ValidationHelper.isBlank(loginBinding.password,context.getString(R.string.please_enter_store_password)))
            {
            return true;
            }
        }
        return false;
    }
}
