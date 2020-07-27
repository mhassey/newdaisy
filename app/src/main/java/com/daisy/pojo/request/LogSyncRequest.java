package com.daisy.pojo.request;

import android.util.Log;

import com.daisy.pojo.Logs;

import java.util.List;

public class LogSyncRequest {
    private List<Logs> logs;

    public List<Logs> getLogs() {
        return logs;
    }

    public void setLogs(List<Logs> logs) {
        this.logs = logs;
    }
}
