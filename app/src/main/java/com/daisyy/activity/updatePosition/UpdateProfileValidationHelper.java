package com.daisyy.activity.updatePosition;

import android.content.Context;

import com.daisyy.R;
import com.daisyy.databinding.ActivityUpdatePositionBinding;
import com.daisyy.utils.ValidationHelper;

public class UpdateProfileValidationHelper {
    private Context context;
    private ActivityUpdatePositionBinding binding;
    public UpdateProfileValidationHelper(Context context,ActivityUpdatePositionBinding binding)
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
                if (!ValidationHelper.isBlank(binding.position,context.getString(R.string.position_not_empty)))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
