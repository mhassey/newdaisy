package com.daisy.daisyGo.pojo.request;

import android.content.Context;

public class LogClearRequest {
    private Context context;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
