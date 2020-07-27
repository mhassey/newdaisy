package com.daisy.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.daisy.R;

public class DialogUtil {


    public static String SelectedTab = "";
    static String startTime = "", endTime = "";

    /**
     * get a blocking progress dialog.
     *
     * @param context context of current activity/fragment
     * @return create of {@link ProgressDialog}
     */
    public static ProgressDialog getProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme);
//        progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    /**
     * Static method to get an create of material styled progress bar
     *
     * @param context Context of the current class
     * @param resId   Resource Id of the progress bar
     * @return An create of MaterialProgressBar
     */
    public static ProgressBar getProgressBarInstance(Context context, int resId) {
        ProgressBar nonBlockingProgressBar = ((Activity) context).getWindow().findViewById(resId);
        return nonBlockingProgressBar;
    }

    public static ProgressBar getProgressBarInstance(View view, int resId) {
        if (view != null) {
            ProgressBar nonBlockingProgressBar = view.findViewById(resId);
            return nonBlockingProgressBar;
        }
        return null;
    }



}
