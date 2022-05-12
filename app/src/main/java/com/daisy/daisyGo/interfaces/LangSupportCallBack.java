package com.daisy.daisyGo.interfaces;

import androidx.recyclerview.widget.RecyclerView;

import com.daisy.daisyGo.pojo.LangPojo;

public interface LangSupportCallBack {
    RecyclerView.ViewHolder getHolder();

    void setLangPojo(LangPojo langPojo, RecyclerView.ViewHolder viewHolder);

    void setDefaultLang(LangPojo langPojo, RecyclerView.ViewHolder holder);
}
