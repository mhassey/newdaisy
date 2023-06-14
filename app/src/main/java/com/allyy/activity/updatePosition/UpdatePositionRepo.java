package com.allyy.activity.updatePosition;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.allyy.activity.updatePosition.vo.UpdatePositionResponse;
import com.allyy.apiService.ApiService;
import com.allyy.apiService.AppRetrofit;
import com.allyy.pojo.response.GlobalResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Purpose -  UpdatePositionRepo is an class that calls update position api for change position of phone
 * Responsibility - Its fire update position api using request and set response to live data
 **/
public class UpdatePositionRepo {

    private ApiService apiService;
    private MutableLiveData<GlobalResponse<UpdatePositionResponse>> liveData = new MutableLiveData<>();

    public UpdatePositionRepo() {

    }

    /**
     * Responsibility - update position method fire updatePosition api and if response is successful then set value to live data else set null on live data
     * Parameters - Its take HashMap<String, String> and string header value
     **/
    public LiveData<GlobalResponse<UpdatePositionResponse>> updatePosition(HashMap<String, String> input, String s) {
        apiService = AppRetrofit.getInstance().getApiService();
        Call<GlobalResponse<UpdatePositionResponse>> call = apiService.updatePosition(input, s);
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
