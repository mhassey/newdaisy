package com.daisy.optimalPermission.activity.onBoarding.slider.slides.signup.vo;

import com.daisy.optimalPermission.pojo.response.LoginResponse;
import com.daisy.optimalPermission.pojo.response.SimpleResponse;

public class SignUpResponse extends SimpleResponse {
    private LoginResponse data;


    public LoginResponse getData() {
        return data;
    }

    public void setData(LoginResponse data) {
        this.data = data;
    }
}
