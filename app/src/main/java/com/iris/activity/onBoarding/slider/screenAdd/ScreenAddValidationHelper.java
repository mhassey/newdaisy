package com.iris.activity.onBoarding.slider.screenAdd;

import android.content.Context;

import com.iris.R;
import com.iris.databinding.AddScreenBinding;
import com.iris.utils.ValidationHelper;

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
