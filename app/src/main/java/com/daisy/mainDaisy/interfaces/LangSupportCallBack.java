package com.daisy.mainDaisy.interfaces;

import androidx.recyclerview.widget.RecyclerView;

import com.daisy.mainDaisy.adapter.LangSupportAdaptor;
import com.daisy.mainDaisy.pojo.LangPojo;

public interface LangSupportCallBack {
    RecyclerView.ViewHolder getHolder();

    void setLangPojo(LangPojo langPojo, RecyclerView.ViewHolder viewHolder);

    void setDefaultLang(LangPojo langPojo, RecyclerView.ViewHolder holder);
}
