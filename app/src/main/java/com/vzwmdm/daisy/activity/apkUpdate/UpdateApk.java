package com.vzwmdm.daisy.activity.apkUpdate;

import com.vzwmdm.daisy.BuildConfig;
import com.vzwmdm.daisy.apiService.ApiService;
import com.vzwmdm.daisy.apiService.AppRetrofit;
import com.vzwmdm.daisy.common.session.SessionManager;
import com.vzwmdm.daisy.pojo.response.ApkDetails;
import com.vzwmdm.daisy.pojo.response.GeneralResponse;
import com.vzwmdm.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Purpose -  UpdateApk class help us to know any update is available or not
 * Responsibility - Check Current version is less then server version if yes then tell app to further process
 **/
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

    /**
     * Responsibility - updateApk method call general api and  pass the output to handleResponse
     * Parameters - No parameter
     **/
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

    /**
     * Responsibility - handleResponse method checks current apk version is less then server apk version if yes then update the version on session
     * Parameters - Its take Response<GlobalResponse<GeneralResponse>> response that contains apk version json
     **/
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
                            try {
                                double apkVersion = Double.parseDouble(apkDetails.getAndroid().getVersion());
                                double ourVersion = Double.parseDouble(BuildConfig.VERSION_NAME);
                                if (apkVersion > ourVersion) {

                                    sessionManager.setApkVersion(BuildConfig.VERSION_NAME);
                                    sessionManager.setVersionDetails(apkDetails);
                                } else {
                                    sessionManager.setVersionDetails(null);
                                }
                            }
                            catch (Exception e)
                            {

                            }
                        }

                    }
                }

            }

        }
    }


}
