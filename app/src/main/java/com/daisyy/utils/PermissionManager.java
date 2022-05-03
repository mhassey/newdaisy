package com.daisyy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermission(Activity activity, String[] permission, int requestCode) {
        List<String> permissions = new ArrayList<>();
        for (String per : permission) {
            int status = activity.checkSelfPermission(per);
            if (status != PackageManager.PERMISSION_GRANTED) {
                permissions.add(per);
            }
        }
        if (!permissions.isEmpty()) {
            activity.requestPermissions(permissions.toArray(new String[permissions.size()]), requestCode);
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermissionOnly(Activity activity, String[] permission, int requestCode) {
        List<String> permissions = new ArrayList<>();
        for (String per : permission) {
            int status = activity.checkSelfPermission(per);
            if (status != PackageManager.PERMISSION_GRANTED) {
                permissions.add(per);
            }
        }
        if (!permissions.isEmpty()) {
            return false;
        }
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkPermission(Context context, Fragment activity, String[] permission, int requestCode) {
        List<String> permissions = new ArrayList<>();
        for (String per : permission) {
            int status = context.checkSelfPermission(per);
            if (status != PackageManager.PERMISSION_GRANTED) {
                permissions.add(per);
            }
        }
        if (!permissions.isEmpty()) {
            activity.requestPermissions(permissions.toArray(new String[permissions.size()]), requestCode);
            return false;
        }
        return true;
    }
}