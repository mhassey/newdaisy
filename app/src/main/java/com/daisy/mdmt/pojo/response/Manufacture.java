package com.daisy.mdmt.pojo.response;

import androidx.annotation.NonNull;

public class Manufacture {
    private String idterm;
    private String termName;
    private String termValue;

    public String getIdterm() {
        return idterm;
    }

    public void setIdterm(String idterm) {
        this.idterm = idterm;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTermValue() {
        return termValue;
    }

    public void setTermValue(String termValue) {
        this.termValue = termValue;
    }

    @NonNull
    @Override
    public String toString() {
    return termValue;
    }
}
