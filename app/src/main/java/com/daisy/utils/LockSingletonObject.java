package com.daisy.utils;

import android.os.Handler;

public class LockSingletonObject {

    public static LockSingletonObject lockSingletonObject = new LockSingletonObject();
    public Handler countDownTimerMain;

    public static LockSingletonObject getInstance() {
        return lockSingletonObject;
    }

    public Handler getLockCounDownTimer() {
        return countDownTimerMain;
    }


    public void setCOunter(Handler countDownTimer) {
        countDownTimerMain = countDownTimer;
    }
}
