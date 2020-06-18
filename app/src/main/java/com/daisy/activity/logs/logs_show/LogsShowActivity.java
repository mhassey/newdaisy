package com.daisy.activity.logs.logs_show;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.adapter.LogsAdapter;
import com.daisy.common.Constraint;
import com.daisy.databinding.ActivityLogsShowBinding;
import com.daisy.pojo.Logs;
import com.daisy.pojo.request.LogClearRequest;
import com.daisy.pojo.request.LogsRequest;
import com.daisy.pojo.response.LogClearResponse;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.ArrayList;
import java.util.List;

public class LogsShowActivity extends BaseActivity implements View.OnClickListener {
    private ActivityLogsShowBinding mBinding;
    private Context context;
    private List<Logs> list = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private LogsAdapter logsAdapter;
    private LogsShowViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_logs_show);
        initView();
        setOnClickListener();
        setDataInRecycleView();
    }


    private void initView() {
        context = this;
        setNoTitleBar(this);
        viewModel = new ViewModelProvider(this).get(LogsShowViewModel.class);
        viewModel.setType(getIntent().getStringExtra(Constraint.TYPEE));
        layoutManager = new LinearLayoutManager(context);
        mBinding.logsList.setLayoutManager(layoutManager);


    }

    private void setOnClickListener() {
        mBinding.backClick.setOnClickListener(this);
        mBinding.clearAndBack.setOnClickListener(this);
    }

    private void setDataInRecycleView() {
        logsAdapter = new LogsAdapter(list, context);
        mBinding.logsList.setAdapter(logsAdapter);
        getLogsData();
        notifyItems();

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


    private void getLogsData() {
        LogsRequest logsRequest = new LogsRequest();
        logsRequest.setContext(this);
        logsRequest.setType(viewModel.getType());
        viewModel.setMutableLiveData(logsRequest);
        LiveData<List<Logs>> liveData = viewModel.getLogResponse();
        if (!liveData.hasActiveObservers()) {
            liveData.observe(this, new Observer<List<Logs>>() {
                @Override
                public void onChanged(List<Logs> logs) {
                    list.addAll(logs);
                    logsAdapter.notifyDataSetChanged();
                    notifyItems();
                }
            });
        }
    }

    private void notifyItems() {
        if (list.isEmpty()) {
            mBinding.logsList.setVisibility(View.GONE);
            mBinding.emptyView.setVisibility(View.VISIBLE);
        } else {
            mBinding.logsList.setVisibility(View.VISIBLE);
            mBinding.emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel.getType().equals(Constraint.APPLICATION_LOGS)) {
            Utils.storeLogInDatabase(context, Constraint.EXIT_APPLICATION_LOG, Constraint.EXIT_APPLICATION_LOG_DESCRIPTION, "", Constraint.APPLICATION_LOGS);

        } else if (viewModel.getType().equals(Constraint.CARD_LOGS)) {
            Utils.storeLogInDatabase(context, Constraint.EXIT_CARD_LOG, Constraint.EXIT_CARD_LOG_DESCRIPTION, "", Constraint.APPLICATION_LOGS);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backClick: {
                finish();
                break;
            }
            case R.id.clear_and_back: {

                clearLogHandler();
                break;
            }
        }
    }

    private void clearLogHandler() {
        LogClearRequest logClearRequest = new LogClearRequest();
        logClearRequest.setContext(context);
        logClearRequest.setType(viewModel.getType());
        viewModel.clearLogMutableRequest(logClearRequest);
        LiveData<LogClearResponse> liveData = viewModel.getLogClearResponseLiveData();
        if (!liveData.hasActiveObservers()) {
            liveData.observe(this, new Observer<LogClearResponse>() {
                @Override
                public void onChanged(LogClearResponse logClearResponse) {
                    if (logClearResponse.isClear()) {
                        if (viewModel.getType().equals(Constraint.APPLICATION_LOGS))
                            Utils.storeLogInDatabase(context, Constraint.CLEAR_APPLICATION_LOG, Constraint.CLEAR_APPLICATION_LOG_DESCRIPTION, "", Constraint.APPLICATION_LOGS);
                        else if (viewModel.getType().equals(Constraint.CARD_LOGS))
                            Utils.storeLogInDatabase(context, Constraint.CLEAR_CARD_LOG, Constraint.CLEAR_CARD_LOG_DESCRIPTION, "", Constraint.APPLICATION_LOGS);

                        finish();
                    } else {
                        ValidationHelper.showToast(context, getString(R.string.some_issue_occur));
                    }
                }
            });
        }
    }
}

