package com.daisy.activity.deleteCard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.pojo.response.DeleteCardResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteCardRepo {
    private ApiService apiService;
    private MutableLiveData<GlobalResponse<DeleteCardResponse>> liveData=new MutableLiveData<>();
    public DeleteCardRepo()
    {
        apiService= AppRetrofit.getInstance().getApiService();
    }
    public LiveData<GlobalResponse<DeleteCardResponse>> deleteCard(HashMap<String,String> hashMap)
    {
        Call<GlobalResponse<DeleteCardResponse>> call=apiService.deleteCard(hashMap,hashMap.get(Constraint.TOKEN));
        call.enqueue(new Callback<GlobalResponse<DeleteCardResponse>>() {
            @Override
            public void onResponse(Call<GlobalResponse<DeleteCardResponse>> call, Response<GlobalResponse<DeleteCardResponse>> response) {
                if (response.isSuccessful())
                {
                    liveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse<DeleteCardResponse>> call, Throwable t) {
                    liveData.setValue(null);
            }
        });
        return liveData;
    }
 }
