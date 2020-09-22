package com.daisy.activity.baseUrl;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.daisy.pojo.response.Url;

import java.util.ArrayList;
import java.util.List;

public class BaseUrlSettingsViewModel extends AndroidViewModel {
    private List<Url> urls;
    public BaseUrlSettingsViewModel(@NonNull Application application) {
        super(application);
    }

    public List<Url> getUrls() {
        return urls;
    }

    public void setUrls(List<Url> urls) {
        this.urls = urls;
    }

    public void setDefaultUrls() {

            List<Url> urls=new ArrayList<>();
            Url url=new Url();
            url.setName("SandBox Server");
            url.setUrl("http://sandbox.mobilepricecard.com/");
            urls.add(url);
            Url url1=new Url();
            url1.setName("VZ Server");
            url1.setUrl("http://vz.mobilepricecards.com/");
            urls.add(url1);
            Url url2=new Url();
            url2.setName("TM Server");
            url2.setUrl("http://tm.mobilepricecards.com/");
            urls.add(url2);
            Url url3=new Url();
            url3.setName("Demo Server");
            url3.setUrl("http://demo.mobilepricecards.com/");
            urls.add(url3);
        Url url4=new Url();
        url4.setName("Oak Dev Server");
        url4.setUrl("http://oak-dev.mobilepricecard.com/");
        urls.add(url4);
        setUrls(urls);
    }
}
