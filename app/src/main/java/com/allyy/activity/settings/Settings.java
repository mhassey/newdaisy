package com.allyy.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;

import com.allyy.R;
import com.allyy.activity.base.BaseActivity;
import com.allyy.activity.editorTool.EditorTool;
import com.allyy.utils.Constraint;
import com.allyy.common.session.SessionManager;
import com.allyy.databinding.ActivitySettingsBinding;
/**
 * Purpose -  Settings class not in used
 * Responsibility - Not in use
 **/
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

    /**
     * Initial data setup
     */
    private void initView() {
        context = this;
        sessionManager = SessionManager.get();
        boolean isChecked = sessionManager.getDarkTheme();
        mBinding.switchButton.setChecked(isChecked);

    }
    /**
     * Button clicks initializing
     */
    private void initClick() {
        mBinding.switchButton.setOnCheckedChangeListener(this);
    }


    /**
     * Change checked listener
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sessionManager.darkMode(isChecked);
        if (isChecked) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);

        }
        restartApp();
    }

    /**
     * Restart app fro changing theme
     */
    private void restartApp() {
        Intent intent = new Intent(Settings.this, EditorTool.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constraint.SETTINGS, Constraint.SETTINGS);
        startActivity(intent);
        finish();
    }
}
