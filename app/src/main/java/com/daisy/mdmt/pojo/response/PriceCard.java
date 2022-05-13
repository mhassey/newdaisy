package com.daisy.mdmt.pojo.response;

public class PriceCard {
    private String idpricecard;
    private String fileName;
    private String version;
    private String dateCreated;
    private String dateModified;
    private String dateExpires;

    public String getIdpricecard() {
        return idpricecard;
    }

    public void setIdpricecard(String idpricecard) {
        this.idpricecard = idpricecard;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(String dateExpires) {
        this.dateExpires = dateExpires;
    }
}
