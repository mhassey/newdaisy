package com.daisy.mainDaisy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.daisy.mainDaisy.app.AppController;
import com.daisy.mainDaisy.common.session.SessionManager;
import com.daisy.mainDaisy.database.DBCaller;
import com.daisy.mainDaisy.utils.Constraint;
import com.daisy.mainDaisy.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LogGenerateService extends Service {
    int seconds = 0;
    int currentTime = 0;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startCounterforLogs();
        return super.onStartCommand(intent, flags, startId);

    }

    private void startCounterforLogs() {
        try {

            Timer T = new Timer();
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    currentTime++;
                    if (SessionManager.get().clickPerform()) {
                        SessionManager.get().pickDown(true);
                        SessionManager.get().clckPerform(false);
                        seconds = 0;
                    } else {
                        seconds++;
                    }
                    if (seconds >= 16) {
                        int day = (int) TimeUnit.SECONDS.toDays(currentTime);
                        long hours = TimeUnit.SECONDS.toHours(currentTime) - (day * 24);
                        String hours_string = "";
                        if (hours <= 9) {
                            hours_string = "0" + hours;
                        } else
                            hours_string = hours + "";

                        long minute = TimeUnit.SECONDS.toMinutes(currentTime) - (TimeUnit.SECONDS.toHours(currentTime) * 60);
                        String minute_string = "";
                        if (minute <= 9) {
                            minute_string = "0" + minute;
                        } else
                            minute_string = minute + "";

                        long second = TimeUnit.SECONDS.toSeconds(currentTime) - (TimeUnit.SECONDS.toMinutes(currentTime) * 60);
                        String second_string = "";
                        if (second <= 9) {
                            second_string = "0" + second;
                        } else
                            second_string = second + "";

                        DBCaller.storeLogInDatabase(getApplicationContext(), Constraint.USER_INTERACTION + "/" + hours_string + Constraint.COLON + minute_string + Constraint.COLON + second_string, "", "", Constraint.APPLICATION_LOGS);
                        T.cancel();
                        Intent lintent = new Intent(getApplicationContext(), LogGenerateService.class);
                        stopService(lintent);


                    }
                }
            }, Constraint.THOUSAND, Constraint.THOUSAND);
        } catch (Exception e) {

        }
    }
}
