package com.daisy.activity.updatePosition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.updatePosition.vo.UpdatePositionResponse;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.ActivityUpdatePositionBinding;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.ScreenPosition;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.HashMap;

public class UpdatePosition extends BaseActivity implements View.OnClickListener {

    private ActivityUpdatePositionBinding mBinding;
    private Context context;
    private SessionManager sessionManager;
    private UpdatePositionViewModel viewModel;
    private UpdateProfileValidationHelper updateProfileValidationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_position);
        initView();
        initClick();
    }


    private void initView() {
        setNoTitleBar(this);
        context = this;
        viewModel = new ViewModelProvider(this).get(UpdatePositionViewModel.class);
        updateProfileValidationHelper=new UpdateProfileValidationHelper(context,mBinding);
        sessionManager = SessionManager.get();
        setDefaultValue();

    }

    private void initClick() {
        mBinding.updatePosition.setOnClickListener(this::onClick);
    }

    private void setDefaultValue() {
        ScreenPosition screenPosition = sessionManager.getPosition();
        mBinding.isle.setText(screenPosition.getIsle());
        mBinding.position.setText(screenPosition.getPosition());
        mBinding.shelf.setText(screenPosition.getShelf());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updatePosition: {
                if (updateProfileValidationHelper.isValid())
                updatePosition();
                break;
            }
        }
    }

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

    private void handleResponse(GlobalResponse<UpdatePositionResponse> updatePositionResponse) {
        showHideProgressDialog(false);
        if (updatePositionResponse != null) {
            if (updatePositionResponse.isApi_status()) {
                DBCaller.storeLogInDatabase(context,Constraint.POSITION_UPDATE,"","",Constraint.APPLICATION_LOGS);
                sessionManager.setScreenPosition(updatePositionResponse.getResult().getScreenPosition());
            }
            ValidationHelper.showToast(context, updatePositionResponse.getMessage());
        } else {
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }
    }

    private HashMap<String, String> getRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.ISLE, mBinding.isle.getText().toString());
        hashMap.put(Constraint.SHELF, mBinding.shelf.getText().toString());
        hashMap.put(Constraint.POSITION, mBinding.position.getText().toString());
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }
}
