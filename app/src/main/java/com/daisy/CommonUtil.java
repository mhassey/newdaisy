package com.daisy;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

public class CommonUtil {
    public static boolean isSystemAlertWindowEnabled(Context context) {
        // SYSTEM_ALERT_WINDOW is disabled on on low ram devices starting from Q
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return (am.isLowRamDevice() && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q));
    }
}
