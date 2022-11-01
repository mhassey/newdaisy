package com.iris.dialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.iris.R;
import com.iris.databinding.TimeCorrectPermissionLayoutBinding;

public class DateTimePermissionDIalog extends DialogFragment implements View.OnClickListener {
    private TimeCorrectPermissionLayoutBinding timeCorrectPermissionLayoutBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        timeCorrectPermissionLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.time_correct_permission_layout, container, false);
        getDialog().setCancelable(false);
        initClick();
        return timeCorrectPermissionLayoutBinding.getRoot();
    }

    private void initClick() {
        timeCorrectPermissionLayoutBinding.curveLayout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();

        params.width = 900;
        params.height = 1200;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.curve_layout: {
                handleClickEvent();
                break;
            }
        }
    }

    private void handleClickEvent() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
        dismiss();
    }
}
