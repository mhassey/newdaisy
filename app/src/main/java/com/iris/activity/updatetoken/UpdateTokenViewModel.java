package com.iris.activity.updatetoken;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.iris.pojo.response.GlobalResponse;
import com.iris.pojo.response.UpdateTokenResponse;

import java.util.HashMap;

public class UpdateTokenViewModel extends AndroidViewModel {
    private MutableLiveData<HashMap<String, String>> requestMutableLiveData = new MutableLiveData<>();
    private LiveData<GlobalResponse<UpdateTokenResponse>> responseLiveData;

    public UpdateTokenViewModel(@NonNull Application application) {
        super(application);
        responseLiveData = Transformations.switchMap(requestMutableLiveData, new Function<HashMap<String, String>, LiveData<GlobalResponse<UpdateTokenResponse>>>() {
            @Override
            public LiveData<GlobalResponse<UpdateTokenResponse>> apply(HashMap<String, String> input) {
                return new UpdateTokenRepo().updateToken(input);
            }
        });
    }

    public void setTokeRequest(HashMap<String, String> request) {
        requestMutableLiveData.setValue(request);
    }

    public LiveData<GlobalResponse<UpdateTokenResponse>> getTokenResponse() {
        return responseLiveData;
    }
}
