package com.daisy.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.daisy.checkCardAvailability.CheckCardAvailability;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//         CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
//        checkCardAvailability.checkCarFd();
        super.onMessageReceived(remoteMessage);
    }
}
