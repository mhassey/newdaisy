package com.daisy.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.daisy.R;
import com.daisy.common.session.SessionManager;
import com.daisy.security.Admin;

public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    private MediaPlayer player;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    public IBinder onBind(Intent arg0) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.ami);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();

        mDPM = (DevicePolicyManager) getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(getApplicationContext(), Admin.class);
        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        if( keyguardManager.isKeyguardSecure()) {
            //it is password protected
        } else {
            //it is not password protected
            mDPM.resetPassword( SessionManager.get().getPasswordLock(), DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);

        }

        mDPM.lockNow();
        return START_STICKY;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }
    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }
    public void onPause() {

    }
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}