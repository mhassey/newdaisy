package com.daisy.mainDaisy.activity.feedBack;

import android.content.Context;

import com.daisy.R;
import com.daisy.databinding.ActivityFeedBackBinding;
import com.daisy.mainDaisy.utils.ValidationHelper;

/**
 * Purpose - FeedBackValidationHelper class help to validate feedback data
 */
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
