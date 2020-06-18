package com.daisy.pojo.request;

import android.content.Context;

public class LogsRequest {
    private String type;
    private Context context;

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
