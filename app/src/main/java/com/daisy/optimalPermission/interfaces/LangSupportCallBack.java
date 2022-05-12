package com.daisy.optimalPermission.interfaces;

import androidx.recyclerview.widget.RecyclerView;

import com.daisy.optimalPermission.pojo.LangPojo;

public interface LangSupportCallBack {
    RecyclerView.ViewHolder getHolder();

    void setLangPojo(LangPojo langPojo, RecyclerView.ViewHolder viewHolder);

    void setDefaultLang(LangPojo langPojo, RecyclerView.ViewHolder holder);
}
