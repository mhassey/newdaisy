package com.daisy.security;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import com.daisy.common.session.SessionManager;

public class Admin extends DeviceAdminReceiver {

    void showToast(Context context, CharSequence msg) {
         SessionManager.get().setPasswordCorrect(false);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {

        showToast(context, "Device Administrator: Activated");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "This is an optional message to warn the user about disabling.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {

    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
    }
}