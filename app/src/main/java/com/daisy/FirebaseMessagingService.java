package com.daisy;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.daisy.mainDaisy.activity.validatePromotion.ValidatePromotion;
import com.daisy.mainDaisy.checkCardAvailability.CheckCardAvailability;
import com.daisy.mainDaisy.utils.Constraint;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            Map value = null;
            String type = new JSONObject(remoteMessage.getData()).getString("type");
            int count = new JSONObject(remoteMessage.getData()).getInt("total_notification_count");
            Random random = new Random(count);

            String type_notification = new JSONObject(remoteMessage.getData()).getString("push_type");
            try {
                value = CommonUtil.getCPUInfo();

                String hardware = (String) value.get(com.daisy.daisyGo.utils.Constraint.HARDWARE);
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    if (type.equals(Constraint.VALIDATE_PROMOTION)) {

                        ValidatePromotionForOptimalPermission(random.nextInt(), type_notification);


                    } else if (type.equals(Constraint.GET_CARDS)) {
                        getCardForOptimalPermission(random.nextInt(), type_notification);

                    }
                } else {
                    try {

                        if (hardware.toLowerCase().contains(com.daisy.daisyGo.utils.Constraint.UNISOC)) {
                            if (type.equals(Constraint.VALIDATE_PROMOTION)) {
                                ValidatePromotionForOptimalPermission(random.nextInt(), type_notification);


                            } else if (type.equals(Constraint.GET_CARDS)) {
                                getCardForOptimalPermission(random.nextInt(), type_notification);

                            }

                        } else if (CommonUtil.isSystemAlertWindowEnabled(this)) {
                            if (type.equals(Constraint.VALIDATE_PROMOTION)) {
                                ValidatePromotionForGoPermission(random.nextInt(), type_notification);


                            } else if (type.equals(Constraint.GET_CARDS)) {
                                getCardForGoPermission(random.nextInt(), type_notification);

                            }
                        } else {

                            if (type.equals(Constraint.VALIDATE_PROMOTION)) {

                                ValidatePromotionPermission(random.nextInt(), type_notification);


                            } else if (type.equals(Constraint.GET_CARDS)) {
                                getCardPermission(random.nextInt(), type_notification);

                            }
                        }
                    } catch (Exception e) {
                        if (type.equals(Constraint.VALIDATE_PROMOTION)) {

                            ValidatePromotionPermission(random.nextInt(), type_notification);


                        } else if (type.equals(Constraint.GET_CARDS)) {
                            getCardPermission(random.nextInt(), type_notification);

                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();

            }
        } catch (Exception e) {

        }
        super.onMessageReceived(remoteMessage);
    }

    private void ValidatePromotionForOptimalPermission(int sec, String type) {

        try {


            final Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    com.daisy.optimalPermission.activity.validatePromotion.ValidatePromotion validatePromotion = new com.daisy.optimalPermission.activity.validatePromotion.ValidatePromotion();
                    validatePromotion.checkPromotion();
                }
            }, ((long) sec * Constraint.THOUSAND));


        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private void getCardForOptimalPermission(int sec, String type) {

        try {


            final Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    com.daisy.optimalPermission.checkCardAvailability.CheckCardAvailability checkCardAvailability = new com.daisy.optimalPermission.checkCardAvailability.CheckCardAvailability();
                    checkCardAvailability.checkCard();
                }
            }, ((long) sec * Constraint.THOUSAND));


        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private void ValidatePromotionForGoPermission(int sec, String type) {

        try {


            final Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    com.daisy.daisyGo.activity.validatePromotion.ValidatePromotion validatePromotion = new com.daisy.daisyGo.activity.validatePromotion.ValidatePromotion();
                    validatePromotion.checkPromotion();
                }
            }, ((long) sec * Constraint.THOUSAND));


        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private void getCardForGoPermission(int sec, String type) {

        try {


            final Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    com.daisy.daisyGo.checkCardAvailability.CheckCardAvailability checkCardAvailability = new com.daisy.daisyGo.checkCardAvailability.CheckCardAvailability();
                    checkCardAvailability.checkCard();
                }
            }, ((long) sec * Constraint.THOUSAND));


        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private void ValidatePromotionPermission(int sec, String type) {

        try {


            final Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ValidatePromotion validatePromotion = new ValidatePromotion();
                    validatePromotion.checkPromotion();
                }
            }, ((long) sec * Constraint.THOUSAND));


        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private void getCardPermission(int sec, String type) {
        try {


            final Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
                    checkCardAvailability.checkCard();
                }
            }, ((long) sec * Constraint.THOUSAND));


        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}

