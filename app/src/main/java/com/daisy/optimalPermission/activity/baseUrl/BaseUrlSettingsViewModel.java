package com.daisy.optimalPermission.activity.baseUrl;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.daisy.optimalPermission.pojo.response.Url;
import com.daisy.optimalPermission.utils.Constraint;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose -  BaseUrlSettingsViewModel is an model class that contains all predefine urls and if need then return to view
 * Responsibility - BaseUrlSettingsViewModel contains url list that help to show all predefine url to view
 **/
public class BaseUrlSettingsViewModel extends AndroidViewModel {

    private List<Url> urls;
    private Url url;

    public BaseUrlSettingsViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Responsibility - getUrls method is an method that returns list of urls
     * Parameters - No parameter
     **/
    public List<Url> getUrls() {
        return urls;
    }

    /**
     * Responsibility - setUrls method is an method that sets url in list
     * Parameters - its take url list
     **/
    public void setUrls(List<Url> urls) {
        this.urls = urls;
    }

    /**
     * Responsibility - setDefaultUrls method is used for adding all static url
     * Parameters - No parameters
     **/
    public void setDefaultUrls() {

        List<Url> urls = new ArrayList<>();
        Url url = new Url();
        url.setName(Constraint.SEND_BOX_SERVER_NAME);
        url.setUrl(Constraint.SEND_BOX_SERVER_URL);
        urls.add(url);
        Url url1 = new Url();
        url1.setName(Constraint.VZ_SERVER_NAME);
        url1.setUrl(Constraint.VZ_SERVER_URL);
        urls.add(url1);
        Url url2 = new Url();
        url2.setName(Constraint.TM_SERVER_NAME);
        url2.setUrl(Constraint.TM_SERVER_URL);
        urls.add(url2);
        Url url3 = new Url();
        url3.setName(Constraint.DEMO_SERVER_NAME);
        url3.setUrl(Constraint.DEMO_SERVER_URL);
        urls.add(url3);
        Url url4 = new Url();
        url4.setName(Constraint.OAK_DEV_SERVER_NAME);
        url4.setUrl(Constraint.OAK_DEV_SERVER_URL);
        Url url5 = new Url();
        url5.setUrl(Constraint.OAK_TEST_SERVER_URL);
        url5.setName(Constraint.OAK_TEST_SERVER_NAME);
        urls.add(url4);
        urls.add(url5);
        Url url6 = new Url();
        url6.setName(Constraint.OAK_SERVER_NAME);
        url6.setUrl(Constraint.OAK_SERVER_URL);
        Url url7 = new Url();
        url7.setUrl(Constraint.VZPROD_SERVER_URL);
        url7.setName(Constraint.VZPROD_SERVER_NAME);
        urls.add(url6);
        urls.add(url7);
        Url url8 = new Url();

        url8.setUrl(Constraint.USE_SERVER_URL);
        url8.setName(Constraint.USE_SERVER_NAME);
        urls.add(url8);

        setUrls(urls);
    }

    /**
     * Responsibility - getUrl method is used for return selected url
     * Parameters - No parameters
     **/
    public Url getUrl() {
        return url;
    }

    /**
     * Responsibility - setUrl method is used for set selected url
     * Parameters - Its take url object that selected by user from view
     **/
    public void setUrl(Url url) {
        this.url = url;
    }


}
