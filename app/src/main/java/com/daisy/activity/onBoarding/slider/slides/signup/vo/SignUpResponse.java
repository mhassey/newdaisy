package com.daisy.activity.onBoarding.slider.slides.signup.vo;

import com.daisy.pojo.response.LoginResponse;
import com.daisy.pojo.response.SimpleResponse;

public class SignUpResponse extends SimpleResponse {
    private LoginResponse data;


    public LoginResponse getData() {
        return data;
    }

    public void setData(LoginResponse data) {
        this.data = data;
    }
}
