package com.daisy.activity.onBoarding.slider.screenAdd;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.activity.onBoarding.slider.screenAdd.vo.ScreenAddResponse;
import com.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

public class ScreenAddViewModel extends AndroidViewModel {

    private MutableLiveData<HashMap<String,String>> mutableLiveData=new MutableLiveData<>();
    private LiveData<GlobalResponse<ScreenAddResponse>> liveData;
    private ScreenAddRepo screenAddRepo=new ScreenAddRepo();
    public ScreenAddViewModel(@NonNull Application application) {
        super(application);
        liveData= Transformations.switchMap(mutableLiveData, new Function<HashMap<String, String>, LiveData<GlobalResponse<ScreenAddResponse>>>() {
            @Override
            public LiveData<GlobalResponse<ScreenAddResponse>> apply(HashMap<String, String> input) {
                return screenAddRepo.addScreen(input);
            }
        });
    }

    public void setMutableLiveData(HashMap<String,String> request)
    {
        mutableLiveData.setValue(request);
    }
    public LiveData<GlobalResponse<ScreenAddResponse>> getLiveData()
    {
        return liveData;
    }
}
