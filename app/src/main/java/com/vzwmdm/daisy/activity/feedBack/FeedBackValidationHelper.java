package com.vzwmdm.daisy.activity.feedBack;

import android.content.Context;

import com.vzwmdm.daisy.R;
import com.vzwmdm.daisy.databinding.ActivityFeedBackBinding;
import com.vzwmdm.daisy.utils.ValidationHelper;

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
