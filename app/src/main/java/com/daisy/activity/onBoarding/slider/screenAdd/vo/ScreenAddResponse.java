package com.daisy.activity.onBoarding.slider.screenAdd.vo;

import com.daisy.pojo.response.ScreenPosition;

public class ScreenAddResponse {
    private int id;
    private String token;
    private ScreenPosition screenPosition;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ScreenPosition getScreenPosition() {
        return screenPosition;
    }

    public void setScreenPosition(ScreenPosition screenPosition) {
        this.screenPosition = screenPosition;
    }
}
