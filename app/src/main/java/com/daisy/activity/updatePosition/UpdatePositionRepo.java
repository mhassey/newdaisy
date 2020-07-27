package com.daisy.activity.updatePosition;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.activity.updatePosition.vo.UpdatePositionResponse;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePositionRepo {
    private ApiService apiService;
    public UpdatePositionRepo()
    {
        apiService= AppRetrofit.getInstance().getApiService();
    }
    private MutableLiveData<GlobalResponse<UpdatePositionResponse>> liveData=new MutableLiveData<>();
    public LiveData<GlobalResponse<UpdatePositionResponse>> updatePosition(HashMap<String, String> input, String s) {
        Call<GlobalResponse<UpdatePositionResponse>> call= apiService.updatePosition(input,s);
        call.enqueue(new Callback<GlobalResponse<UpdatePositionResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<UpdatePositionResponse>> call, Response<GlobalResponse<UpdatePositionResponse>> response) {
              if (response.isSuccessful())
                liveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<GlobalResponse<UpdatePositionResponse>> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        return liveData;
    }
}
