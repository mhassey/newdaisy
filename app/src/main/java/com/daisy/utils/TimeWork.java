package com.daisy.utils;

import com.daisy.common.session.SessionManager;
import com.daisy.pojo.response.Time;
import com.daisy.pojo.response.UpdateTiming;

import java.util.Timer;

public class TimeWork {
    private static TimeWork timer = new TimeWork();

    private TimeWork() {
    }

    public static TimeWork getTimer() {
        return timer;
    }

    public int deleteTiming = ((Constraint.TEN * Constraint.SIXTY)) * Constraint.THOUSAND;

    public int logTiming = ((6 * Constraint.THIRTY_SIX_HUNDRED)) * Constraint.THOUSAND;

    public int checkUpdateTiming = 0;

    public int checkPromotionTiming = ((Constraint.ONE * Constraint.THIRTY_SIX_HUNDRED)) * Constraint.THOUSAND;

    public int checkInversionTiming = (Constraint.THREE * Constraint.SIXTY) * Constraint.THOUSAND;

    public int updateApkTiming = ((Constraint.FOUR * Constraint.THIRTY_SIX_HUNDRED) + (Constraint.ONE * Constraint.SIXTY)) * Constraint.THOUSAND;

    public int validatePromotionTiming = ((Constraint.ONE * Constraint.THIRTY_SIX_HUNDRED) + (Constraint.THIRTY_INT * Constraint.SIXTY)) * Constraint.THOUSAND;

    public Timer deleteTimer = new Timer();
    public Timer logTimer = new Timer();
    public Timer updateTimer = new Timer();
    public Timer promotionTimer = new Timer();
    public Timer inversionTimer = new Timer();
    public Timer updateApkTimer = new Timer();
    public Timer validatePromotionTimer = new Timer();

    public void setUpdateTiming() {

        SessionManager sessionManager = SessionManager.get();
        Time time = sessionManager.getTimeData();
        int hour = Constraint.FOUR;
        int minit = Constraint.ZERO;
        if (time != null) {
            hour = time.getHour();
            minit = time.getMinit();
        }
        checkUpdateTiming = ((hour * Constraint.THIRTY_SIX_HUNDRED) + (minit * Constraint.SIXTY)) * Constraint.THOUSAND;
    }


    public void updateTiming(UpdateTiming updateTiming) {


    }
}
