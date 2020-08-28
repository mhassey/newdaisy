package com.daisy.activity.configSettings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.logs.LogsMainActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.refreshTimer.RefreshTimer;
import com.daisy.activity.updateBaseUrl.UpdateBaseUrl;
import com.daisy.activity.updatePosition.UpdatePosition;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityConfigSettingsBinding;

import java.util.Locale;

public class ConfigSettings extends BaseActivity implements View.OnClickListener {

    private ActivityConfigSettingsBinding mBinding;
    private Context context;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_config_settings);
        initView();
        initClick();
    }

    private void initView() {
        context = this;
        setNoTitleBar(this);
        sessionManager=SessionManager.get();
    }

    private void initClick() {
        mBinding.logs.setOnClickListener(this);
        mBinding.setRefreshRate.setOnClickListener(this);
        mBinding.updateBaseUrl.setOnClickListener(this);
        mBinding.updatePosition.setOnClickListener(this);
        mBinding.changeLanguage.setOnClickListener(this::onClick);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }

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

        ViewGroup.LayoutParams params = mBinding.rootLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mBinding.rootLayout.requestLayout();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logs: {
                openLogActivity();
                break;
            }
            case R.id.setRefreshRate: {
                openRefreshRate();
                break;
            }
            case R.id.updateBaseUrl: {
                updateBaseUrl();
                break;
            }
            case R.id.updatePosition: {
                openUpdatePositionActivity();
                break;
            }
            case R.id.changeLanguage: {
                changeLanguage();
                break;
            }
        }
    }


    private void openRefreshRate() {
        Intent intent = new Intent(ConfigSettings.this, RefreshTimer.class);
        startActivity(intent);
    }


    private void updateBaseUrl() {
        Intent intent = new Intent(ConfigSettings.this, UpdateBaseUrl.class);
        startActivity(intent);

    }

    private void openUpdatePositionActivity() {
        Intent intent = new Intent(ConfigSettings.this, UpdatePosition.class);
        startActivity(intent);
    }

    private void openLogActivity() {
        Intent intent = new Intent(ConfigSettings.this, LogsMainActivity.class);
        startActivity(intent);
    }

    private void changeLanguage() {
        String[] lang = {getString(R.string.english), getString(R.string.french),getString(R.string.spanish),getString(R.string.postigues)};
        String  loadedLang=sessionManager.getLang();
        int pos=0;
        if (loadedLang!=null && !loadedLang.equals(""))
        {
            if (loadedLang.equals("en"))
            {
                pos=0;
            }
            else if (loadedLang.equals("fr"))
            {
                pos=1;
            }
            else if (loadedLang.equals("es"))
            {
                pos=2;
            }
            else if (loadedLang.equals("pt"))
            {
                pos=3;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose_lang))
                .setSingleChoiceItems(lang, pos, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        if (selectedPosition == 0) {
                            setLang("en");
                        } else if (selectedPosition == 1) {
                            setLang("fr");
                        }
                        else if (selectedPosition==2)
                        {
                            setLang("es");
                        }
                        else if (selectedPosition==3)
                        {
                            setLang("pt");
                        }
                        dialog.dismiss();
                    }
                })
                .show();


    }

    private void setLang(String s) {
        Locale locale=new Locale(s);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        sessionManager.setLang(s);
        Intent i = new Intent(ConfigSettings.this, MainActivity.class);
// set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}
