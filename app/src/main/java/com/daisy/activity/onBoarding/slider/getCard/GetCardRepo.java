package com.daisy.activity.onBoarding.slider.getCard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.utils.Constraint;
import com.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetCardRepo {
    private MutableLiveData<GlobalResponse<GetCardResponse>> liveData=new MutableLiveData<>();
    private ApiService apiService;
    public GetCardRepo()
    {
        apiService= AppRetrofit.getInstance().getApiService();
    }
    public LiveData<GlobalResponse<GetCardResponse>> getCard(HashMap<String, String> input) {

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
