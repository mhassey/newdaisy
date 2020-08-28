package com.daisy.activity.deleteCard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.pojo.response.DeleteCardResponse;
import com.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

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

    public void setMutableLiveData(HashMap<String,String> request)
    {
        mutableLiveData.setValue(request);
    }
    public LiveData<GlobalResponse<DeleteCardResponse>> getLiveData()
    {
        return liveData;
    }
}
