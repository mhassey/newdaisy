package com.allyy.activity.onBoarding.slider.screenAdd;

import android.content.Context;

import com.allyy.databinding.AddScreenBinding;

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
