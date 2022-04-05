package com.nzmdm.daisy.pojo.response;

public class StoreDetails {
    private  int idstore;
    private  String UTCOffset;
    private String open;
    private String pricingPlanID;
    private String  closed;
    private String currentTime;
    private String deviceSecurity;

    public String getDeviceSecurity() {
        return deviceSecurity;
    }

    public void setDeviceSecurity(String deviceSecurity) {
        this.deviceSecurity = deviceSecurity;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getPricingPlanID() {
        return pricingPlanID;
    }

    public void setPricingPlanID(String pricingPlanID) {
        this.pricingPlanID = pricingPlanID;
    }

    public int getIdstore() {
        return idstore;
    }

    public void setIdstore(int idstore) {
        this.idstore = idstore;
    }

    public String getUTCOffset() {
        return UTCOffset;
    }

    public void setUTCOffset(String UTCOffset) {
        this.UTCOffset = UTCOffset;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }
}
