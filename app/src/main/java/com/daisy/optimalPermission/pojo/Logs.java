package com.daisy.optimalPermission.pojo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Logs {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "eventName")
    private String eventName;
    @ColumnInfo(name = "eventUrl")
    private String eventUrl;
    @ColumnInfo(name = "eventDateTime")
    private String eventDateTime;
    @ColumnInfo(name = "eventTimeStemp")
    private String eventTimeStemp;
    @ColumnInfo(name = "eventDescription")
    private String eventDescription;
    @ColumnInfo(name = "logType")
    private String logType;
    @ColumnInfo(name = "isSync")
    private boolean isSync;
    @ColumnInfo(name = "isClear")
    private boolean isClear;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getEventTimeStemp() {
        return eventTimeStemp;
    }

    public void setEventTimeStemp(String eventTimeStemp) {
        this.eventTimeStemp = eventTimeStemp;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public boolean isClear() {
        return isClear;
    }

    public void setClear(boolean clear) {
        isClear = clear;
    }


}
