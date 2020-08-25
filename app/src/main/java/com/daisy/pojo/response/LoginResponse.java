package com.daisy.pojo.response;

import java.util.List;

public class LoginResponse extends GlobalResponse {
    private String idstore;
    private String idOU;
    private String pricingPlanID;
    private String storeName;
    private String Phones;
    private String storeLocation;
    private String storeContact;
    private String storeHours;
    private List<Carrier> carrier;

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
