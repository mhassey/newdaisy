package com.daisy.General;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeneralModelRepo {

    private ApiService apiService;
    public GeneralModelRepo()
    {
        apiService= AppRetrofit.getInstance().getApiService();
    }
    private MutableLiveData<GlobalResponse<GeneralResponse>> liveData=new MutableLiveData<>();
    public LiveData<GlobalResponse<GeneralResponse>> getGeneralResponse(HashMap<String, String> input) {
        Call<GlobalResponse<GeneralResponse>> call=apiService.getGeneralResponse(input);
        call.enqueue(new Callback<GlobalResponse<GeneralResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<GeneralResponse>> call, Response<GlobalResponse<GeneralResponse>> response) {
                if (response.isSuccessful())
                {
                    liveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse<GeneralResponse>> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        return liveData;

    }
}
