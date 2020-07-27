package com.daisy.activity.onBoarding.slider.slides.signup;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpRequest;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class SignUpViewModel extends AndroidViewModel {
    private MutableLiveData<HashMap<String,String>> signUpRequestMutableLiveData = new MutableLiveData<>();
    private LiveData<SignUpResponse> responseLiveData;
    private SignUpRepo signUpRepo=new SignUpRepo();

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        responseLiveData = Transformations.switchMap(signUpRequestMutableLiveData, new Function<HashMap<String, String>, LiveData<SignUpResponse>>() {
            @Override
            public LiveData<SignUpResponse> apply(HashMap<String, String> input) {
                return signUpRepo.signUp(input);
            }
        });
    }

    public void setSignUpRequestMutableLiveData(HashMap<String,String> request) {
        signUpRequestMutableLiveData.setValue(request);
    }

    public LiveData<SignUpResponse> getResponseLiveData() {
        return responseLiveData;
    }
}
