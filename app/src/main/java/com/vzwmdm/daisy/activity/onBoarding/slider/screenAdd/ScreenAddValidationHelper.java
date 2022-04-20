package com.vzwmdm.daisy.activity.onBoarding.slider.screenAdd;

import android.content.Context;

import com.vzwmdm.daisy.R;
import com.vzwmdm.daisy.databinding.AddScreenBinding;
import com.vzwmdm.daisy.utils.ValidationHelper;

public class ScreenAddValidationHelper {
    private Context context;
    private AddScreenBinding binding;

    public ScreenAddValidationHelper(Context context, AddScreenBinding binding) {
        this.context = context;
        this.binding = binding;
    }

    public boolean isValid() {
        if (binding != null) {
            if (binding.productName.getSelectedItem() != null)
                return true;
            else {
                ValidationHelper.showToast(context, context.getString(R.string.please_select_product));
                return false;
            }
        }
        return false;
    }
}
