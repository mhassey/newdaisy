package com.iris.support;

import android.util.Log;

import com.iris.apiService.ApiService;
import com.iris.apiService.AppRetrofit;
import com.iris.common.session.SessionManager;
import com.iris.pojo.response.GlobalResponse;
import com.iris.pojo.response.PushUpdateResponse;
import com.iris.utils.Constraint;

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
