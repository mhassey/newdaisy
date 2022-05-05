package com.daisy.pojo.response;

public class GlobalResponse<T> {


    private boolean api_status;
    private String message;

    T data;

    public T getResult() {
        return data;
    }

    public void setResult(T result) {
        this.data = result;
    }
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
