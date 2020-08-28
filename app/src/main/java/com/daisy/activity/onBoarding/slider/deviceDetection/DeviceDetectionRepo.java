package com.daisy.activity.onBoarding.slider.deviceDetection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.activity.onBoarding.slider.deviceDetection.vo.DeviceDetectRequest;
import com.daisy.activity.onBoarding.slider.deviceDetection.vo.DeviceDetectResponse;
import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceDetectionRepo {
    private MutableLiveData<DeviceDetectResponse> responseLiveData=new MutableLiveData<>();
    private ApiService apiService;
    public  DeviceDetectionRepo()
    {
        apiService= AppRetrofit.getInstance().getApiService();
    }

    public LiveData<DeviceDetectResponse> getDeviceData(DeviceDetectRequest input) {
        Call<DeviceDetectResponse> call=apiService.detectDevice(input);
        call.enqueue(new Callback<DeviceDetectResponse>() {
            @Override
            public void onResponse(Call<DeviceDetectResponse> call, Response<DeviceDetectResponse> response) {
                if (response.isSuccessful())
                {
                    responseLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<DeviceDetectResponse> call, Throwable t) {
                responseLiveData.setValue(null);
            }
        });
        return responseLiveData;
    }
}
