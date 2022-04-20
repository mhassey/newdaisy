package com.vzwmdm.daisy.activity.onBoarding.slider.slides.signup.vo;

import com.vzwmdm.daisy.pojo.response.LoginResponse;
import com.vzwmdm.daisy.pojo.response.SimpleResponse;

public class SignUpResponse extends SimpleResponse {
    private LoginResponse data;


    public LoginResponse getData() {
        return data;
    }

    public void setData(LoginResponse data) {
        this.data = data;
    }
}
