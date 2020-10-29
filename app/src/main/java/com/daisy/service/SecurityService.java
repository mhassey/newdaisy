package com.daisy.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.daisy.common.session.SessionManager;
import com.daisy.security.GeofenceHelper;
import com.daisy.utils.ValidationHelper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class SecurityService extends Service {
    LocationManager locationManager;
    LocationListener locationListener;
    SessionManager sessionManager;
    private static GeofencingClient geofencingClient;
    private static GeofenceHelper geofenceHelper;
    int i=0;
    public SecurityService() {
        sessionManager=SessionManager.get();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
      return null;
         }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (sessionManager!=null)
        {
            sessionManager=SessionManager.get();
        }
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        geofencingClient= LocationServices.getGeofencingClient(this);
        geofenceHelper=new GeofenceHelper(this);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                ValidationHelper.showToast(getApplicationContext(),location.toString());
                String lat=sessionManager.getLatitude();
                String lon=sessionManager.getLongitude();
                if (lat.equals("")) {

                    ValidationHelper.showToast(getApplicationContext(),"inner");

                    sessionManager.setLatitude(location.getLatitude()+"");
                    sessionManager.setLongitude(location.getLongitude()+"");
                 //   LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
               //  addGeoFence(latLng,5);
                }
                else
                {
                    Location location1=new Location("Point B");
                    location1.setLatitude(Double.parseDouble(lat));
                    location1.setLongitude(Double.parseDouble(lon));

                    if (location1.distanceTo(location) - location.getAccuracy() >0){
                        double distance=location1.distanceTo(location);
                        if (distance>15)
                        {
                            ValidationHelper.showToast(getApplicationContext(),"5 meter Higher"+distance);
                        }
                        else
                        {
                            ValidationHelper.showToast(getApplicationContext(),"5 meter lower"+distance);

                        }
                    }

       //      double distance=       distance(Double.parseDouble(lat),Double.parseDouble(lon),location.getLatitude(),location.getLongitude());


                }

            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        return START_STICKY;
    }
//    private  static  void addGeoFence(LatLng latLng, float radius)
//    {
//        GeofencingRequest geofencingRequest=geofenceHelper.getGeofencingRequest(geofenceHelper.getGeoFence("SOME_GEO_FENCE",latLng,radius, Geofence.GEOFENCE_TRANSITION_EXIT));
//        PendingIntent pendingIntent=geofenceHelper.getPendingIntent();
//        geofencingClient.addGeofences(geofencingRequest,pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.e("sucess","perform");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//
//
//    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
