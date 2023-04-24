package com.daisy.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.daisy.activity.validatePromotion.ValidatePromotion;
import com.daisy.checkCardAvailability.CheckCardAvailability;
import com.daisy.utils.Constraint;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Random;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
//            String type = Constraint.GET_CARDS;

            String type = new JSONObject(remoteMessage.getData()).getString("type");
            int count = new JSONObject(remoteMessage.getData()).getInt("total_notification_count");

            String type_notification = new JSONObject(remoteMessage.getData()).getString("push_type");

            Random random = new Random(count);

            if (type.equals(Constraint.VALIDATE_PROMOTION)) {
//                if (type_notification.equals(Constraint.PROMOTION_UPDATE))
//                    updateCard(random.nextInt(), type_notification);


                ValidatePromotion(random.nextInt(), type_notification);

            } else if (type.equals(Constraint.GET_CARDS)) {
                updateCard(random.nextInt(), type_notification);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        super.onMessageReceived(remoteMessage);
    }

    /**
     * Purpose - Check for apk update availability
     */
    public static void ValidatePromotion(int sec, String type) {
        try {


            final Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ValidatePromotion validatePromotion = new ValidatePromotion();
                    validatePromotion.checkPromotion(type);
                }
            }, ((long) sec * Constraint.THOUSAND));


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Purpose - Check for apk update availability
     */
    public static void updateCard(int sec, String type) {
        try {
            final Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    CheckCardAvailability validatePromotion = new CheckCardAvailability();
                    validatePromotion.checkCardForPush(type);
                }
            }, ((long) sec * Constraint.THOUSAND));


        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
