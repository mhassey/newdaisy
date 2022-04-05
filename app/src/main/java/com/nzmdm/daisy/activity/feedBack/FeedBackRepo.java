package com.nzmdm.daisy.activity.feedBack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nzmdm.daisy.apiService.ApiService;
import com.nzmdm.daisy.apiService.AppRetrofit;
import com.nzmdm.daisy.pojo.response.FeedBackResponse;
import com.nzmdm.daisy.pojo.response.GlobalResponse;
import com.nzmdm.daisy.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Purpose - FeedBackRepo class helps to call feedback api and return result
 */
public class FeedBackRepo {
    ApiService apiService;

    public FeedBackRepo() {
        apiService = AppRetrofit.getInstance().getApiService();
    }

    private MutableLiveData<GlobalResponse<FeedBackResponse>> feedBackResponseMutableLiveData = new MutableLiveData<>();

    /**
     * Purpose - getFeedBackResponse method helps to call feedback api and set its result to  mutable live data
     *
     * @param feedBackRequest
     * @return
     */
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
