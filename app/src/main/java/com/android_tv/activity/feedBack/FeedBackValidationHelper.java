package com.android_tv.activity.feedBack;

import android.content.Context;

import com.android_tv.R;
import com.android_tv.databinding.ActivityFeedBackBinding;
import com.android_tv.utils.ValidationHelper;

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
