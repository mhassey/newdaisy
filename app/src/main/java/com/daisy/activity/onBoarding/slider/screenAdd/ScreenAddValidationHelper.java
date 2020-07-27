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
        if (!ValidationHelper.isBlank(binding.isle,context.getString(R.string.isle_not_empty)))
        {

                if (!ValidationHelper.isBlank(binding.shelf,context.getString(R.string.shelf_not_empty)))
                {
                    if (!ValidationHelper.isBlank(binding.position,context.getString(R.string.position_not_empty))) {
                        return true;

                    }

                }

        }
        return  false;
    }
}
