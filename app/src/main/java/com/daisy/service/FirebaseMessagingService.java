package com.daisy.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.daisy.activity.validatePromotion.ValidatePromotion;
import com.daisy.checkCardAvailability.CheckCardAvailability;
import com.daisy.utils.Constraint;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            String type = new JSONObject(remoteMessage.getData()).getString("type");

            if (type.equals(Constraint.VALIDATE_PROMOTION)) {
                ValidatePromotion validatePromotion = new ValidatePromotion();
                validatePromotion.checkPromotion();

            } else if (type.equals(Constraint.GET_CARDS)) {
                CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
                checkCardAvailability.checkCard();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        super.onMessageReceived(remoteMessage);
    }
}
