package com.daisy.activity.feedBack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.pojo.response.FeedBackResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.utils.Constraint;

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
        Call<LiveData<GlobalResponse<FeedBackResponse>>> call = apiService.addFeedBack(feedBackRequest, feedBackRequest.get(Constraint.TOKEN));
        call.enqueue(new Callback<LiveData<GlobalResponse<FeedBackResponse>>>() {
            @Override
            public void onResponse(Call<LiveData<GlobalResponse<FeedBackResponse>>> call, Response<LiveData<GlobalResponse<FeedBackResponse>>> response) {
                if (response.isSuccessful()) {
                    feedBackResponseMutableLiveData.setValue(response.body().getValue());
                }
            }

            @Override
            public void onFailure(Call<LiveData<GlobalResponse<FeedBackResponse>>> call, Throwable t) {
                feedBackResponseMutableLiveData.setValue(null);
            }
        });
        return feedBackResponseMutableLiveData;
    }
}
