package com.nzmdm.daisy.activity.updateProduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nzmdm.daisy.apiService.ApiService;
import com.nzmdm.daisy.apiService.AppRetrofit;
import com.nzmdm.daisy.pojo.response.GlobalResponse;
import com.nzmdm.daisy.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Purpose -  UpdateProductRepo is an class that help to call update screen api
 * Responsibility - Its takes update screen request as parameter and call update product api and set response to mutable live data
 **/
public class UpdateProductRepo {
    private MutableLiveData<GlobalResponse> mutableLiveData = new MutableLiveData<>();
    private ApiService apiService;

    UpdateProductRepo() {
        apiService = AppRetrofit.getInstance().getApiService();
    }

    /**
     * Responsibility - updateScreen  method fire updateProduct api and set the response on mutable live data
     * Parameters - Its takes HashMap<String, String> object as parameter
     **/
    public LiveData<GlobalResponse> updateScreen(HashMap<String, String> input) {
        Call<GlobalResponse> responseCall = apiService.updateProduct(input, input.get(Constraint.TOKEN));
        responseCall.enqueue(new Callback<GlobalResponse>() {
            @Override
            public void onResponse(Call<GlobalResponse> call, Response<GlobalResponse> response) {
                if (response.isSuccessful()) {
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse> call, Throwable t) {
                    mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }
}
