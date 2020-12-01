package com.daisy.activity.updateProduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.activity.onBoarding.slider.screenAdd.vo.ScreenAddResponse;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProductRepo {
    private MutableLiveData<GlobalResponse> mutableLiveData = new MutableLiveData<>();
    private ApiService apiService;

    UpdateProductRepo() {
        apiService = AppRetrofit.getInstance().getApiService();
    }

    public LiveData<GlobalResponse> updateScreen(HashMap<String, String> input) {
        Call<GlobalResponse> responseCall = apiService.updateProduct(input, input.get(Constraint.TOKEN));
        responseCall.enqueue(new Callback<GlobalResponse>() {
            @Override
            public void onResponse(Call<GlobalResponse> call, Response<GlobalResponse> response) {
                if (response.isSuccessful()) {
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse> call, Throwable t) {
                    mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }
}
