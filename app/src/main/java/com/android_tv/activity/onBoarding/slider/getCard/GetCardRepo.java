package com.android_tv.activity.onBoarding.slider.getCard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android_tv.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.android_tv.apiService.ApiService;
import com.android_tv.apiService.AppRetrofit;
import com.android_tv.utils.Constraint;
import com.android_tv.pojo.response.GlobalResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Purpose -  GetCardRepo is an class that call getCard api for get new card details
 * Responsibility - Its call getCard api and save response to live data
 **/
public class GetCardRepo {
    private MutableLiveData<GlobalResponse<GetCardResponse>> liveData=new MutableLiveData<>();
    private ApiService apiService;


    /**
     * Responsibility - getCard method is used for card response and save it to live data
     * Parameters - Its takes HashMap<String, String> object as parameter
     **/
    public LiveData<GlobalResponse<GetCardResponse>> getCard(HashMap<String, String> input) {
        apiService= AppRetrofit.getInstance().getApiService();
        Call<GlobalResponse<GetCardResponse>> globalResponseCall=apiService.getCard(input,input.get(Constraint.TOKEN));
        globalResponseCall.enqueue(new Callback<GlobalResponse<GetCardResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<GetCardResponse>> call, Response<GlobalResponse<GetCardResponse>> response) {
                if (response.isSuccessful())
                {
                    liveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse<GetCardResponse>> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        return liveData;
    }
}
