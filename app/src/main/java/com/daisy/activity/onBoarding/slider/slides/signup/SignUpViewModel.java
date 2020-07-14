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

public class SignUpViewModel extends AndroidViewModel {
    private MutableLiveData<SignUpRequest> signUpRequestMutableLiveData = new MutableLiveData<>();
    private LiveData<SignUpResponse> responseLiveData;
    private SignUpRepo signUpRepo=new SignUpRepo();

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        responseLiveData = Transformations.switchMap(signUpRequestMutableLiveData, new Function<SignUpRequest, LiveData<SignUpResponse>>() {
            @Override
            public LiveData<SignUpResponse> apply(SignUpRequest input) {
                return signUpRepo.signUp(input);
            }
        });
    }

    public void setSignUpRequestMutableLiveData(SignUpRequest request) {
        signUpRequestMutableLiveData.setValue(request);
    }

    public LiveData<SignUpResponse> getResponseLiveData() {
        return responseLiveData;
    }
}
