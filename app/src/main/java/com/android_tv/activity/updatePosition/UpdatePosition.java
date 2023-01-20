package com.android_tv.activity.updatePosition;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android_tv.R;
import com.android_tv.activity.base.BaseActivity;
import com.android_tv.activity.updatePosition.vo.UpdatePositionResponse;
import com.android_tv.common.session.SessionManager;
import com.android_tv.databinding.ActivityUpdatePositionBinding;
import com.android_tv.pojo.response.GlobalResponse;
import com.android_tv.pojo.response.ScreenPosition;
import com.android_tv.utils.Constraint;
import com.android_tv.utils.Utils;
import com.android_tv.utils.ValidationHelper;

import java.util.HashMap;
import java.util.Locale;

/**
 * Purpose -  LockScreen is an activity that help to show when need to ask password in various conditions
 * Responsibility - Its ask for password when user open play store ,settings ,browser and when we are going to uninstall the app
 **/
public class UpdatePosition extends BaseActivity implements View.OnClickListener {

    private ActivityUpdatePositionBinding mBinding;
    private Context context;
    private SessionManager sessionManager;
    private UpdatePositionViewModel viewModel;
    private UpdateProfileValidationHelper updateProfileValidationHelper;
    final int sdk = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_position);
        initView();
        initClick();
    }


    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        setNoTitleBar(this);
        context = this;
        viewModel = new ViewModelProvider(this).get(UpdatePositionViewModel.class);
        updateProfileValidationHelper=new UpdateProfileValidationHelper(context,mBinding);
        sessionManager = SessionManager.get();
        setDefaultValue();

    }

    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        mBinding.updatePosition.setOnClickListener(this::onClick);
        mBinding.cancel.setOnClickListener(this::onClick);
    }

    /**
     * Responsibility - setDefaultValue is an method that used for set default value in all edittext
     * Parameters - No parameter
     **/
    private void setDefaultValue() {
        ScreenPosition screenPosition = sessionManager.getPosition();
        mBinding.isle.setText(screenPosition.getIsle());
        mBinding.position.setText(screenPosition.getPosition());
        mBinding.shelf.setText(screenPosition.getShelf());
    }

    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updatePosition: {
                if (updateProfileValidationHelper.isValid())
                updatePosition();
                break;
            }
            case R.id.cancel:
            {
              onBackPressed();
                break;
            }
        }
    }

    /**
     * Responsibility - updatePosition is an method that used for update position data on server
     * Parameters - No parameter
     **/
    private void updatePosition() {
        if (Utils.getNetworkState(context)) {
            HashMap<String, String> request = getRequest();
            viewModel.setMutableLiveData(request);
            showHideProgressDialog(true);
            LiveData<GlobalResponse<UpdatePositionResponse>> liveData = viewModel.getUpdatePosition();
            if (!liveData.hasActiveObservers()) {
                liveData.observe(this, new Observer<GlobalResponse<UpdatePositionResponse>>() {
                    @Override
                    public void onChanged(GlobalResponse<UpdatePositionResponse> updatePositionResponseGlobalResponse) {
                        handleResponse(updatePositionResponseGlobalResponse);
                    }
                });
            }
        }
    }

    /**
     * Responsibility - updatePosition is an method that used for handle response sanded by updatePosition method
     * Parameters - No parameter
     **/
    private void handleResponse(GlobalResponse<UpdatePositionResponse> updatePositionResponse) {
        showHideProgressDialog(false);
        if (updatePositionResponse != null) {
            if (updatePositionResponse.isApi_status()) {
               // DBCaller.storeLogInDatabase(context,getString(R.string.position_update),"","",Constraint.APPLICATION_LOGS);
                sessionManager.setScreenPosition(updatePositionResponse.getResult().getScreenPosition());
            }
            ValidationHelper.showToast(context, updatePositionResponse.getMessage());
        } else {
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }
    }

    /**
     * Responsibility - getRequest is an method that used for create update position request
     * Parameters - No parameter
     **/
    private HashMap<String, String> getRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.ISLE, mBinding.isle.getText().toString());
        hashMap.put(Constraint.SHELF, mBinding.shelf.getText().toString());
        hashMap.put(Constraint.POSITION, mBinding.position.getText().toString());
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }

    @Override
    protected void onStart() {
        if (Locale.getDefault().getLanguage().equals(Constraint.AR)) {
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mBinding.updatePosition.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_update_position_rtl) );
            } else {
                mBinding.updatePosition.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_update_position_rtl));
            }
        }
        super.onStart();

    }

}
