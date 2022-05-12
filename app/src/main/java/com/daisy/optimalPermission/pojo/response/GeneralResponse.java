package com.daisy.optimalPermission.pojo.response;

import java.util.List;

public class GeneralResponse {
        private List<Product> products;
        private List<Carrier> carrier;
        private List<OsType> osTypes;
        private ApkDetails apkDetails;

    public ApkDetails getApkDetails() {
        return apkDetails;
    }

    public void setApkDetails(ApkDetails apkDetails) {
        this.apkDetails = apkDetails;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Carrier> getCarrier() {
        return carrier;
    }

    public void setCarrier(List<Carrier> carrier) {
        this.carrier = carrier;
    }

    public List<OsType> getOsTypes() {
        return osTypes;
    }

    public void setOsTypes(List<OsType> osTypes) {
        this.osTypes = osTypes;
    }
}
