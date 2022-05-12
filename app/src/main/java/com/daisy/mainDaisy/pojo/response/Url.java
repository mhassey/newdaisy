package com.daisy.mainDaisy.pojo.response;

import androidx.annotation.NonNull;

public class Url {
    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NonNull
    @Override
    public String toString() {
        return url;
    }
}
