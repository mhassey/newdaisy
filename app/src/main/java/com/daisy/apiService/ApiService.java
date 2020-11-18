package com.daisy.apiService;

import androidx.lifecycle.LiveData;

import com.daisy.activity.onBoarding.slider.deviceDetection.vo.DeviceDetectRequest;
import com.daisy.activity.onBoarding.slider.deviceDetection.vo.DeviceDetectResponse;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.activity.onBoarding.slider.screenAdd.vo.ScreenAddResponse;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.daisy.activity.updatePosition.vo.UpdatePositionResponse;
import com.daisy.pojo.response.BlankResponse;
import com.daisy.pojo.response.DeleteCardResponse;
import com.daisy.pojo.response.FeedBackResponse;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Promotion;
import com.daisy.pojo.response.ValidatePromotionPojo;
import com.daisy.utils.Constraint;

import java.io.File;
import java.util.HashMap;
import java.util.List;

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

    @FormUrlEncoded
    @POST(ApiConstant.DELETE_CARD)
    Call<GlobalResponse<DeleteCardResponse>> deleteCard(@FieldMap  HashMap<String, String> hashMap,@Header(Constraint.TOKEN) String s);

    @FormUrlEncoded
    @POST(ApiConstant.API+ Constraint.SLASH +ApiConstant.PROMOTION_CHECK)
    Call<GlobalResponse<ValidatePromotionPojo>> checkPromotion(@FieldMap  HashMap<String, String> hashMap, @Header(Constraint.TOKEN) String s);

    @FormUrlEncoded
    @POST(ApiConstant.ADD_FEEDBACK)
    Call<LiveData<GlobalResponse<FeedBackResponse>>> addFeedBack(@FieldMap HashMap<String, String> feedBackRequest,@Header(Constraint.TOKEN) String s);

    @FormUrlEncoded
    @POST(ApiConstant.ADD_FEEDBACK)
    Call<GlobalResponse<GeneralResponse>> updateApk(HashMap<String, String> input, String s);
}
