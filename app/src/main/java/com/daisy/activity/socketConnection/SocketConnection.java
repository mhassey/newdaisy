package com.daisy.activity.socketConnection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.langSupport.LangSelectionActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivitySocketConnectionBinding;
import com.daisy.pojo.response.IpSearched;
import com.daisy.service.BackgroundService;
import com.daisy.service.DeviceSearch;
import com.daisy.utils.ValidationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SocketConnection extends BaseActivity implements View.OnClickListener {
    private ActivitySocketConnectionBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_socket_connection);
        initView();
        initClick();
    }


    private void initView() {
        handleViewState();
    }

    private void handleViewState() {
        if (SessionManager.get().getIpSearched()) {
            mBinding.becomeAdmin.setText(getString(R.string.disable_admin));

        } else {
            mBinding.becomeAdmin.setText(getString(R.string.become_admin));
        }
        mBinding.syncLoader.setVisibility(View.GONE);
    }

    private void initClick() {
        mBinding.becomeAdmin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.become_admin: {

                handleAdmin();
                break;
            }
        }
    }

    private void handleAdmin() {
        if (!SessionManager.get().getIpSearched()) {
            mBinding.syncLoader.setVisibility(View.VISIBLE);
            startService(new Intent(this, DeviceSearch.class));
            // IpSearched(new IpSearched());
        } else {
            SessionManager.get().setIpSearched(false);
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
            handleViewState();

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void IpSearched(IpSearched ipSearched) {
        SessionManager.get().setIpSearched(true);
        ValidationHelper.showToast(this, getString(R.string.device_synced_sucessfully));
        Intent i = new Intent(this, MainActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

    }
}