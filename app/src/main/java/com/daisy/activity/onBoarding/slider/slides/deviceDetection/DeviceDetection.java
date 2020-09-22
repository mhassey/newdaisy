package com.daisy.activity.onBoarding.slider.slides.deviceDetection;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daisy.R;
import com.daisy.database.DBCaller;
import com.daisy.databinding.FragmentDeviceDetectionBinding;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;


public class DeviceDetection extends Fragment {

    private FragmentDeviceDetectionBinding detectionBinding;
    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        detectionBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_device_detection, container, false);
    return detectionBinding.getRoot();
    }
    public static DeviceDetection getInstance() {
        return new DeviceDetection();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setDefaultValues();
    }

    private void initView() {
        context=getContext();
        DBCaller.storeLogInDatabase(context, getString(R.string.load_add_screen),"","",Constraint.APPLICATION_LOGS);

    }

    private void setDefaultValues() {
    detectionBinding.deviceName.setText(Utils.getDeviceName());
    }
}
