package com.daisy.activity.updatePosition;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.activity.updatePosition.vo.UpdatePositionResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.utils.Constraint;

import java.util.HashMap;

public class UpdatePositionViewModel extends AndroidViewModel {
    private MutableLiveData<HashMap<String,String>> mutableLiveData=new MutableLiveData<>();
    private LiveData<GlobalResponse<UpdatePositionResponse>> liveData;
    private UpdatePositionRepo positionRepo=new UpdatePositionRepo();
    public UpdatePositionViewModel(@NonNull Application application) {
        super(application);
        liveData= Transformations.switchMap(mutableLiveData, new Function<HashMap<String, String>, LiveData<GlobalResponse<UpdatePositionResponse>>>() {
            @Override
            public LiveData<GlobalResponse<UpdatePositionResponse>> apply(HashMap<String, String> input) {
                return positionRepo.updatePosition(input,input.get(Constraint.TOKEN));
            }
        });
    }
    public void setMutableLiveData(HashMap<String,String> request)
    {
        mutableLiveData.setValue(request);
    }
    public LiveData<GlobalResponse<UpdatePositionResponse>> getUpdatePosition()
    {
        return  liveData;
    }
}
