package com.daisy.activity.onBoarding.slider.slides.addScreen;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

public class AddScreenViewModel extends AndroidViewModel {

    private MutableLiveData<HashMap<String ,String>> generalRequest=new MutableLiveData<>();
    private LiveData<GlobalResponse<GeneralResponse>> generalResponseLiveData;
    private AddScreenRepo addScreenRepo=new AddScreenRepo();
    public AddScreenViewModel(@NonNull Application application) {
        super(application);
        generalResponseLiveData= Transformations.switchMap(generalRequest, new Function<HashMap<String, String>, LiveData<GlobalResponse<GeneralResponse>>>() {
            @Override
            public LiveData<GlobalResponse<GeneralResponse>> apply(HashMap<String, String> input) {
                return  addScreenRepo.getGenralResponse(input);
            }
        });
    }
    public void setGeneralRequest(HashMap<String,String> request)
    {
        generalRequest.setValue(request);
    }
    public LiveData<GlobalResponse<GeneralResponse>> getGeneralResponseLiveData()
    {
        return generalResponseLiveData;
    }
}
