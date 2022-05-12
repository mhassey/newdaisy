package com.daisy.daisyGo.utils;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.daisy.R;
import com.daisy.daisyGo.activity.welcomeScreen.WelcomeScreen;
import com.daisy.daisyGo.broadcast.broadcastforbackgroundservice.AlaramHelperBackground;
import com.daisy.daisyGo.session.SessionManager;
import com.daisy.databinding.LogoutBinding;
import com.daisy.daisyGo.service.BackgroundService;

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

        params.width = 900;
        params.height = 1200;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.curve_layout: {
                handleLogout();
                break;
            }
        }
    }

    private void handleClickEvent() {
        if (BackgroundService.getServiceObject() != null) {
            AlaramHelperBackground.cancelAlarmElapsed();
            AlaramHelperBackground.cancelAlarmRTC();
            BackgroundService.getServiceObject().closeService();
            getActivity().stopService(new Intent(getActivity(), BackgroundService.class));
            SessionManager.get().clear();
            closeHoleApp();
        } else {
            ValidationHelper.showToast(getActivity(), getString(R.string.please_wait_service_is_not_register_yet));
        }
        dismiss();
    }

    /**
     * Responsibility - handleLogout is an method that help to logout the app with stop all services
     * Parameters - No parameter
     **/
    private void handleLogout() {
        if (BackgroundService.getServiceObject() != null) {
            AlaramHelperBackground.cancelAlarmElapsed();
            AlaramHelperBackground.disableBootReceiver(getContext());
            AlaramHelperBackground.cancelAlarmRTC();
            BackgroundService.getServiceObject().closeService();
            getActivity().stopService(new Intent(getActivity(), BackgroundService.class));
            SessionManager.get().clear();
            SessionManager.get().logout(true);
            Intent intent = new Intent(getActivity(), WelcomeScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            ValidationHelper.showToast(getActivity(), getString(R.string.please_wait_service_is_not_register_yet));
        }
    }

    private void closeHoleApp() {
        getActivity().stopLockTask();
        getActivity().finishAffinity();
        System.exit(0);
    }

}
