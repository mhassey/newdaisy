package com.daisy.activity.refreshTimer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.checkCardAvailability.CheckCardAvailability;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityRefreshTimerBinding;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Time;
import com.daisy.service.BackgroundService;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;
import com.google.gson.JsonObject;

import java.util.HashMap;

import javax.xml.validation.ValidatorHandler;

public class RefreshTimer extends BaseActivity implements OnClickListener {

    private ActivityRefreshTimerBinding mBinding;
    private Context context;
    private SessionManager sessionManager;
    private GetCardViewModel getCardViewModel;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_refresh_timer);
        getCardViewModel = new ViewModelProvider(this).get(GetCardViewModel.class);
        initView();
        initClick();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        context = this;
        setNoTitleBar(this);
        sessionManager = SessionManager.get();
        setTimerValue();
    }

    private void initClick() {
        mBinding.setTime.setOnClickListener(this::onClick);
        mBinding.directUpdate.setOnClickListener(this::onClick);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTimerValue() {
        mBinding.timePicker.setIs24HourView(true);
        Time time = sessionManager.getTimeData();
        if (time != null) {
            mBinding.timePicker.setHour(time.getHour());
            mBinding.timePicker.setMinute(time.getMinit());
        } else {
            mBinding.timePicker.setHour(1);
            mBinding.timePicker.setMinute(0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setTime: {
                setTimerClick();
                break;
            }
            case R.id.directUpdate: {
                directUpdate();
            }
        }
    }

    private void directUpdate() {
        if (Utils.getNetworkState(context)) {
            showHideProgressDialog(true);
            getCardViewModel.setMutableLiveData(getCardRequest());
            LiveData<GlobalResponse<GetCardResponse>> liveData = getCardViewModel.getLiveData();
            if (!liveData.hasActiveObservers()) {
                liveData.observe(this, new Observer<GlobalResponse<GetCardResponse>>() {
                    @Override
                    public void onChanged(GlobalResponse<GetCardResponse> response) {
                        showHideProgressDialog(false);
                        if (response.isApi_status()) {
                            if (response.getResult() != null) {
                                redirectToMain();
                            } else {
                                ValidationHelper.showToast(context, getString(R.string.no_data_available));
                            }
                        } else {
                            ValidationHelper.showToast(context, getString(R.string.no_data_available));

                        }
                    }
                });
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }
    }

    private void redirectToMain() {
        Intent i = new Intent(RefreshTimer.this, MainActivity.class);
// set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private HashMap<String, String> getCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.SCREEN_ID, sessionManager.getScreenId() + "");
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTimerClick() {
        int hour=mBinding.timePicker.getHour();
        int minute=mBinding.timePicker.getMinute();
        if (hour==0 && minute==0)
        {
            ValidationHelper.showToast(context,getString(R.string.invald_time));
            return;
        }
        Time time = new Time();
        time.setHour(mBinding.timePicker.getHour());
        time.setMinit(mBinding.timePicker.getMinute());
        sessionManager.setTimerToGetCard(time);
        BackgroundService.refreshTimer.cancel();
        BackgroundService.checkUpdate();
        ValidationHelper.showToast(context,getString(R.string.refresh_time_set));
    }
}

