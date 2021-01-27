package com.daisy.activity.apkUpdate;

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

// TODO Apk update model class
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
    // TODO Set new request for getting apk update from server
    public void setRequest(HashMap requestWork)
    {
        request.setValue(requestWork);
    }

    // TODO Get response to view
    public LiveData<GlobalResponse<GeneralResponse>> getResponseLiveData()
    {
        return responseLiveData;
    }
}
