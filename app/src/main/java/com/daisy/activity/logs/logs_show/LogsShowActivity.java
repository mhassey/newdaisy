package com.daisy.activity.logs.logs_show;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.adapter.LogsAdapter;
import com.daisy.databinding.ActivityLogsShowBinding;
import com.daisy.pojo.Logs;
import com.daisy.pojo.request.LogClearRequest;
import com.daisy.pojo.request.LogsRequest;
import com.daisy.pojo.response.LogClearResponse;
import com.daisy.utils.Constraint;
import com.daisy.utils.ValidationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose -  LogsShowActivity is an activity that help to show list of logs here user can clear logs and exit from list
 * Responsibility - Main responsibility to show logs in recycle view according to request type and when user scroll the page then add new items in list
 **/
public class LogsShowActivity extends BaseActivity implements View.OnClickListener {
    private ActivityLogsShowBinding mBinding;
    private Context context;
    private List<Logs> list =null;
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


    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        context = this;
        setNoTitleBar(this);
        list= new ArrayList<>();
        viewModel = new ViewModelProvider(this).get(LogsShowViewModel.class);
        viewModel.setType(getIntent().getStringExtra(Constraint.TYPEE));
        layoutManager = new LinearLayoutManager(context);
        mBinding.logsList.setLayoutManager(layoutManager);


    }

    /**
     * Responsibility - setDataInRecycleView method is used for set list in adaptor and set adaptor to list
     * Parameters - No parameter
     **/
    private void setDataInRecycleView() {
        logsAdapter = new LogsAdapter(list, context);
        mBinding.logsList.setAdapter(logsAdapter);
        getLogsData();
        notifyItems();

    }

    /**
     * Responsibility - setOnClickListener is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void setOnClickListener() {
        mBinding.backClick.setOnClickListener(this);
        mBinding.clearAndBack.setOnClickListener(this);
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
     * Responsibility - getLogsData is an method that used for get logs from database according to request data and set value to adaptor
     * Parameters - No parameter
     **/
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


    /**
     * Responsibility - notifyItems is an method that used for  check list data is empty and maintain list accordingly
     * Parameters - No parameter
     **/
    private void notifyItems() {
        if (list.isEmpty()) {
            mBinding.logsList.setVisibility(View.GONE);
            mBinding.emptyView.setVisibility(View.VISIBLE);
        } else {
            mBinding.logsList.setVisibility(View.VISIBLE);
            mBinding.emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Handle  exit log
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel.getType().equals(Constraint.APPLICATION_LOGS)) {
  //          DBCaller.storeLogInDatabase(context, Constraint.EXIT_APPLICATION_LOG, Constraint.EXIT_APPLICATION_LOG_DESCRIPTION, "", Constraint.APPLICATION_LOGS);

        } else if (viewModel.getType().equals(Constraint.CARD_LOGS)) {
    //        DBCaller.storeLogInDatabase(context, Constraint.EXIT_CARD_LOG, Constraint.EXIT_CARD_LOG_DESCRIPTION, "", Constraint.APPLICATION_LOGS);

        }
    }


    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
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

    /**
     * Responsibility - clearLogHandler is an method that use to clear logs of particular type
     * Parameters - No parameter
     **/
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
                handleClearLogResponse(logClearResponse);
                }
            });
        }
    }

    /**
     * Responsibility - handleClearLogResponse is an method that helps to enter a logs says user clear the logs if clear logs request is successfully performed
     * Parameters - No parameter
     **/
    private void handleClearLogResponse(LogClearResponse logClearResponse) {
        if (logClearResponse.isClear()) {
//            if (viewModel.getType().equals(Constraint.APPLICATION_LOGS))
//           //     DBCaller.storeLogInDatabase(context, Constraint.CLEAR_APPLICATION_LOG, Constraint.CLEAR_APPLICATION_LOG_DESCRIPTION, "", Constraint.APPLICATION_LOGS);
//            else if (viewModel.getType().equals(Constraint.CARD_LOGS))
//             //   DBCaller.storeLogInDatabase(context, Constraint.CLEAR_CARD_LOG, Constraint.CLEAR_CARD_LOG_DESCRIPTION, "", Constraint.APPLICATION_LOGS);

            finish();
        } else {
            ValidationHelper.showToast(context, getString(R.string.some_issue_occur));
        }
    }
}

