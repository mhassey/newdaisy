package com.android_tv.support;


import android.util.Log;

import com.android_tv.apiService.ApiService;
import com.android_tv.apiService.AppRetrofit;
import com.android_tv.common.session.SessionManager;
import com.android_tv.pojo.response.GlobalResponse;
import com.android_tv.pojo.response.PushUpdateResponse;
import com.android_tv.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushUpdate {
    private SessionManager sessionManager;

    public void pushUpdate(String value) {
        sessionManager = SessionManager.get();
        new Thread(new Runnable() {
            @Override
            public void run() {
                updatePromotion(value);
            }
        }).start();
    }

    private void updatePromotion(String pushType) {
        ApiService apiService = AppRetrofit.getInstance().getApiService();
        HashMap<String, String> hashMap = getPushRequest(pushType);
        Call<GlobalResponse<PushUpdateResponse>> globalResponseCall = apiService.updatePush(hashMap, hashMap.get(Constraint.TOKEN));
        globalResponseCall.enqueue(new Callback<GlobalResponse<PushUpdateResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<PushUpdateResponse>> call, Response<GlobalResponse<PushUpdateResponse>> response) {
                Log.e("Push", "Update successfully");
            }

            @Override
            public void onFailure(Call<GlobalResponse<PushUpdateResponse>> call, Throwable t) {
                Log.e("Push", "Update fail");

                t.printStackTrace();

            }
        });
    }

    private HashMap<String, String> getPushRequest(String pushType) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.PUSH_TYPE, pushType);
        hashMap.put(Constraint.TOKEN, SessionManager.get().getDeviceToken());
        return hashMap;

    }

}
