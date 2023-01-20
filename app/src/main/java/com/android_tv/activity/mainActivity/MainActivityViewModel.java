package com.android_tv.activity.mainActivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.json.JSONArray;

public class MainActivityViewModel extends AndroidViewModel {
    private boolean isSettingVisible=false;
    private JSONArray jsonArray;
    private boolean isExceptionInHtml=false;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isExceptionInHtml() {
        return isExceptionInHtml;
    }

    public void setExceptionInHtml(boolean exceptionInHtml) {
        isExceptionInHtml = exceptionInHtml;
    }

    public boolean isSettingVisible() {
        return isSettingVisible;
    }

    public void setSettingVisible(boolean settingVisible) {
        isSettingVisible = settingVisible;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
}
