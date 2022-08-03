package com.daisy;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.daisy.mainDaisy.activity.validatePromotion.ValidatePromotion;
import com.daisy.mainDaisy.checkCardAvailability.CheckCardAvailability;
import com.daisy.mainDaisy.utils.Constraint;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            Map value = null;
            String type = new JSONObject(remoteMessage.getData()).getString("type");

            try {
                value = CommonUtil.getCPUInfo();

                String hardware = (String) value.get(com.daisy.daisyGo.utils.Constraint.HARDWARE);
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    if (type.equals(Constraint.VALIDATE_PROMOTION)) {

                        com.daisy.optimalPermission.activity.validatePromotion.ValidatePromotion validatePromotion = new com.daisy.optimalPermission.activity.validatePromotion.ValidatePromotion();
                        validatePromotion.checkPromotion();

                    } else if (type.equals(Constraint.GET_CARDS)) {
                        com.daisy.optimalPermission.checkCardAvailability.CheckCardAvailability checkCardAvailability = new com.daisy.optimalPermission.checkCardAvailability.CheckCardAvailability();
                        checkCardAvailability.checkCard();
                    }
                } else {
                    try {

                        if (hardware.toLowerCase().contains(com.daisy.daisyGo.utils.Constraint.UNISOC)) {
                            if (type.equals(Constraint.VALIDATE_PROMOTION)) {

                                com.daisy.optimalPermission.activity.validatePromotion.ValidatePromotion validatePromotion = new com.daisy.optimalPermission.activity.validatePromotion.ValidatePromotion();
                                validatePromotion.checkPromotion();

                            } else if (type.equals(Constraint.GET_CARDS)) {
                                com.daisy.optimalPermission.checkCardAvailability.CheckCardAvailability checkCardAvailability = new com.daisy.optimalPermission.checkCardAvailability.CheckCardAvailability();
                                checkCardAvailability.checkCard();
                            }

                        } else if (CommonUtil.isSystemAlertWindowEnabled(this)) {
                            if (type.equals(Constraint.VALIDATE_PROMOTION)) {

                                com.daisy.daisyGo.activity.validatePromotion.ValidatePromotion validatePromotion = new com.daisy.daisyGo.activity.validatePromotion.ValidatePromotion();
                                validatePromotion.checkPromotion();

                            } else if (type.equals(Constraint.GET_CARDS)) {
                                com.daisy.daisyGo.checkCardAvailability.CheckCardAvailability checkCardAvailability = new com.daisy.daisyGo.checkCardAvailability.CheckCardAvailability();
                                checkCardAvailability.checkCard();
                            }
                        } else {

                            if (type.equals(Constraint.VALIDATE_PROMOTION)) {

                                ValidatePromotion validatePromotion = new ValidatePromotion();
                                validatePromotion.checkPromotion();

                            } else if (type.equals(Constraint.GET_CARDS)) {
                                CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
                                checkCardAvailability.checkCard();
                            }
                        }
                    } catch (Exception e) {
                        if (type.equals(Constraint.VALIDATE_PROMOTION)) {

                            ValidatePromotion validatePromotion = new ValidatePromotion();
                            validatePromotion.checkPromotion();

                        } else if (type.equals(Constraint.GET_CARDS)) {
                            CheckCardAvailability checkCardAvailability = new CheckCardAvailability();
                            checkCardAvailability.checkCard();
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
}
