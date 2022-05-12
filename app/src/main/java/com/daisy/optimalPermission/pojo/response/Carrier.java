package com.daisy.optimalPermission.pojo.response;

import androidx.annotation.NonNull;

public class Carrier {
    private int idcarrier;
    private String carrierName;
    private String carrierDescription;

    public int getIdcarrier() {
        return idcarrier;
    }

    public void setIdcarrier(int idcarrier) {
        this.idcarrier = idcarrier;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getCarrierDescription() {
        return carrierDescription;
    }

    public void setCarrierDescription(String carrierDescription) {
        this.carrierDescription = carrierDescription;
    }
    @NonNull
    @Override
    public String toString() {
        return carrierName;
    }
}
