package com.daisy.activity.onBoarding.slider.screenAdd;

import android.content.Context;

import com.daisy.R;
import com.daisy.databinding.AddScreenBinding;
import com.daisy.utils.ValidationHelper;

public class ScreenAddValidationHelper  {
    private Context context;
    private AddScreenBinding binding;
    public ScreenAddValidationHelper(Context context,AddScreenBinding binding)
    {
        this.context=context;
        this.binding=binding;
    }

    public boolean isValid()
    {
        return  true;
    }
}
