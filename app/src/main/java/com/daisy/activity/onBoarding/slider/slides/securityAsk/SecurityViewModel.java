package com.daisy.activity.onBoarding.slider.slides.securityAsk;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.daisy.databinding.ActivityOnBaordingBinding;

public class SecurityViewModel extends AndroidViewModel {
    private ActivityOnBaordingBinding onBaordingBindingMain;

    public ActivityOnBaordingBinding getOnBaordingBindingMain() {
        return onBaordingBindingMain;
    }

    public void setOnBaordingBindingMain(ActivityOnBaordingBinding onBaordingBindingMain) {
        this.onBaordingBindingMain = onBaordingBindingMain;
    }

    public SecurityViewModel(@NonNull Application application) {
        super(application);
    }
}
