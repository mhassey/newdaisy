package com.daisyy.activity.logs;

import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import com.daisyy.R;
import com.daisyy.activity.base.BaseActivity;
import com.daisyy.activity.logs.logs_show.LogsShowActivity;
import com.daisyy.activity.settings.Settings;
import com.daisyy.database.DBCaller;
import com.daisyy.interfaces.SyncLogCallBack;
import com.daisyy.pojo.Logs;
import com.daisyy.sync.SyncLogs;
import com.daisyy.utils.Constraint;
import com.daisyy.databinding.ActivityLogsMainBinding;
import com.daisyy.utils.Utils;

import java.util.List;

/**
 * Purpose -  LogsMainActivity is an activity that helps to show all type of logs redirect button
 * Responsibility - Here when user clicks on any type of logs button then open logs list accordingly
 **/
public class LogsMainActivity extends BaseActivity implements View.OnClickListener, SyncLogCallBack {

    private ActivityLogsMainBinding mBinding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_logs_main);
        initView();
        initClickListener();
    }

    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        setNoTitleBar(this);
        context = this;

    }



    /**
     * Responsibility - initClickListener is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClickListener() {
        mBinding.applicationLogs.setOnClickListener(this);
        mBinding.cardLogs.setOnClickListener(this);
        mBinding.settings.setOnClickListener(this);
        mBinding.promotionLogs.setOnClickListener(this::onClick);
        mBinding.cancel.setOnClickListener(this::onClick);
        mBinding.syncAllLogs.setOnClickListener(this::onClick);

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

    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.applicationLogs: {
                callLogsShowActivity(Constraint.APPLICATION_LOGS);

                break;
            }
            case R.id.cardLogs: {
                callLogsShowActivity(Constraint.PRICECARD_LOG);
                break;
            }
            case R.id.promotionLogs: {
                callLogsShowActivity(Constraint.PROMOTION);
                break;

            }
            case R.id.settings: {
                handleSettingClick();
                break;
            }
            case R.id.cancel: {
                onBackPressed();
                break;
            }
            case R.id.syncAllLogs: {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handleSyncing();
                    }
                }).start();
                break;
            }

        }
    }

    /**
     * Responsibility - HandleSettingClick open redirect app to settings page
     * Parameters - No parameter
     **/
    private void handleSettingClick() {
        Intent intent=new Intent(context, Settings.class);
        startActivity(intent);

    }


    private void handleSyncing() {
        if (Utils.getNetworkState(getApplicationContext())) {

            List<Logs> logsVOList = DBCaller.getLogsFromDatabaseNotSync(getApplicationContext());
            if (logsVOList.isEmpty()) {

                new LogSyncExtra(this,true).fireLogExtra();
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showHideProgressDialog(true);
                    }
                });
                SyncLogs syncLogs = SyncLogs.getLogsSyncing(getApplicationContext());
                syncLogs.saveContactApi(Constraint.APPLICATION_LOGS, this::syncDone);
            }

        }

    }


    /**
     * Responsibility - callLogsShowActivity open list of logs according to its type
     * Parameters - Its takes type that help to know which type of logs user want to seee
     **/
    void callLogsShowActivity(String type)
    {
        Intent intent=new Intent(context, LogsShowActivity.class);
        intent.putExtra(Constraint.TYPEE,type);
        startActivity(intent);
    }



    @Override
    public void syncDone(String val, int index) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showHideProgressDialog(false);
                }
            });
            List<Integer> integers = DBCaller.getPromotionCountByID(getApplicationContext());

            if (val.equals(Constraint.APPLICATION_LOGS)) {
                if (integers != null) {
                    if (integers.size() > 0) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                showHideProgressDialog(true);
                            }
                        });
                        SyncLogs syncLogsPromotion = SyncLogs.getLogsSyncing(getApplicationContext());
                        syncLogsPromotion.saveContactApi(Constraint.PROMOTION, integers.get(0));

                    }
                }

            } else if (val.equals(Constraint.PROMOTION)) {
                if (integers.size() > 0) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            showHideProgressDialog(true);
                        }
                    });

                    SyncLogs syncLogsPromotion = SyncLogs.getLogsSyncing(getApplicationContext());
                    syncLogsPromotion.saveContactApi(Constraint.PROMOTION, integers.get(0));
                }
            }
        } catch (Exception e) {

        }
    }
}

