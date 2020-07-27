package com.daisy.activity.onBoarding.slider.slides.addScreen;

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

public class AddScreenRepo {
    private ApiService apiService;
    private MutableLiveData<GlobalResponse<GeneralResponse>> generalResponseMutableLiveData=new MutableLiveData<>();

    public AddScreenRepo()
    {
        apiService= AppRetrofit.getInstance().getApiService();
    }

    public LiveData<GlobalResponse<GeneralResponse>> getGenralResponse(HashMap<String, String> input) {
        Call<GlobalResponse<GeneralResponse>> call=apiService.getGeneralResponse(input);
        call.enqueue(new Callback<GlobalResponse<GeneralResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<GeneralResponse>> call, Response<GlobalResponse<GeneralResponse>> response) {
                generalResponseMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<GlobalResponse<GeneralResponse>> call, Throwable t) {

            }
        });
        return generalResponseMutableLiveData;
    }
}
