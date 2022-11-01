package com.iris.activity.onBoarding.slider.slides.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iris.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.iris.apiService.ApiService;
import com.iris.apiService.AppRetrofit;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Purpose - SignUpRepo class helps to fire sign up api and handle its response
 */
public class SignUpRepo {
    private MutableLiveData<SignUpResponse> liveData=new MutableLiveData<>();
    private ApiService apiService;
    public SignUpRepo()
    {
        apiService= AppRetrofit.getInstance().getApiService();
    }
    public LiveData<SignUpResponse> signUp(HashMap<String,String> input) {
         Call<SignUpResponse> call=apiService.signUp(input);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful())
                {
                    liveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                t.printStackTrace();
            liveData.setValue(null);
            }
        });
        return liveData;
    }
}
