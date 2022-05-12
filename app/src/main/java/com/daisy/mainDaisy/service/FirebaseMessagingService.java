package com.daisy.mainDaisy.service;

import androidx.annotation.NonNull;

import com.daisy.mainDaisy.checkCardAvailability.CheckCardAvailability;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
       // CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
       // checkCardAvailability.checkCard();
        super.onMessageReceived(remoteMessage);
    }
}
