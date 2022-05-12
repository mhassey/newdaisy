package com.daisy.daisyGo.activity.onBoarding.slider.screenAdd.vo;

import com.daisy.daisyGo.pojo.response.ScreenPosition;

public class ScreenAddResponse {
    private int id;
    private int iddevice;
    private String token;
    private ScreenPosition screenPosition;

    public int getIddevice() {
        return iddevice;
    }

    public void setIddevice(int iddevice) {
        this.iddevice = iddevice;
    }

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
