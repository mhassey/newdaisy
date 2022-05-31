package com.daisy.pojo.response;

import java.util.List;

public class KeyToUrlResponse {
    private List<Urls> urls;
    private boolean matched_status;
    private String matched_url;

    public boolean isMatched_status() {
        return matched_status;
    }

    public void setMatched_status(boolean matched_status) {
        this.matched_status = matched_status;
    }

    public String getMatched_url() {
        return matched_url;
    }

    public void setMatched_url(String matched_url) {
        this.matched_url = matched_url;
    }

    public List<Urls> getUrls() {
        return urls;
    }

    public void setUrls(List<Urls> urls) {
        this.urls = urls;
    }
}
