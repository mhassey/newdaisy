package com.daisy.daisyGo.pojo.response;

import androidx.annotation.NonNull;

public class Product {
    private String idproductStatic;
    private String idcarrier;
    private String upc;
    private String mfg;
    private String model;
    private String aca;
    private String productName;
    private String mdescShort;
    private  String mdescLong;
    private String thumbnailImageURL;

    public String getIdproductStatic() {
        return idproductStatic;
    }

    public void setIdproductStatic(String idproductStatic) {
        this.idproductStatic = idproductStatic;
    }

    public String getIdcarrier() {
        return idcarrier;
    }

    public void setIdcarrier(String idcarrier) {
        this.idcarrier = idcarrier;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getMfg() {
        return mfg;
    }

    public void setMfg(String mfg) {
        this.mfg = mfg;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAca() {
        return aca;
    }

    public void setAca(String aca) {
        this.aca = aca;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMdescShort() {
        return mdescShort;
    }

    public void setMdescShort(String mdescShort) {
        this.mdescShort = mdescShort;
    }

    public String getMdescLong() {
        return mdescLong;
    }

    public void setMdescLong(String mdescLong) {
        this.mdescLong = mdescLong;
    }

    public String getThumbnailImageURL() {
        return thumbnailImageURL;
    }

    public void setThumbnailImageURL(String thumbnailImageURL) {
        this.thumbnailImageURL = thumbnailImageURL;
    }

    @NonNull
    @Override
    public String toString() {
        return productName;
    }
}
