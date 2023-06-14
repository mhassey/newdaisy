package com.allyy.apiService;

import com.allyy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.allyy.activity.onBoarding.slider.screenAdd.vo.ScreenAddResponse;
import com.allyy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.allyy.activity.updatePosition.vo.UpdatePositionResponse;
import com.allyy.pojo.response.BlankResponse;
import com.allyy.pojo.response.DeleteCardResponse;
import com.allyy.pojo.response.FeedBackResponse;
import com.allyy.pojo.response.GeneralResponse;
import com.allyy.pojo.response.GlobalResponse;
import com.allyy.pojo.response.KeyToUrlResponse;
import com.allyy.pojo.response.PushUpdateResponse;
import com.allyy.pojo.response.UpdateTokenResponse;
import com.allyy.pojo.response.ValidatePromotionPojo;
import com.allyy.pojo.response.VersionUpdate;
import com.allyy.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
/**
 * This interface calls api and return result
 **/
public interface ApiService {

    @FormUrlEncoded
    @POST(ApiConstant.SIGN_UP)
    Call<SignUpResponse> signUp(@FieldMap HashMap<String,String> password);

    @FormUrlEncoded
    @POST(ApiConstant.GENERAL)
    Call<GlobalResponse<GeneralResponse>> getGeneralResponse(@FieldMap HashMap<String, String> input);

    @FormUrlEncoded
    @POST(ApiConstant.API + Constraint.SLASH + ApiConstant.UPDATE_PUSH_SCREEN)
    Call<GlobalResponse<PushUpdateResponse>> updatePush(@FieldMap HashMap<String, String> hashMap, @Header(Constraint.TOKEN) String s);

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
    Call<GlobalResponse<FeedBackResponse>> addFeedBack(@FieldMap HashMap<String, String> feedBackRequest,@Header(Constraint.TOKEN) String s);
    @FormUrlEncoded
    @POST(ApiConstant.CREATE_SCREEN_OS)
    Call<GlobalResponse<VersionUpdate>> createScreenOs(@FieldMap HashMap<String, String> hashMap, @Header(Constraint.TOKEN) String s);
    @FormUrlEncoded
    @POST(ApiConstant.UPDATE_PRODUCT)
    Call<GlobalResponse> updateProduct(@FieldMap HashMap<String, String> input,@Header(Constraint.TOKEN) String s);

    @FormUrlEncoded
    @POST(ApiConstant.KEY_TO_URL)
    Call<GlobalResponse<KeyToUrlResponse>> getKeyToValue(@FieldMap HashMap<String, String> input, @Header(Constraint.TOKEN) String s);

    @FormUrlEncoded
    @POST(ApiConstant.UPDATE_DEVICE_TOKEN)
    Call<GlobalResponse<UpdateTokenResponse>> updateDeviceToken(@FieldMap HashMap<String, String> hashCode, @Header(Constraint.TOKEN) String s);

}
