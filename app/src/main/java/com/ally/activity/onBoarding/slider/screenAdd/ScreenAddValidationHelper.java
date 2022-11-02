package com.ally.activity.onBoarding.slider.screenAdd;

import android.content.Context;

import com.ally.databinding.AddScreenBinding;

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
