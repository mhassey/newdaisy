package com.daisy.mainDaisy.pojo.request;

import android.util.Log;

import com.daisy.mainDaisy.pojo.Logs;

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
