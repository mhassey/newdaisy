package com.nzmdm.daisy.pojo.response;

public class SimpleResponse {
    private boolean api_status;
    private String message;

    public boolean isApi_status() {
        return api_status;
    }

    public void setApi_status(boolean api_status) {
        this.api_status = api_status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
