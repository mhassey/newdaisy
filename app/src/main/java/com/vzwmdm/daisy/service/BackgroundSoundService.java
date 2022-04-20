package com.vzwmdm.daisy.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.vzwmdm.daisy.R;
import com.vzwmdm.daisy.common.session.SessionManager;
import com.vzwmdm.daisy.security.Admin;
import com.vzwmdm.daisy.utils.Constraint;

import java.util.Timer;
import java.util.TimerTask;

/**
 * BackgroundSoundService is use to play sound in background and disable sound customization
 */
public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    private MediaPlayer player;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    Timer soundIncreseTimer;

    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (SessionManager.get().getAlaramSecurity()) {
            try {
                if (player != null) {
                    if (player.isPlaying()) {
                        return;
                    }
                }
                player = MediaPlayer.create(this, R.raw.ami);

                player.setLooping(true); // Set looping
                player.setVolume(Constraint.HUNDERD, Constraint.HUNDERD);
                soundIncreseTimer = new Timer();
                AudioManager audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        soundIncreseTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                float percent = 0.8f;
                                int seventyVolume = (int) (maxVolume * percent);
                                int sb2value = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, Constraint.ZERO);
                            }
                        }, Constraint.THOUSAND, Constraint.THOUSAND);

                    }
                }).start();
                if (player != null) {
                    if (player.isPlaying()) {

                    } else {

                        player.start();
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            SessionManager.get().deviceSecuried(false);
            try {
                mDPM = (DevicePolicyManager) getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
                mAdminName = new ComponentName(getApplicationContext(), Admin.class);
                KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                if (keyguardManager.isKeyguardSecure()) {
                    //it is password protected
                } else {
                    //it is not password protected
                    mDPM.resetPassword(SessionManager.get().getPasswordLock(), DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);

                }
                if (keyguardManager.inKeyguardRestrictedInputMode()) {
                    //it is locked
                } else {
                    mDPM.lockNow();
                }

            } catch (Exception e) {

            }


        } catch (Exception e) {

        }


        return START_STICKY;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }

    @Override
    public void onDestroy() {
        try {
            if (player.isPlaying()) {
                soundIncreseTimer.cancel();
                player.stop();
                player.release();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onLowMemory() {

    }
}