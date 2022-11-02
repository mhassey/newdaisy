package com.ally.utils;

import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.ally.R;
import com.ally.databinding.TimeCorrectPermissionLayoutBinding;
import com.ally.databinding.WifiAlertDialogBinding;

public class WifiPermissionDialog extends DialogFragment implements View.OnClickListener {
    private WifiAlertDialogBinding wifiAlertDialogBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        wifiAlertDialogBinding = DataBindingUtil.inflate(inflater, R.layout.wifi_alert_dialog, container, false);
        getDialog().setCancelable(false);
        initClick();
        return wifiAlertDialogBinding.getRoot();
    }

    private void initClick() {
        wifiAlertDialogBinding.curveLayout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();

        new Handler(Looper.getMainLooper()).postDelayed((Runnable) () -> {
            params.width = getScreenWidth();
            params.height = getScreenHeight();
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        },200);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels / 1;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels / 1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.curve_layout: {
                goToWifi();
                break;
            }
        }
    }


    /**
     * Ge to wifi screen
     */
    private void goToWifi() {

        Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        if (Utils.getDeviceName().contains(getString(R.string.onePlus))) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        }
        this.startActivityForResult(intent, Constraint.NINE_THOUSANT_NINE_HUNDRED);
        dismiss();
    }

}
