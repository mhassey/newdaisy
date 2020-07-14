package com.daisy.activity.onBoarding.slider.slides.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpRequest;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpRepo {
    private MutableLiveData<SignUpResponse> liveData=new MutableLiveData<>();
    private ApiService apiService;
    public SignUpRepo()
    {
        apiService= AppRetrofit.getInstance().getApiService();
    }
    public LiveData<SignUpResponse> signUp(SignUpRequest input) {
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
            liveData.setValue(null);
            }
        });
        return liveData;
    }
}
