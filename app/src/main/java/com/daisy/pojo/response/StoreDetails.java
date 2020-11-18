package com.daisy.pojo.response;

public class StoreDetails {
    private  int idstore;
    private  String UTCOffset;
    private String open;
    private String  closed;

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
