package com.daisy.activity.apkUpdate;

import androidx.lifecycle.GeneratedAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApkUpdateRepo {
    private ApiService apiService;
    private MutableLiveData<GlobalResponse<GeneralResponse>> globalResponseMutableLiveData=new MutableLiveData<>();
    public ApkUpdateRepo()
    {
        apiService=AppRetrofit.getInstance().getApiService();
    }
    public  LiveData<GlobalResponse<GeneralResponse>> getUpdate(HashMap<String, String> input) {
        Call<GlobalResponse<GeneralResponse>>  liveDataCall=apiService.getGeneralResponse(input);
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
