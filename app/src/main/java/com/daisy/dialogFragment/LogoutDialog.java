package com.daisy.dialogFragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.daisy.R;
import com.daisy.broadcast.broadcastforbackgroundservice.AlaramHelperBackground;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.LogoutBinding;
import com.daisy.service.BackgroundService;
import com.daisy.utils.ValidationHelper;

public class LogoutDialog extends DialogFragment implements View.OnClickListener {
    private LogoutBinding logoutBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logoutBinding = DataBindingUtil.inflate(inflater, R.layout.logout, container, false);
        initClick();
        return logoutBinding.getRoot();
    }

    private void initClick() {
        logoutBinding.curveLayout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();

        params.width = getScreenWidth();
        params.height = getScreenHeight();
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels / 1;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels / 2;
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
        if (BackgroundService.getServiceObject() != null) {
            AlaramHelperBackground.cancelAlarmElapsed();
            AlaramHelperBackground.cancelAlarmRTC();
            BackgroundService.getServiceObject().closeService();
            requireActivity().stopService(new Intent(requireActivity(), BackgroundService.class));
            SessionManager.get().clear();
            closeHoleApp();
        } else {
            ValidationHelper.showToast(requireActivity(), getString(R.string.please_wait_service_is_not_register_yet));
        }
        dismiss();
    }

    private void closeHoleApp() {
        requireActivity().stopLockTask();
        requireActivity().finishAffinity();
        System.exit(0);
    }

}
