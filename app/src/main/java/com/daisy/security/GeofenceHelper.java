package com.daisy.security;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
//import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {
    private PendingIntent pendingIntent;
    public GeofenceHelper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence)
    {
        return new GeofencingRequest.Builder().addGeofence(geofence).setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT).build();
    }
//    public Geofence getGeoFence(String ID, LatLng latLng, float radius , int transitionType)
//    {
//        return new Geofence.Builder().setCircularRegion(latLng.latitude,latLng.longitude,radius).setRequestId(ID).setTransitionTypes(transitionType).setLoiteringDelay(1000).setExpirationDuration(Geofence.NEVER_EXPIRE).build();
//    }
    public PendingIntent getPendingIntent()
    {
        if (pendingIntent!=null)
        {
            return pendingIntent;
        }
        Intent intent=new Intent(this,GeoFenceBroadCastReciever.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this,2607,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

}
