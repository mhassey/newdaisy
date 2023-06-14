package com.allyy.activity.onBoarding.slider.slides.signup.vo;

import com.allyy.pojo.response.LoginResponse;
import com.allyy.pojo.response.SimpleResponse;

public class SignUpResponse extends SimpleResponse {
    private LoginResponse data;


    public LoginResponse getData() {
        return data;
    }

    public void setData(LoginResponse data) {
        this.data = data;
    }
}
