package com.daisy.apiService;

import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpRequest;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.daisy.activity.onBoarding.slider.vo.DeviceDetectRequest;
import com.daisy.activity.onBoarding.slider.vo.DeviceDetectResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST(ApiConstant.SIGN_UP)
    Call<SignUpResponse> signUp(@Body SignUpRequest jsonObject);

    @POST(ApiConstant.DETECT_DEVICE)
    Call<DeviceDetectResponse> detectDevice(@Body  DeviceDetectRequest input);
}
