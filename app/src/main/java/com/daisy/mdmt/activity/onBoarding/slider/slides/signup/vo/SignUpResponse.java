package com.daisy.mdmt.activity.onBoarding.slider.slides.signup.vo;

import com.daisy.mdmt.pojo.response.LoginResponse;
import com.daisy.mdmt.pojo.response.SimpleResponse;

public class SignUpResponse extends SimpleResponse {
    private LoginResponse data;


    public LoginResponse getData() {
        return data;
    }

    public void setData(LoginResponse data) {
        this.data = data;
    }
}
