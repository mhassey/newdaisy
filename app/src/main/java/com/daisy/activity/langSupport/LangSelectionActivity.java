package com.daisy.activity.langSupport;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.configSettings.ConfigSettings;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.adapter.LangSupportAdaptor;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityLangSelectionBinding;
import com.daisy.interfaces.CallBack;
import com.daisy.interfaces.LangSupportCallBack;
import com.daisy.pojo.LangPojo;
import com.daisy.utils.ValidationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
     * initialize variable
     */
    private void initView() {
        context = this;
        setNoTitleBar(this);
        langSupportViewModel = new ViewModelProvider(this).get(LangSupportViewModel.class);
        langSupportViewModel.setLangData(context);
    }

    private void initClick() {
        binding.back.setOnClickListener(this::onClick);
        binding.confirmLang.setOnClickListener(this::onClick);
    }

    private void setLangAdaptor() {

        langSupportAdaptor = new LangSupportAdaptor(langSupportViewModel.getLangPojosForAdaptor(),this);
        binding.lang.setLayoutManager(new LinearLayoutManager(context));
        binding.lang.setAdapter(langSupportAdaptor);

    }



    @Override
    public RecyclerView.ViewHolder getHolder() {
        return langSupportViewModel.getViewHolder();
    }

    //TODO  Handle Item click
    @Override
    public void setLangPojo(LangPojo langPojo, RecyclerView.ViewHolder viewHolder) {
        langSupportViewModel.setViewHolder(viewHolder);
        langSupportViewModel.setSelectedLanguage(langPojo);
        binding.confirmLang.setVisibility(View.VISIBLE);
    }

    //TODO Set Default language
    @Override
    public void setDefaultLang(LangPojo langPojo, RecyclerView.ViewHolder holder) {
        langSupportViewModel.setViewHolder(holder);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.confirmLang:
            {
                changeLang();
                break;
            }
            case R.id.back:
            {
                finish();
                break;
            }
        }
    }

    //TODO  Change language
    private void changeLang() {
       LangPojo langPojo= langSupportViewModel.getSelectedLanguage();
        if (langPojo!=null)
        {
            setLang(langPojo.getKey());
        }
        else
        {
            ValidationHelper.showToast(context,getString(R.string.please_select_lang));
        }
    }

    /**
     * Set language
     */
    private void setLang (String s){
        Locale locale = new Locale(s);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SessionManager.get().setLang(s);
        Intent i = new Intent(LangSelectionActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
    /**
     * Change system ui to full screen when any change perform in activity
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }


    /**
     * Handle full screen mode
     */
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
