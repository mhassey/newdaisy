package com.daisy.pojo.response;

import com.daisy.pojo.Logs;

import java.util.List;

public class LogResponse  {
    private List<Logs> logs;

    public List<Logs> getLogs() {
        return logs;
    }

    public void setLogs(List<Logs> logs) {
        this.logs = logs;
    }
}
