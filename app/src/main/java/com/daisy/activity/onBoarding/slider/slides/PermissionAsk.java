package com.daisy.activity.onBoarding.slider.slides;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daisy.R;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.common.Constraint;
import com.daisy.databinding.ActivityOnBaordingBinding;
import com.daisy.databinding.FragmentPermissionAskBinding;
import com.daisy.pojo.response.PermissionDone;
import com.daisy.utils.PermissionManager;
import com.daisy.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PermissionAsk extends Fragment implements View.OnClickListener {
    private static ActivityOnBaordingBinding onBaordingBindingMain;
    private FragmentPermissionAskBinding permissionAskBinding;
    private boolean grandMediaPermission = false;
    private boolean grandDisplayOverTheApp = false;
    private boolean grandModifySystemSettings = false;
    private boolean grandUsageAccess = false;
    private boolean grandBatteyOptimization = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        permissionAskBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_permission_ask, container, false);
        return permissionAskBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        permissionSetter();
        initClick();
    }

    private void initClick() {
        permissionAskBinding.grandMediaPermission.setOnClickListener(this);
        permissionAskBinding.modifySystemSettings.setOnClickListener(this);
        permissionAskBinding.usageAccess.setOnClickListener(this);
        permissionAskBinding.displayOverTheApp.setOnClickListener(this);
        permissionAskBinding.dontOptimizedBattery.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionSetter() {
        checkDisplayOverTheApp();
        checkAccessUsage();
        modifySystemSettings();
        mediaPermission();
        checkForOnePlus();
        String name = Utils.getDeviceName();
        if (!name.contains(getString(R.string.onePlus))) {

            if (grandMediaPermission && grandModifySystemSettings && grandUsageAccess && grandDisplayOverTheApp) {
                Log.e("ok", "enable");
                onBaordingBindingMain.nextSlide.setVisibility(View.VISIBLE);
            } else {
                Log.e("ok", "not enable");

                onBaordingBindingMain.nextSlide.setVisibility(View.GONE);

            }
        }
        else
        {
            if (grandMediaPermission && grandModifySystemSettings && grandUsageAccess && grandDisplayOverTheApp && grandBatteyOptimization) {
                Log.e("ok", "enable");
                onBaordingBindingMain.nextSlide.setVisibility(View.VISIBLE);
            } else {
                Log.e("ok", "not enable");
                onBaordingBindingMain.nextSlide.setVisibility(View.GONE);

            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkForOnePlus() {
        String name = Utils.getDeviceName();
        if (name.contains(getString(R.string.onePlus))) {
            permissionAskBinding.dontOptimizedBatteryHeader.setVisibility(View.VISIBLE);
            final PowerManager pm = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(getString(R.string.packageName))) {
                grandBatteyOptimization=false;
                permissionAskBinding.batteryUsages.setVisibility(View.INVISIBLE);
                permissionAskBinding.dontOptimizedBattery.setEnabled(true);
            } else {
                grandBatteyOptimization=true;
                permissionAskBinding.batteryUsages.setVisibility(View.VISIBLE);
                permissionAskBinding.dontOptimizedBattery.setEnabled(false);

            }
        } else {
            permissionAskBinding.dontOptimizedBatteryHeader.setVisibility(View.GONE);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void mediaPermission() {
        boolean b = PermissionManager.checkPermissionOnly(requireActivity(), Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);
        if (!b) {
            permissionAskBinding.grandMediaPermissionDone.setVisibility(View.INVISIBLE);
            permissionAskBinding.grandMediaPermission.setEnabled(true);
            grandMediaPermission=false;
        } else {
            permissionAskBinding.grandMediaPermissionDone.setVisibility(View.VISIBLE);
            permissionAskBinding.grandMediaPermission.setEnabled(false);
            grandMediaPermission=true;

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void modifySystemSettings() {
        if (!Settings.System.canWrite(requireContext())) {
             permissionAskBinding.modifySystemSettingsDone.setVisibility(View.INVISIBLE);
            permissionAskBinding.modifySystemSettings.setEnabled(true);
            grandModifySystemSettings=false;
        } else {
            permissionAskBinding.modifySystemSettingsDone.setVisibility(View.VISIBLE);
            permissionAskBinding.modifySystemSettings.setEnabled(false);
            grandModifySystemSettings=true;
        }
    }

    private void checkAccessUsage() {
        if (Utils.isAccessGranted(requireContext())) {
            grandUsageAccess=true;
            permissionAskBinding.usageAccessDone.setVisibility(View.VISIBLE);
            permissionAskBinding.usageAccess.setEnabled(false);

        } else {
            grandUsageAccess=false;

            permissionAskBinding.usageAccessDone.setVisibility(View.INVISIBLE);
            permissionAskBinding.usageAccess.setEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkDisplayOverTheApp() {
        if (Settings.canDrawOverlays(requireContext())) {
            permissionAskBinding.displayOverTheAppDone.setVisibility(View.VISIBLE);
            permissionAskBinding.displayOverTheApp.setEnabled(false);
            grandDisplayOverTheApp=true;
        } else {
            permissionAskBinding.displayOverTheAppDone.setVisibility(View.INVISIBLE);
            permissionAskBinding.displayOverTheApp.setEnabled(true);
            grandDisplayOverTheApp=false;

        }
    }

    public static PermissionAsk getInstance(ActivityOnBaordingBinding onBaordingBinding) {
        onBaordingBindingMain = onBaordingBinding;
        return new PermissionAsk();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.grandMediaPermission: {
                boolean b = PermissionManager.checkPermission(requireActivity(), Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);

                break;
            }
            case R.id.displayOverTheApp: {
                askForPopUpPermission();
                break;
            }
            case R.id.usageAccess: {
                callUsageAccessSettings();
                break;
            }
            case R.id.modifySystemSettings: {
                modifySystemSettingsAsk();
                break;
            }
            case R.id.dontOptimizedBattery: {
                batteryUsage();
                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void batteryUsage() {
        final PowerManager pm = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(getString(R.string.packageName))) {
            Utils.showAlertDialog(requireContext(), Constraint.BATTRY_OPTIMIZATION, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String packageName = requireContext().getPackageName();
                        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                            requireActivity().startActivityForResult(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS), Constraint.BATTRY_OPTIMIZATION_CODE);
                        }
                    }
                }
            }, false);

        }
    }

    private void callUsageAccessSettings() {
        Utils.showAlertDialog(requireContext(), Constraint.ACTION_USAGE_ACCESS_SETTINGS, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                requireActivity().startActivityForResult(intent, Constraint.RETURN);
            }
        }, false);


    }

    private void modifySystemSettingsAsk() {
        Utils.showAlertDialog(requireContext(), Constraint.MODIFY_SYSTEM_SEETINGS, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.youDesirePermissionCode(requireActivity());
            }
        }, false);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean askForPopUpPermission() {

        if (!Settings.canDrawOverlays(requireContext())) {
            Utils.showAlertDialog(requireContext(), Constraint.ASK_FOR_POPUP_PERMISSION, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + requireContext().getPackageName()));
                    requireActivity().startActivityForResult(intent, Constraint.POP_UP_RESPONSE);
                }
            }, false);

            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void permissionDone(PermissionDone permissionDone) {

        permissionSetter();
    }

    @Override
    public void onStart() {

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
