package com.daisy.apiService;

import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.activity.onBoarding.slider.screenAdd.vo.ScreenAddResponse;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.daisy.activity.onBoarding.slider.deviceDetection.vo.DeviceDetectRequest;
import com.daisy.activity.onBoarding.slider.deviceDetection.vo.DeviceDetectResponse;
import com.daisy.activity.updatePosition.vo.UpdatePositionResponse;
import com.daisy.utils.Constraint;
import com.daisy.pojo.response.BlankResponse;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST(ApiConstant.SIGN_UP)
    Call<SignUpResponse> signUp(@FieldMap HashMap<String,String> password);

    @POST(ApiConstant.DETECT_DEVICE)
    Call<DeviceDetectResponse> detectDevice(@Body  DeviceDetectRequest input);

    @FormUrlEncoded
    @POST(ApiConstant.GENERAL)
    Call<GlobalResponse<GeneralResponse>> getGeneralResponse(@FieldMap HashMap<String, String> input);

    @FormUrlEncoded
    @POST(ApiConstant.CREATE_SCREEN)
    Call<GlobalResponse<ScreenAddResponse>> addScreen(@FieldMap HashMap<String, String> input);

    @FormUrlEncoded
    @POST(ApiConstant.GET_CARD)
    Call<GlobalResponse<GetCardResponse>> getCard(@FieldMap HashMap<String, String> input, @Header(Constraint.TOKEN) String token);

    @FormUrlEncoded
    @POST(ApiConstant.SEND_LOGS)
    Call<GlobalResponse<BlankResponse>> sendLogs(@FieldMap HashMap<String,String> request , @Header(Constraint.TOKEN) String token);
    @FormUrlEncoded
    @POST(ApiConstant.UPDATE_POSITION)
    Call<GlobalResponse<UpdatePositionResponse>> updatePosition(@FieldMap HashMap<String, String> input,@Header(Constraint.TOKEN) String s);
}
