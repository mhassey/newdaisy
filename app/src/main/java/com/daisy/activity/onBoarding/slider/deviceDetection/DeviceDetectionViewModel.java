package com.daisy.activity.onBoarding.slider.deviceDetection;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.activity.onBoarding.slider.deviceDetection.vo.DeviceDetectRequest;
import com.daisy.activity.onBoarding.slider.deviceDetection.vo.DeviceDetectResponse;

public class DeviceDetectionViewModel extends AndroidViewModel {
    private MutableLiveData<DeviceDetectRequest> detectRequestMutableLiveData = new MutableLiveData<>();
    private LiveData<DeviceDetectResponse> responseLiveData;
    private DeviceDetectionRepo deviceDetectionRepo=new DeviceDetectionRepo();

    public DeviceDetectionViewModel(@NonNull Application application) {
        super(application);
        responseLiveData = Transformations.switchMap(detectRequestMutableLiveData, new Function<DeviceDetectRequest, LiveData<DeviceDetectResponse>>() {
            @Override
            public LiveData<DeviceDetectResponse> apply(DeviceDetectRequest input) {
                return deviceDetectionRepo.getDeviceData(input);
            }
        });
    }

    public void setDetectRequestMutableLiveData(DeviceDetectRequest request) {
        detectRequestMutableLiveData.setValue(request);
    }

    public LiveData<DeviceDetectResponse> getResponseLiveData() {
        return responseLiveData;
    }
}
