package com.iris.activity.apkUpdate;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.iris.pojo.response.GeneralResponse;
import com.iris.pojo.response.GlobalResponse;

import java.util.HashMap;

/**
 * Purpose -  ApkUpdateViewModel is an view model that help to connect with ApkUpdateRepo to know is their any update is available or not
 * Responsibility - ApkUpdateViewModel takes request and provide LiveData<GlobalResponse<GeneralResponse>> response and when api return output then return response to activity
 **/
public class ApkUpdateViewModel extends AndroidViewModel {
    private LiveData<GlobalResponse<GeneralResponse>> responseLiveData;
    private MutableLiveData<HashMap<String,String>> request=new MutableLiveData<>();

    public ApkUpdateViewModel(@NonNull Application application) {
        super(application);
        responseLiveData= Transformations.switchMap(request, new Function<HashMap<String, String>, LiveData<GlobalResponse<GeneralResponse>>>() {
            @Override
            public LiveData<GlobalResponse<GeneralResponse>> apply(HashMap<String, String> input) {
                return new ApkUpdateRepo().getUpdate(input);
            }
        });
    }

    /**
     * Responsibility - setRequest method set values in request mutable live data
     * Parameters - Its takes HashMap object
     **/
    public void setRequest(HashMap requestWork)
    {
        request.setValue(requestWork);
    }

    /**
     * Responsibility - getResponseLiveData method help to get  LiveData<GlobalResponse<GeneralResponse>>
     * Parameters - No parameter
     **/
    public LiveData<GlobalResponse<GeneralResponse>> getResponseLiveData()
    {
        return responseLiveData;
    }
}
