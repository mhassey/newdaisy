package com.ally.activity.feedBack;

import android.content.Context;

import com.ally.R;
import com.ally.databinding.ActivityFeedBackBinding;
import com.ally.utils.ValidationHelper;

public class FeedBackValidationHelper {
    private Context context;
    private ActivityFeedBackBinding binding;
    public FeedBackValidationHelper(Context context, ActivityFeedBackBinding binding)
    {
     this.context=context;
     this.binding=binding;
    }

    public boolean isValid()
    {
        if (!ValidationHelper.isBlank(binding.description,context.getString(R.string.all_field_are_mendetory)))
        {
            return true;
        }
        return  false;
    }
}
