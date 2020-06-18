package com.daisy.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivitySettingsBinding;

public class Settings extends BaseActivity implements SwitchCompat.OnCheckedChangeListener {
    private ActivitySettingsBinding mBinding;
    private Context context;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNoTitleBar(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        initView();
        initClick();
    }


    private void initView() {
        context = this;
        sessionManager=SessionManager.get();
         boolean isChecked=sessionManager.getDarkTheme();
         mBinding.switchButton.setChecked(isChecked);

    }

    private void initClick() {
        mBinding.switchButton.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sessionManager.darkMode(isChecked);
        if (isChecked)
        {
            setTheme(R.style.AppThemeDark);
        }
        else
        {
            setTheme(R.style.AppTheme);

        }
        restartApp();
    }

    private void restartApp() {
        Intent intent=new Intent(Settings.this, EditorTool.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constraint.SETTINGS,Constraint.SETTINGS);
        startActivity(intent);
        finish();
    }
}
