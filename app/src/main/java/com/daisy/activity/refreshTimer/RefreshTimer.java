package com.daisy.activity.refreshTimer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.app.AppController;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityRefreshTimerBinding;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Time;
import com.daisy.service.BackgroundService;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Purpose -  RefreshTimer is an activity that help to take card update or set a timer for new update request
 * Responsibility - Its takes update directly by direct update button or set timer for firing request
 **/
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


    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        context = this;
        setNoTitleBar(this);
        sessionManager = SessionManager.get();
        setTimerValue();
    }

    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        mBinding.setTime.setOnClickListener(this::onClick);
        mBinding.directUpdate.setOnClickListener(this::onClick);
        mBinding.cancel.setOnClickListener(this::onClick);
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
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ViewGroup.LayoutParams params = mBinding.rootLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mBinding.rootLayout.requestLayout();

    }

    /**
     * Responsibility - setTimerValue method is used for set timer value if session contains some previous value then show that if not then set timer to one hour
     * Parameters - No parameter
     **/
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

    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/

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
                break;
            }
            case R.id.cancel: {
                onBackPressed();
                break;
            }
        }
    }

    /**
     * Responsibility - directUpdate method is an method that create an card request and call an api for direct update and pass response to handleRefreshTimeResponse method
     * Parameters - No parameter
     **/
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
                            handleRefreshTimeResponse(response);
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

    /**
     * Responsibility -  handleRefreshTimeResponse contains response coming from directUpdate  method here we set some default values and  if new price card promotions comes then pass the urls to download class for downloading
     * Parameters - No parameter
     **/
    private void handleRefreshTimeResponse(GlobalResponse<GetCardResponse> response) {
        if (response.getResult() != null) {
            sessionManager.setOpenTime(response.getResult().getStoreDetails().getOpen());
            sessionManager.setCloseTime(response.getResult().getStoreDetails().getClosed());
            sessionManager.setOffset(response.getResult().getStoreDetails().getUTCOffset());
           sessionManager.setServerTime(response.getResult().getStoreDetails().getCurrentTime());
            sessionManager.setDeviceSecurity(response.getResult().getStoreDetails().getDeviceSecurity());
            sessionManager.setPricingPlainId(response.getResult().getStoreDetails().getPricingPlanID());
         //   Utils.getInvertedTimeWithNewCorrectionFactor();
            if (!response.getResult().isDefault()) {
                if (response.getResult().getPricecard() != null && response.getResult().getPricecard().getFileName() != null) {

                    sessionManager.deleteLocation();
                    sessionManager.deletePromotions();
                    sessionManager.setPriceCard(response.getResult().getPricecard());
                    sessionManager.setPromotion(response.getResult().getPromotions());
                    sessionManager.setPricing(response.getResult().getPricing());
                    sessionManager.setCardDeleted(Constraint.FALSE);
                    redirectToMain(response);

                } else if (response.getResult().getPromotions() != null && !response.getResult().getPromotions().isEmpty()) {
                    sessionManager.setPromotion(response.getResult().getPromotions());

                    Intent i = new Intent(RefreshTimer.this, MainActivity.class);
                    if (response.getResult().getPricing() != null && !response.getResult().getPricing().isEmpty()) {
                        sessionManager.setPricing(response.getResult().getPricing());
                    }
                    i.putExtra(Constraint.PROMOTION, Constraint.TRUE_STR);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else if (response.getResult().getPricing() != null && !response.getResult().getPricing().isEmpty()) {
                    sessionManager.setPricing(response.getResult().getPricing());
                    Intent i = new Intent(RefreshTimer.this, MainActivity.class);
                    i.putExtra(Constraint.PRICING, Constraint.TRUE_STR);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }
            } else {

                if (response.getResult().getPromotions() != null && !response.getResult().getPromotions().isEmpty()) {
                    sessionManager.setPromotion(response.getResult().getPromotions());

                    if (response.getResult().getPricing() != null && !response.getResult().getPricing().isEmpty()) {
                        sessionManager.setPricing(response.getResult().getPricing());
                    }
                    Intent i = new Intent(RefreshTimer.this, MainActivity.class);
                    i.putExtra(Constraint.PROMOTION, Constraint.TRUE_STR);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else if (response.getResult().getPricing() != null && !response.getResult().getPricing().isEmpty()) {
                    sessionManager.setPricing(response.getResult().getPricing());
                    Intent i = new Intent(RefreshTimer.this, MainActivity.class);
                    i.putExtra(Constraint.PRICING, Constraint.TRUE_STR);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                } else {
                    sessionManager.setPricing(null);
                    Intent i = new Intent(RefreshTimer.this, MainActivity.class);
                    i.putExtra(Constraint.PRICING, Constraint.TRUE_STR);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }

            }

        } else {
            ValidationHelper.showToast(context, getString(R.string.no_data_available));
        }
    }

    /**
     * Responsibility -  redirectToMain checks the file path  if any new price card is available set its path to config file and redirect pagr to MainActivity
     * Parameters - No parameter
     **/
    private void redirectToMain(GlobalResponse<GetCardResponse> response) {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
            String UrlPath = response.getResult().getPricecard().getFileName();
            if (response.getResult().getPricecard().getFileName() != null) {
                String configFilePath = File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(getExternalFilesDir(""),configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                   // if (!path.equals(UrlPath)) {
                        Utils.deleteCardFolder();
                        try {
                            Utils.writeFile(configFilePath, UrlPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sessionManager.deleteLocation();

                 //   }
                } else {
                    try {
                        Utils.writeFile(configFilePath, UrlPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                sessionManager.onBoarding(Constraint.TRUE);

                Intent i = new Intent(RefreshTimer.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }
        else {

            Utils.deleteDaisy();
            String UrlPath = response.getResult().getPricecard().getFileName();
            if (response.getResult().getPricecard().getFileName() != null) {
                String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                    if (!path.equals(UrlPath)) {
                        Utils.deleteCardFolder();
                        try {
                            Utils.writeFile(configFilePath, UrlPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sessionManager.deleteLocation();

                    }
                } else {
                    try {
                        Utils.writeFile(configFilePath, UrlPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                sessionManager.onBoarding(Constraint.TRUE);

                Intent i = new Intent(RefreshTimer.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }
    }

    /**
     * Responsibility -  getCardRequest  method create a card request with device token and screen id
     * Parameters - No parameter
     **/
    private HashMap<String, String> getCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.SCREEN_ID, sessionManager.getScreenId() + "");
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        if (sessionManager.getPriceCard() != null)
            hashMap.put(Constraint.pricecardid, sessionManager.getPriceCard().getIdpriceCard());
        return hashMap;
    }

    /**
     * Responsibility -  setTimerClick  method calls when user click on set timer button its take hours and minutes and store it to  session
     * Parameters - No parameter
     **/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTimerClick() {
        int hour = mBinding.timePicker.getHour();
        int minute = mBinding.timePicker.getMinute();
        if (hour == Constraint.ZERO && minute == Constraint.ZERO) {
            ValidationHelper.showToast(context, getString(R.string.invald_time));
            return;
        }
        Time time = new Time();
        time.setHour(mBinding.timePicker.getHour());
        time.setMinit(mBinding.timePicker.getMinute());
        sessionManager.setTimerToGetCard(time);
        if (BackgroundService.refreshTimer != null) {
            BackgroundService.refreshTimer.cancel();
            BackgroundService.checkUpdate();
        }
        ValidationHelper.showToast(context, getString(R.string.refresh_time_set));
    }


}

