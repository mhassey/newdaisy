package com.daisy.activity.validatePromotion;

import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.common.session.SessionManager;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Promotion;
import com.daisy.pojo.response.Promotions;
import com.daisy.pojo.response.ValidatePromotionPojo;
import com.daisy.utils.Constraint;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ValidatePromotion {
    private SessionManager sessionManager;

    public void checkPromotion() {
        sessionManager = SessionManager.get();
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkPromotions();
            }
        }).start();
    }

    private void checkPromotions() {
        ApiService apiService = AppRetrofit.getInstance().getApiService();
        HashMap<String, String> hashMap = getPromotionRequest();
        Call<GlobalResponse<ValidatePromotionPojo>> globalResponseCall = apiService.checkPromotion(hashMap, hashMap.get(Constraint.TOKEN));
        globalResponseCall.enqueue(new Callback<GlobalResponse<ValidatePromotionPojo>>() {
            @Override
            public void onResponse(Call<GlobalResponse<ValidatePromotionPojo>> call, Response<GlobalResponse<ValidatePromotionPojo>> response) {
                    handleResponse(response);
            }

            @Override
            public void onFailure(Call<GlobalResponse<ValidatePromotionPojo>> call, Throwable t) {
                t.printStackTrace();
                handleResponse(null);

            }
        });
    }

    private void handleResponse(Response<GlobalResponse<ValidatePromotionPojo>> liveData) {
        if (liveData != null) {
            if (liveData.isSuccessful()) {
                GlobalResponse<ValidatePromotionPojo> response = liveData.body();
                if (response.isApi_status()) {
                    if (response.getResult() != null ) {
                        JSONArray updatedPromotion = new JSONArray();
                        JSONArray promotions = sessionManager.getPromotions();
                        for (int i = 0; i < promotions.length(); i++) {

                            for (Promotion promotion : response.getResult().getPromotions()) {
                                try {
                                    JSONObject pro=(JSONObject) promotions.get(i);
                                    if (pro.get(Constraint.PROMOTION_ID).equals(promotion.getIdpromotion())) {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put(Constraint.PROMOTION, pro.getString(Constraint.PROMOTION));
                                        jsonObject.put(Constraint.PROMOTION_ID, promotion.getIdpromotion());
                                        jsonObject.put("dateCreated", promotion.getDateCreated());
                                        jsonObject.put("dateExpires", promotion.getDateExpires());

                                        updatedPromotion.put(jsonObject);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        sessionManager.deletePromotions();
                        sessionManager.setPromotions(updatedPromotion);
                        EventBus.getDefault().post(new Promotions());
                    } else {

                    }
                }

            }
        }
    }


    private HashMap<String, String> getPromotionRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        JSONArray promotions=sessionManager.getPromotions();
        String sendingId="";
        try {
            for (int i = 0; i < promotions.length(); i++) {
                JSONObject jsonObject =(JSONObject) promotions.get(i);
                sendingId += jsonObject.get(Constraint.PROMOTION_ID);
                if (!(i==(promotions.length()-1)))
                {
                    sendingId+=",";
                }
            }
        }
        catch (Exception e)
        {

        }
        hashMap.put(Constraint.PROMOTION_ID, sendingId);
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
         return hashMap;
    }
}
