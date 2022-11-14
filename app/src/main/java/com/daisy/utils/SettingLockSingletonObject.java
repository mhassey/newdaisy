package com.daisy.utils;

import android.os.Handler;

public class SettingLockSingletonObject {

    public static SettingLockSingletonObject lockSingletonObject = new SettingLockSingletonObject();
    public Handler countDownTimerMain;

    public static SettingLockSingletonObject getInstance() {
        return lockSingletonObject;
    }

    public Handler getLockCounDownTimer() {
        return countDownTimerMain;
    }


    public void setCOunter(Handler countDownTimer) {
        countDownTimerMain = countDownTimer;
    }
}

