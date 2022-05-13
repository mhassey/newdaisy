package com.daisy.mdmt.activity.deleteCard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.mdmt.pojo.response.DeleteCardResponse;
import com.daisy.mdmt.pojo.response.GlobalResponse;

import java.util.HashMap;

/**
 * Purpose -  DeleteCardViewModel is an view model class that call DeleteCardRepo to fire delete request
 * Responsibility - Its helps to call deleteCard method of DeleteCardRepo and set request live data  value and return response live data
 **/
public class DeleteCardViewModel extends AndroidViewModel {
    private MutableLiveData<HashMap<String,String>> mutableLiveData=new MutableLiveData<>();
    private LiveData<GlobalResponse<DeleteCardResponse>> liveData;
    private DeleteCardRepo deleteCardRepo=new DeleteCardRepo();


    public DeleteCardViewModel(@NonNull Application application) {
        super(application);
        liveData= Transformations.switchMap(mutableLiveData, new Function<HashMap<String, String>, LiveData<GlobalResponse<DeleteCardResponse>>>() {
            @Override
            public LiveData<GlobalResponse<DeleteCardResponse>> apply(HashMap<String, String> input) {
                return deleteCardRepo.deleteCard(input);
            }
        });
    }

    /**
     * Responsibility - setMutableLiveData is an method that set requested value to requested mutable live data
     * Parameters - Its takes HashMap<String,String> hashMap
     **/
    public void setMutableLiveData(HashMap<String,String> request)
    {
        mutableLiveData.setValue(request);
    }

    /**
     * Responsibility - getLiveData is an method that returns LiveData<GlobalResponse<DeleteCardResponse>> to calling method
     * Parameters - No parameter
     **/
    public LiveData<GlobalResponse<DeleteCardResponse>> getLiveData()
    {
        return liveData;
    }
}
