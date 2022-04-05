package com.nzmdm.daisy.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
       // CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
       // checkCardAvailability.checkCard();
        super.onMessageReceived(remoteMessage);
    }
}
