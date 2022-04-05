package com.nzmdm.daisy.activity.onBoarding.slider.slides.addScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nzmdm.daisy.apiService.ApiService;
import com.nzmdm.daisy.apiService.AppRetrofit;
import com.nzmdm.daisy.pojo.response.GeneralResponse;
import com.nzmdm.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Purpose -  AddScreenRepo is an class  that addScreen api and handle response
 * Responsibility - Its takes hashmap as request and call add Screen api and save its response to live data
 **/
public class AddScreenRepo {
    private ApiService apiService;
    private MutableLiveData<GlobalResponse<GeneralResponse>> generalResponseMutableLiveData=new MutableLiveData<>();

    //  getGeneralResponse method is used for get general api response
    public LiveData<GlobalResponse<GeneralResponse>> getGeneralResponse(HashMap<String, String> input) {
        apiService= AppRetrofit.getInstance().getApiService();
        Call<GlobalResponse<GeneralResponse>> call=apiService.getGeneralResponse(input);
        call.enqueue(new Callback<GlobalResponse<GeneralResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<GeneralResponse>> call, Response<GlobalResponse<GeneralResponse>> response) {
                generalResponseMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<GlobalResponse<GeneralResponse>> call, Throwable t) {
                generalResponseMutableLiveData.setValue(null);
            }
        });
        return generalResponseMutableLiveData;
    }
}
