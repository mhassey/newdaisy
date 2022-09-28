package com.daisy.activity.onBoarding.slider.getCard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

public class GetCardViewModel extends AndroidViewModel {
    private MutableLiveData<HashMap<String,String>> mutableLiveData=new MutableLiveData<>();
    private LiveData<GlobalResponse<GetCardResponse>> liveData;
    public GetCardViewModel(@NonNull Application application) {
        super(application);
        liveData= Transformations.switchMap(mutableLiveData, new Function<HashMap<String, String>, LiveData<GlobalResponse<GetCardResponse>>>() {
            @Override
            public LiveData<GlobalResponse<GetCardResponse>> apply(HashMap<String, String> input) {
                 GetCardRepo getCardResponse=new GetCardRepo();

                return getCardResponse.getCard(input);
            }
        });
    }

    public void setMutableLiveData(HashMap<String,String> request)
    {
        mutableLiveData.setValue(request);
    }
    public LiveData<GlobalResponse<GetCardResponse>> getLiveData()
    {
        return liveData;
    }
}
