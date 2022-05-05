package com.daisy.activity.updateProduct;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.pojo.response.GlobalResponse;

import java.util.HashMap;

/**
 * Purpose -  UpdateProductViewModel class in an view model class that connect UpdateProduct activity to UpdateProductRepo
 * Responsibility - Its takes request from activity and call repository to call api and send the response to activity
 **/
public class UpdateProductViewModel extends AndroidViewModel {

    private MutableLiveData<HashMap<String,String>> mutableLiveData=new MutableLiveData<>();
    private LiveData<GlobalResponse> liveData;
    private UpdateProductRepo updateProductRepo=new UpdateProductRepo();

    public UpdateProductViewModel(@NonNull Application application) {
        super(application);
        liveData= Transformations.switchMap(mutableLiveData, new Function<HashMap<String, String>, LiveData<GlobalResponse>>() {
            @Override
            public LiveData<GlobalResponse> apply(HashMap<String, String> input) {
                return updateProductRepo.updateScreen(input);
            }
        });


    }

    public void setMutableLiveData(HashMap<String,String> request)
    {
        mutableLiveData.setValue(request);
    }
    public LiveData<GlobalResponse> getLiveData()
    {
        return liveData;
    }}
