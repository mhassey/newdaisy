package com.iris.activity.onBoarding.slider.slides.signup;

import android.content.Context;

import com.iris.R;
import com.iris.databinding.FragmentLoginBinding;
import com.iris.utils.ValidationHelper;

/**
 * Purpose - SignUpValidationHelper class handles validation of sign up
 */
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
