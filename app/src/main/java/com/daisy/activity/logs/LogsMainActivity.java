package com.daisy.activity.logs;

import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.logs.logs_show.LogsShowActivity;
import com.daisy.activity.settings.Settings;
import com.daisy.common.Constraint;
import com.daisy.databinding.ActivityLogsMainBinding;
import com.daisy.utils.Utils;

public class LogsMainActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLogsMainBinding mBinding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_logs_main);
        initView();
        initClickListener();
    }


    private void initView() {
        setNoTitleBar(this);
        context = this;

    }

    private void initClickListener() {
        mBinding.applicationLogs.setOnClickListener(this);
        mBinding.cardLogs.setOnClickListener(this);
        mBinding.settings.setOnClickListener(this);
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
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
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
            case R.id.applicationLogs:
            {
                Utils.storeLogInDatabase(context, Constraint.REVIEW_APPLICATION_LOG,Constraint.REVIEW_APPLICATION_LOG_DESCRIPTION,"",Constraint.APPLICATION_LOGS);

                callLogsShowActivity(Constraint.APPLICATION_LOGS);

                break;
            }
            case R.id.cardLogs:
            {
                Utils.storeLogInDatabase(context, Constraint.REVIEW_CARD_LOG,Constraint.REVIEW_CARD_LOG_DESCRIPTION,"",Constraint.APPLICATION_LOGS);

                callLogsShowActivity(Constraint.CARD_LOGS);
                break;
            }
            case R.id.settings:
            {
                handleSettingClick();
                break;
            }
        }

    }

    private void handleSettingClick() {
        Intent intent=new Intent(context, Settings.class);
        startActivity(intent);

    }


    void callLogsShowActivity(String type)
    {
        Intent intent=new Intent(context, LogsShowActivity.class);
        intent.putExtra(Constraint.TYPEE,type);
        startActivity(intent);
    }


}
