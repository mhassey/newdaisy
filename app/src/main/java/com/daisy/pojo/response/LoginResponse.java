package com.daisy.pojo.response;

import java.util.List;

public class LoginResponse extends GlobalResponse {
    private String idstore;
    private String idOU;
    private String pricingPlanID;
    private String deviceSanitize;
    private String storeName;
    private String Phones;
    private String storeLocation;
    private String storeContact;
    private String storeHours;
    private List<Carrier> carrier;
    private List<Manufacture> manufacturers;
    private String UTCOffset;
    private String open;
    private String closed;
    private String currentDate;
    private  String currentTime;
    private String currentTimeH;
    private String deviceSecurity;

    public String getDeviceSecurity() {
        return deviceSecurity;
    }

    public void setDeviceSecurity(String deviceSecurity) {
        this.deviceSecurity = deviceSecurity;
    }

    public String getDeviceSanitize() {
        return deviceSanitize;
    }

    public void setDeviceSanitize(String deviceSanitize) {
        this.deviceSanitize = deviceSanitize;
    }

    public List<Manufacture> getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(List<Manufacture> manufacturers) {
        this.manufacturers = manufacturers;
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

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentTimeH() {
        return currentTimeH;
    }

    public void setCurrentTimeH(String currentTimeH) {
        this.currentTimeH = currentTimeH;
    }

    public String getPricingPlanID() {
        return pricingPlanID;
    }

    public void setPricingPlanID(String pricingPlanID) {
        this.pricingPlanID = pricingPlanID;
    }

    public List<Carrier> getCarrier() {
        return carrier;
    }

    public void setCarrier(List<Carrier> carrier) {
        this.carrier = carrier;
    }

    public String getIdstore() {
        return idstore;
    }

    public void setIdstore(String idstore) {
        this.idstore = idstore;
    }

    public String getIdOU() {
        return idOU;
    }

    public void setIdOU(String idOU) {
        this.idOU = idOU;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPhones() {
        return Phones;
    }

    public void setPhones(String phones) {
        Phones = phones;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getStoreContact() {
        return storeContact;
    }

    public void setStoreContact(String storeContact) {
        this.storeContact = storeContact;
    }

    public String getStoreHours() {
        return storeHours;
    }

    public void setStoreHours(String storeHours) {
        this.storeHours = storeHours;
    }
}
