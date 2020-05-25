package com.daisy.activity.mainActivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainActivityViewModel extends AndroidViewModel {
    private boolean isSettingVisible=false;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }


    public boolean isSettingVisible() {
        return isSettingVisible;
    }

    public void setSettingVisible(boolean settingVisible) {
        isSettingVisible = settingVisible;
    }
}
