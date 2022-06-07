package com.daisy.mainDaisy.activity.updatetoken;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.mainDaisy.apiService.ApiService;
import com.daisy.mainDaisy.apiService.AppRetrofit;
import com.daisy.mainDaisy.pojo.response.GlobalResponse;
import com.daisy.mainDaisy.pojo.response.UpdateTokenResponse;
import com.daisy.mainDaisy.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTokenRepo {
    private ApiService apiService;
    private MutableLiveData<GlobalResponse<UpdateTokenResponse>> responseToTransfer = new MutableLiveData<>();

    public LiveData<GlobalResponse<UpdateTokenResponse>> updateToken(HashMap<String, String> hashCode) {
        apiService = AppRetrofit.getInstance().getApiService();
        Call<GlobalResponse<UpdateTokenResponse>> call = apiService.updateDeviceToken(hashCode, hashCode.get(Constraint.TOKEN));
        call.enqueue(new Callback<GlobalResponse<UpdateTokenResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<UpdateTokenResponse>> call, Response<GlobalResponse<UpdateTokenResponse>> response) {
                if (response.isSuccessful()) {
                    responseToTransfer.setValue(response.body());
                } else {
                    responseToTransfer.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse<UpdateTokenResponse>> call, Throwable t) {
                responseToTransfer.setValue(null);
            }
        });

        return responseToTransfer;

    }
}
