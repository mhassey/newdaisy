package com.daisy.activity.onBoarding.slider.screenAdd;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.activity.onBoarding.slider.screenAdd.vo.ScreenAddResponse;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Purpose -  ScreenAddRepo is an class that fire addScreen api and get response
 * Responsibility - Its takes request data from view model and fire addScreen api and save response in live data
 **/
public class ScreenAddRepo {

    private ApiService apiService;
    private MutableLiveData<GlobalResponse<ScreenAddResponse>> liveData=new MutableLiveData<>();

    public ScreenAddRepo()
    {

    }

    //  addScreen method is used for add new Screen on server
    public LiveData<GlobalResponse<ScreenAddResponse>> addScreen(HashMap<String,String> input) {
        apiService= AppRetrofit.getInstance().getApiService();
        Call<GlobalResponse<ScreenAddResponse>> call=apiService.addScreen(input);
        call.enqueue(new Callback<GlobalResponse<ScreenAddResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<ScreenAddResponse>> call, Response<GlobalResponse<ScreenAddResponse>> response) {
                if (response.isSuccessful())
                {
                    liveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse<ScreenAddResponse>> call, Throwable t) {
            liveData.setValue(null);
            }
        });
        return liveData;
    }
}
