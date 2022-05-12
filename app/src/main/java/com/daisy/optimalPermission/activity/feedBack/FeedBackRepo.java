package com.daisy.optimalPermission.activity.feedBack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.optimalPermission.apiService.ApiService;
import com.daisy.optimalPermission.apiService.AppRetrofit;
import com.daisy.optimalPermission.pojo.response.FeedBackResponse;
import com.daisy.optimalPermission.pojo.response.GlobalResponse;
import com.daisy.optimalPermission.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedBackRepo {
    ApiService apiService;

    public FeedBackRepo() {
        apiService = AppRetrofit.getInstance().getApiService();
    }

    private MutableLiveData<GlobalResponse<FeedBackResponse>> feedBackResponseMutableLiveData = new MutableLiveData<>();

    public LiveData<GlobalResponse<FeedBackResponse>> getFeedBackResponse(HashMap<String, String> feedBackRequest) {
        Call<GlobalResponse<FeedBackResponse>> call = apiService.addFeedBack(feedBackRequest, feedBackRequest.get(Constraint.TOKEN));
        call.enqueue(new Callback<GlobalResponse<FeedBackResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<FeedBackResponse>> call, Response<GlobalResponse<FeedBackResponse>> response) {
                if (response.isSuccessful()) {
                    feedBackResponseMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse<FeedBackResponse>> call, Throwable t) {
                feedBackResponseMutableLiveData.setValue(null);
            }
        });
        return feedBackResponseMutableLiveData;
    }
}
