package com.daisy.optimalPermission.pojo.response;

public class Android {
    private boolean status;
    private String link;
    private String version;
    private int id;
    private String osID;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOsID() {
        return osID;
    }

    public void setOsID(String osID) {
        this.osID = osID;
    }
}
