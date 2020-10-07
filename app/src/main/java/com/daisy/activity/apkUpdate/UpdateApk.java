package com.daisy.activity.apkUpdate;

import android.util.Log;

import com.daisy.BuildConfig;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.common.session.SessionManager;
import com.daisy.pojo.response.ApkDetails;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateApk {
    private SessionManager sessionManager;

    public void UpdateApk() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                updateApk();
            }
        }).start();
    }

    private void updateApk() {
        ApiService apiService = AppRetrofit.getInstance().getApiService();
        HashMap<String, String> hashMap = new HashMap<>();
        Call<GlobalResponse<GeneralResponse>> globalResponseCall = apiService.getGeneralResponse(hashMap);
        globalResponseCall.enqueue(new Callback<GlobalResponse<GeneralResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<GeneralResponse>> call, Response<GlobalResponse<GeneralResponse>> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(Call<GlobalResponse<GeneralResponse>> call, Throwable t) {
                handleResponse(null);
            }
        });


    }

    private void handleResponse(Response<GlobalResponse<GeneralResponse>> response) {
        if (response != null) {
            if (response.isSuccessful()) {
                GlobalResponse<GeneralResponse> globalResponse = response.body();
                if (globalResponse.isApi_status()) {
                    ApkDetails apkDetails = globalResponse.getResult().getApkDetails();
                    if (apkDetails != null) {
                        if (apkDetails.getAndroid().getVersion() != null) {
                            if (sessionManager == null)
                                sessionManager = SessionManager.get();
                                double apkVersion=Double.parseDouble(apkDetails.getAndroid().getVersion());
                                double ourVersion=Double.parseDouble(BuildConfig.VERSION_NAME);
                            if (apkVersion>ourVersion) {


                                sessionManager.setVersionDetails(apkDetails);
                                EventBus.getDefault().post(apkDetails);
                            }
                            else
                            {
                                sessionManager.setVersionDetails(null);
                            }
                        }

                    }
                }

            }

        }
    }


}
