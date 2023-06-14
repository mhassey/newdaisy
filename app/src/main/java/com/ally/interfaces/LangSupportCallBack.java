package com.ally.interfaces;

import androidx.recyclerview.widget.RecyclerView;

import com.ally.pojo.LangPojo;

public interface LangSupportCallBack {
    RecyclerView.ViewHolder getHolder();
    void setLangPojo(LangPojo langPojo, RecyclerView.ViewHolder viewHolder);

    void setDefaultLang(LangPojo langPojo, RecyclerView.ViewHolder holder);
}
