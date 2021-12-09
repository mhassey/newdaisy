package com.daisy.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.app.AppController;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.pojo.EventHandler;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;

import org.apache.http.client.params.ClientPNames;
import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LogGenerateService extends Service {
    int seconds = 0;
    int currentTime = 0;
    int isCameraOpenOnce=0;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("increse counter", "command start");

        startCounterforLogs();
        return super.onStartCommand(intent, flags, startId);

    }

    private void startCounterforLogs() {
        try {

            Timer T = new Timer();
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Log.e("increse counter", currentTime + "");
                    currentTime++;
                    // if (!SessionManager.get().pickDOwn() || SessionManager.get().clickPerform()) {
                    if (SessionManager.get().clickPerform()) {

                        Log.e("increse counter", "second become 0");

                        SessionManager.get().pickDown(true);
                        SessionManager.get().clckPerform(false);
                        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                        ComponentName componentInfo = taskInfo.get(0).topActivity;
                        String name = componentInfo.getClassName();
                        if (isCameraOpenOnce==0) {
                            isCameraOpenOnce++;
                            openCameraApp();
                        }
                        seconds = 0;
                    } else {
                        Log.e("increse counter", "second increse");

                        seconds++;
                    }
                    if (seconds >= 16) {
                        Log.e("increse counter", "second become 16");

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
//                        EventHandler eventHandler = new EventHandler();
//                        eventHandler.eventName(Constraint.PICK_DOWN);
//                        EventBus.getDefault().post(eventHandler);
                        showOverlayActivity(getApplicationContext());
                        isCameraOpenOnce= 0;
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

    private void openCameraApp() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            PackageManager pm = getPackageManager();

            final ResolveInfo mInfo = pm.resolveActivity(i, 0);

            Intent intent = new Intent();
            intent.setComponent(new ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
            Log.i("TAG", "Unable to launch camera: " + e);
        }

    }


    //  Open Main as Overlay on lock screen
    private void showOverlayActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
