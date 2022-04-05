package com.nzmdm.daisy.utils;

import android.os.CountDownTimer;

public class SanitisedSingletonObject {
    public static SanitisedSingletonObject sanitisedSingletonObject = new SanitisedSingletonObject();
    public static CountDownTimer countDownTimerMain;

    public static SanitisedSingletonObject getInstance() {
        return sanitisedSingletonObject;
    }

    public static CountDownTimer getSanitisedCoundownTimer() {
        return countDownTimerMain;
    }


    public void setCOunter(CountDownTimer countDownTimer) {
        countDownTimerMain = countDownTimer;
    }
}
