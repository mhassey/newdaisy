package com.daisy.optimalPermission.activity.langSupport;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daisy.R;
import com.daisy.optimalPermission.activity.base.BaseActivity;
import com.daisy.optimalPermission.activity.mainActivity.MainActivity;
import com.daisy.optimalPermission.adapter.LangSupportAdaptor;
import com.daisy.optimalPermission.session.SessionManager;
import com.daisy.databinding.ActivityLangSelectionBinding;
import com.daisy.optimalPermission.interfaces.LangSupportCallBack;
import com.daisy.optimalPermission.pojo.LangPojo;
import com.daisy.optimalPermission.utils.ValidationHelper;

import java.util.Locale;

/**
 * Purpose -  LangSelectionActivity is an activity that help to select lang that will reflect in hole app
 * Responsibility - Its show all language in an screen and when user select any language and just click right icon then the selected language will reflect in hole app
 **/
public class LangSelectionActivity extends BaseActivity implements LangSupportCallBack, View.OnClickListener {

    private ActivityLangSelectionBinding binding;
    private Context context;
    private LangSupportAdaptor langSupportAdaptor;
    private LangSupportViewModel langSupportViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lang_selection);
        initView();
        initClick();
        setLangAdaptor();

    }


    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        context = this;
        setNoTitleBar(this);
        langSupportViewModel = new ViewModelProvider(this).get(LangSupportViewModel.class);
        langSupportViewModel.setLangData(context);
    }

    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        binding.back.setOnClickListener(this::onClick);
        binding.confirmLang.setOnClickListener(this::onClick);
    }

    /**
     * Responsibility - setLangAdaptor is an method that used for assigning the value to adaptor and pass it to lang recycle view
     * Parameters - No parameter
     **/
    private void setLangAdaptor() {

        langSupportAdaptor = new LangSupportAdaptor(langSupportViewModel.getLangPojosForAdaptor(), this);
        binding.lang.setLayoutManager(new LinearLayoutManager(context));
        binding.lang.setAdapter(langSupportAdaptor);

    }


    @Override
    public RecyclerView.ViewHolder getHolder() {
        return langSupportViewModel.getViewHolder();
    }


    /**
     * Responsibility - setLangPojo is an call back method which help to get which language user select
     * Parameters - No parameter
     **/
    @Override
    public void setLangPojo(LangPojo langPojo, RecyclerView.ViewHolder viewHolder) {
        langSupportViewModel.setViewHolder(viewHolder);
        langSupportViewModel.setSelectedLanguage(langPojo);
        binding.confirmLang.setVisibility(View.VISIBLE);
    }

    /**
     * Responsibility - setDefaultLang is an call back method which help get default language
     * Parameters - No parameter
     **/
    @Override
    public void setDefaultLang(LangPojo langPojo, RecyclerView.ViewHolder holder) {
        langSupportViewModel.setViewHolder(holder);

    }

    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmLang: {
                changeLang();
                break;
            }
            case R.id.back: {
                finish();
                break;
            }
        }
    }

    /**
     * Responsibility - changeLang method help to get selected lang then pass to setLang method that will update app language
     * Parameters - No parameter
     **/
    private void changeLang() {
        LangPojo langPojo = langSupportViewModel.getSelectedLanguage();
        if (langPojo != null) {
            setLang(langPojo.getKey());
        } else {
            ValidationHelper.showToast(context, getString(R.string.please_select_lang));
        }
    }

    /**
     * Responsibility - setLang method help to set app language
     * Parameters - No parameter
     **/
    private void setLang(String s) {
        try {
            Locale locale = new Locale(s);
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
            SessionManager.get().setLang(s);
            Intent i = new Intent(LangSelectionActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } catch (Exception e) {

        }
    }

    /**
     * Responsibility - onWindowFocusChanged method is an override function that call when any changes perform on ui
     * Parameters - its take boolean hasFocus that help to know out app is in focused or not
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }


    /**
     * Responsibility - hideSystemUI method is an default method that help to change app ui to full screen when any change perform in activity
     * Parameters - No parameter
     **/
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ViewGroup.LayoutParams params = binding.rootLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.rootLayout.requestLayout();

    }

}
