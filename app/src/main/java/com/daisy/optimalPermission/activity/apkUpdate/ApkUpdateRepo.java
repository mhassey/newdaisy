package com.daisy.optimalPermission.activity.apkUpdate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.optimalPermission.apiService.ApiService;
import com.daisy.optimalPermission.apiService.AppRetrofit;
import com.daisy.optimalPermission.pojo.response.GeneralResponse;
import com.daisy.optimalPermission.pojo.response.GlobalResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
* Purpose -  ApkUpdateRepo is used for check any update is available
* Responsibility - ApkUpdateRepo takes request  and get response using general api and pass the response to view models
**/
public class ApkUpdateRepo {

    private ApiService apiService;
    private MutableLiveData<GlobalResponse<GeneralResponse>> globalResponseMutableLiveData = new MutableLiveData<>();

    public ApkUpdateRepo() {
        apiService = AppRetrofit.getInstance().getApiService();
    }


    /**
     * Responsibility - getUpdate method is used for getting the update from server using general api and return  LiveData<GlobalResponse<GeneralResponse>> as output
     * Parameters - Its takes HashMap<String,String> object
     **/
    public LiveData<GlobalResponse<GeneralResponse>> getUpdate(HashMap<String, String> input) {
        Call<GlobalResponse<GeneralResponse>> liveDataCall = apiService.getGeneralResponse(input);
        liveDataCall.enqueue(new Callback<GlobalResponse<GeneralResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<GeneralResponse>> call, Response<GlobalResponse<GeneralResponse>> response) {
                if (response.isSuccessful())
                    globalResponseMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<GlobalResponse<GeneralResponse>> call, Throwable t) {
                globalResponseMutableLiveData.setValue(null);
            }
        });
        return globalResponseMutableLiveData;
    }
}
