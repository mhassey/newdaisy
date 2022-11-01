package com.iris.activity.onBoarding.slider.slides.signup.vo;

import com.iris.pojo.response.LoginResponse;
import com.iris.pojo.response.SimpleResponse;

public class SignUpResponse extends SimpleResponse {
    private LoginResponse data;


    public LoginResponse getData() {
        return data;
    }

    public void setData(LoginResponse data) {
        this.data = data;
    }
}
