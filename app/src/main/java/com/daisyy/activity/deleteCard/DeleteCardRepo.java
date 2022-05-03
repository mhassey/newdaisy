package com.daisyy.activity.deleteCard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisyy.apiService.ApiService;
import com.daisyy.apiService.AppRetrofit;
import com.daisyy.pojo.response.DeleteCardResponse;
import com.daisyy.pojo.response.GlobalResponse;
import com.daisyy.utils.Constraint;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Purpose -  DeleteCardRepo is an class that help to fire delete_cards api
 * Responsibility - Its helps to delete card from server which is assigned to screen

 **/
public class DeleteCardRepo {
    private ApiService apiService;
    private MutableLiveData<GlobalResponse<DeleteCardResponse>> liveData=new MutableLiveData<>();

    public DeleteCardRepo()
    {

    }

    /**
     * Responsibility - deleteCard method fire the delete_cards api and set the response to live data and return live data to calling method
     * Parameters - Its takes HashMap<String,String> hashMap
     **/
    public LiveData<GlobalResponse<DeleteCardResponse>> deleteCard(HashMap<String,String> hashMap)
    {
        apiService= AppRetrofit.getInstance().getApiService();
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
