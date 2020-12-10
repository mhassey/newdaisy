package com.daisy.security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.daisy.R;
import com.daisy.service.BackgroundSoundService;
//import com.daisy.service.SecurityService;

public class GeoFenceBroadCastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("fired","broad cast");
         context.startService(new Intent(context, BackgroundSoundService.class));
    }
}
