package com.daisy.activity.onBoarding.slider.slides.wifiAsk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.daisy.R;
import com.daisy.activity.base.BaseFragment;
import com.daisy.activity.onBoarding.slider.OnBoarding;
import com.daisy.activity.onBoarding.slider.slides.welcome.WelcomeAsk;
import com.daisy.databinding.WifiAskBinding;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;

public class WifiAsk extends BaseFragment implements View.OnClickListener {

    private WifiAskBinding wifiAskBinding;
    private static OnBoarding baording;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        wifiAskBinding = DataBindingUtil.inflate(inflater, R.layout.wifi_ask, container, false);
        initView();
        initClick();
        return wifiAskBinding.getRoot();
    }

    private void initView() {
        registerWifiReceiver();
    }

    private void registerWifiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(wifiStateReceiver, intentFilter);
    }


    private void initClick() {
        wifiAskBinding.next.setOnClickListener(this);
        wifiAskBinding.tapToOpenWifi.setOnClickListener(this);
    }

    // getInstance method is used for getting signup object
    public static WifiAsk getInstance(OnBoarding onBoarding) {
        baording = onBoarding;
        return new WifiAsk();
    }

    @Override
    public void onResume() {
        super.onResume();
        designWork();
        handleResumePermission();

    }

    // Change design at run time
    private void designWork() {

        baording.mBinding.tabDotsLayout.getTabAt(Constraint.ZERO).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.ONE).setIcon(getResources().getDrawable(R.drawable.selected_dot_pink));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.TWO).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.THREE).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.FOUR).setIcon(getResources().getDrawable(R.drawable.default_dot));
        baording.mBinding.tabDotsLayout.getTabAt(Constraint.FIVE_INE_REAL).setIcon(getResources().getDrawable(R.drawable.default_dot));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tap_to_open_wifi: {
                goToWifi();
                break;
            }
            case R.id.next: {
                handleNext();
                break;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(wifiStateReceiver);
    }

    /**
     * Ge to wifi screen
     */
    private void goToWifi() {

        Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        if (Utils.getDeviceName().contains(getString(R.string.onePlus))) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        }
        this.startActivityForResult(intent, Constraint.NINE_THOUSANT_NINE_HUNDRED);

    }

    private void handleNext() {
        baording.counterPlus();
    }


    /**
     * Purpose - wifiStateReceiver method handles wifi state change
     */
    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            InternetResponse internetResponse = new InternetResponse();
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLED:
                case WifiManager.WIFI_STATE_DISABLED: {
                    handleResumePermission();
                    break;
                }
            }

        }
    };


    private void handleResumePermission() {
        boolean wifiPermission = Utils.checkWifiState(getActivity());


        if (wifiPermission) {
            wifiAskBinding.next.setVisibility(View.VISIBLE);
            wifiAskBinding.tapToOpenWifi.setVisibility(View.VISIBLE);
        } else {
            wifiAskBinding.tapToOpenWifi.setVisibility(View.VISIBLE);
            wifiAskBinding.next.setVisibility(View.GONE);


        }
    }

}
