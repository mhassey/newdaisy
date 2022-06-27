package com.daisy.daisyGo.activity.welcomeScreen;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.daisyGo.pojo.response.GlobalResponse;
import com.daisy.mainDaisy.pojo.response.KeyToUrlResponse;

import java.util.HashMap;

public class WelcomeViewModel extends AndroidViewModel {
    private LiveData<GlobalResponse<KeyToUrlResponse>> responseLiveData;
    private MutableLiveData<HashMap<String, String>> requestLiveData = new MutableLiveData();

    public WelcomeViewModel(@NonNull Application application) {
        super(application);
        responseLiveData = Transformations.switchMap(requestLiveData, new Function<HashMap<String, String>, LiveData<GlobalResponse<KeyToUrlResponse>>>() {
            @Override
            public LiveData<GlobalResponse<KeyToUrlResponse>> apply(HashMap<String, String> input) {
                return new WelcomeRepo().fireKeyToUrlApi(input);
            }
        });
    }

    public void setRequestLiveData(HashMap<String, String> request) {
        requestLiveData.setValue(request);
    }

    public LiveData<GlobalResponse<KeyToUrlResponse>> getResponseLiveData() {
        return responseLiveData;
    }


}
