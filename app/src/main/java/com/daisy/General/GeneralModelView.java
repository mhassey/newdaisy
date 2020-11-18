package com.daisy.General;

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

public class GeneralModelView extends AndroidViewModel {
    private MutableLiveData<HashMap<String,String>> requestMutableLiveData=new MutableLiveData<>();
    private LiveData<GlobalResponse<GeneralResponse>> liveData;
    private GeneralModelRepo generalModelRepo=new GeneralModelRepo();

    public GeneralModelView(@NonNull Application application) {
        super(application);
        liveData= Transformations.switchMap(requestMutableLiveData, new Function<HashMap<String, String>, LiveData<GlobalResponse<GeneralResponse>>>() {
            @Override
            public LiveData<GlobalResponse<GeneralResponse>> apply(HashMap<String, String> input) {
                return generalModelRepo.getGeneralResponse(input);
            }
        });
    }
    public void setRequestMutableLiveData(HashMap<String,String> map)
    {
        requestMutableLiveData.setValue(map);
    }
    public LiveData<GlobalResponse<GeneralResponse>> getLiveData()
    {
        return liveData;
    }
}
