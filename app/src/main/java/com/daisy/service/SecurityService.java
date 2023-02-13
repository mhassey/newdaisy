package com.daisy.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import com.daisy.common.session.SessionManager;
import com.daisy.utils.ValidationHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class SecurityService extends Service {
    private SessionManager sessionManager;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Intent alarmIntent;
    int i = 0;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public SecurityService() {
        sessionManager = SessionManager.get();

    }

    @Override
    public void onCreate() {
        super.onCreate();

        alarmIntent = new Intent(getApplicationContext(), BackgroundSoundService.class);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (sessionManager != null) {
            sessionManager = SessionManager.get();
        }
        handleReciver();
        handleLocation();


        return START_NOT_STICKY;
    }

    private void handleLocation() {

//
//        LocationCallback locationListener = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                Location location = null;
//                for (Location location1 : locationResult.getLocations()) {
//                    location = location1;
//                }
//
//                String lat = sessionManager.getLatitude();
//                String lon = sessionManager.getLongitude();
//                if (lat.equals("")) {
//                    sessionManager.setLatitude(location.getLatitude() + "");
//                    sessionManager.setLongitude(location.getLongitude() + "");
//                } else {
//                    if (i < 10) {
//                        sessionManager.setLatitude(location.getLatitude() + "");
//                        sessionManager.setLongitude(location.getLongitude() + "");
//                        i++;
//                        return;
//                    }
//                    Location currentLocation = new Location("Point B");
//                    currentLocation.setLatitude(Double.parseDouble(lat));
//                    currentLocation.setLongitude(Double.parseDouble(lon));
//                    ValidationHelper.showToast(getApplicationContext(), "" + currentLocation.distanceTo(location));
//                    if (currentLocation.distanceTo(location) - location.getAccuracy() > 0) {
//
//                        double distance = currentLocation.distanceTo(location);
//                        if (distance > 20) {
                            if (sessionManager.getDeviceSecured()) {
                                startService(alarmIntent);
                            }
//                        }
//                    }
//                }
//
//            }
//        };
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//        locationRequest = LocationRequest.create();
//        locationRequest.setInterval(4000);
//        locationRequest.setFastestInterval(2000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationListener, Looper.myLooper());
    }

    //  Define receiver  for lock open
    private void handleReciver() {
        IntentFilter securityFilter = new IntentFilter("android.intent.action.USER_PRESENT");
        registerReceiver(PhoneUnlockedReceiver, securityFilter);

    }

    @Override
    public void onDestroy() {
        if (alarmIntent != null) {
            stopService(alarmIntent);
            stopSelf();
        }
        super.onDestroy();
    }

    private BroadcastReceiver PhoneUnlockedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if (keyguardManager.isKeyguardSecure()) {
                    if (alarmIntent != null) {
                        sessionManager.deviceSecuried(false);
                        stopService(alarmIntent);
                    }
                }
            }
            catch (Exception e)
            {

            }
        }
    };

}
