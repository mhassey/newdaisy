package com.daisy.activity.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daisy.R;

/**
 * Purpose -  BaseFragment is an fragment that extends by all fragment available in app and define some common functionality
 * Responsibility - All the basic code that will used in every fragment is written in BaseFragment
 **/
public class BaseFragment extends Fragment {

    private ProgressDialog progressDialog;
    private Typeface regularFontTypeFace;
    private Typeface boldFontTypeFace;
    private Typeface semiBoldFontTypeFace;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Responsibility -  Define no title bar for all fragment
     * Parameters - Activity object to know from which fragment we want to remove title bar
     **/
    public void setNoTitleBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

        }

    }


    /**
     * Responsibility -  showHideProgressDialog method help to show progress bar from all fragment when app needed
     * Parameters - its takes iShow boolean value that define progress should display or hide
     **/
    public void showHideProgressDialog(boolean iShow) {
        try {
            if (progressDialog != null) {
                if (iShow)
                    progressDialog.show();
                else {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }
            } else {
                progressDialog = new ProgressDialog(requireContext(), R.style.ProgressTheme);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                showHideProgressDialog(iShow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void globalSettingsRegularFont(TextView view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (regularFontTypeFace==null)
            {
                regularFontTypeFace = getResources().getFont(R.font.rragular);
            }

            view.setTypeface(regularFontTypeFace);


        }
    }
    public void globalSettingsBoldFont(TextView view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (boldFontTypeFace==null)
            {
                boldFontTypeFace = getResources().getFont(R.font.rbold);
            }

            view.setTypeface(boldFontTypeFace);


        }
    }

    void globalSettingsSemiBoldFont(TextView view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (semiBoldFontTypeFace==null)
            {
                semiBoldFontTypeFace = getResources().getFont(R.font.rmedium);
            }

            view.setTypeface(semiBoldFontTypeFace);


        }
    }
}
