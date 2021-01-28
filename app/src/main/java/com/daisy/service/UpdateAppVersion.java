package com.daisy.service;

import android.os.Build;
import android.util.Log;

import com.daisy.BuildConfig;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.common.session.SessionManager;
import com.daisy.pojo.response.Android;
import com.daisy.pojo.response.ApkDetails;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.VersionUpdate;
import com.daisy.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAppVersion {
    private SessionManager sessionManager;

    public void UpdateAppVersion() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                updateAppVersion();
            }
        }).start();
    }

    // TODO Fire update app version api request
    private void updateAppVersion() {
        sessionManager = SessionManager.get();
        ApiService apiService = AppRetrofit.getInstance().getApiService();
        HashMap<String, String> hashMap = createRequest();

        Call<GlobalResponse<VersionUpdate>> globalResponseCall = apiService.createScreenOs(hashMap, hashMap.get(Constraint.TOKEN));
        globalResponseCall.enqueue(new Callback<GlobalResponse<VersionUpdate>>() {
            @Override
            public void onResponse(Call<GlobalResponse<VersionUpdate>> call, Response<GlobalResponse<VersionUpdate>> response) {
                if (response.isSuccessful()) {
                    GlobalResponse<VersionUpdate> versionUpdate = response.body();
                    if (versionUpdate.isApi_status()) {
                        if (versionUpdate.getResult().isId()) {

                            sessionManager.setApkVersion(BuildConfig.VERSION_NAME);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse<VersionUpdate>> call, Throwable t) {
            t.printStackTrace();
            }
        });

    }

    // TODO Create Update app version api request
    private HashMap<String, String> createRequest() {
        if (sessionManager == null)
            sessionManager = SessionManager.get();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.IDDEVICE, sessionManager.getDeviceId() + "");
        hashMap.put(Constraint.OS_ID, sessionManager.getOS_ID());

        hashMap.put(Constraint.OS_VER, Build.VERSION.RELEASE + "");
        hashMap.put(Constraint.MAV_ID, sessionManager.getMavID());
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }

}
