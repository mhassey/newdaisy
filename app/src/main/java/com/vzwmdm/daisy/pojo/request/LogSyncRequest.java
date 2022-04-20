package com.vzwmdm.daisy.pojo.request;

import com.vzwmdm.daisy.pojo.Logs;

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
