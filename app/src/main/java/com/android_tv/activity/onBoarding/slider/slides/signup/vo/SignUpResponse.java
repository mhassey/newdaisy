package com.android_tv.activity.onBoarding.slider.slides.signup.vo;

import com.android_tv.pojo.response.LoginResponse;
import com.android_tv.pojo.response.SimpleResponse;

public class SignUpResponse extends SimpleResponse {
    private LoginResponse data;


    public LoginResponse getData() {
        return data;
    }

    public void setData(LoginResponse data) {
        this.data = data;
    }
}
