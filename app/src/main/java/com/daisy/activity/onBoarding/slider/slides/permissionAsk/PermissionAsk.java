package com.daisy.activity.onBoarding.slider.slides.permissionAsk;

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
import com.daisy.database.DBCaller;
import com.daisy.utils.Constraint;
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
    private boolean grandExtraAccess = false;
    private Context context;

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
        context=requireContext();
        permissionAskBinding.grandMediaPermission.setOnClickListener(this);
        permissionAskBinding.modifySystemSettings.setOnClickListener(this);
        permissionAskBinding.usageAccess.setOnClickListener(this);
        permissionAskBinding.displayOverTheApp.setOnClickListener(this);
        permissionAskBinding.dontOptimizedBattery.setOnClickListener(this);
        permissionAskBinding.miExtra.setOnClickListener(this::onClick);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionSetter() {
        checkDisplayOverTheApp();
        checkAccessUsage();
        modifySystemSettings();
        mediaPermission();
        checkForOnePlus();
        checkForMi();
        String name = Utils.getDeviceName();
        if (name.contains(getString(R.string.onePlus))) {

            if (grandMediaPermission && grandModifySystemSettings && grandUsageAccess && grandDisplayOverTheApp && grandBatteyOptimization) {
                onBaordingBindingMain.nextSlide.setVisibility(View.VISIBLE);
            } else {
                onBaordingBindingMain.nextSlide.setVisibility(View.GONE);

            }
        }
        else  if (name.contains(Constraint.REDME))
        {
            if (grandMediaPermission && grandModifySystemSettings && grandUsageAccess && grandDisplayOverTheApp && grandExtraAccess) {
                onBaordingBindingMain.nextSlide.setVisibility(View.VISIBLE);
            } else {
                onBaordingBindingMain.nextSlide.setVisibility(View.GONE);

            }
        }
        else
        {
            if (grandMediaPermission && grandModifySystemSettings && grandUsageAccess && grandDisplayOverTheApp) {
                onBaordingBindingMain.nextSlide.setVisibility(View.VISIBLE);
            } else {
                onBaordingBindingMain.nextSlide.setVisibility(View.GONE);

            }

        }

    }

    private void checkForMi() {
    if (Utils.getDeviceName().contains(Constraint.REDME))
    {
        permissionAskBinding.miExtraHeader.setVisibility(View.VISIBLE);
    }
    else
    {
        permissionAskBinding.miExtraHeader.setVisibility(View.INVISIBLE);
    }
    if (grandExtraAccess)
    {

        permissionAskBinding.miExtraRight.setVisibility(View.VISIBLE);
        permissionAskBinding.miExtra.setEnabled(false);

    }
    else
    {
        permissionAskBinding.miExtraRight.setVisibility(View.INVISIBLE);
        permissionAskBinding.miExtra.setEnabled(true);


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
            case R.id.miExtra:
            {
                callMiExtraPopUp();
                break;
            }
        }
    }

    private void callMiExtraPopUp() {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", getActivity().getPackageName());
        requireActivity().startActivityForResult(intent,Constraint.MI_EXTRA_PERMISSION_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void batteryUsage() {
        final PowerManager pm = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(getString(R.string.packageName))) {
            Utils.showAlertDialog(requireContext(), getString(R.string.battery_optimized), "Ok", new DialogInterface.OnClickListener() {
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
        Utils.showAlertDialog(requireContext(), getString(R.string.allow_data_access), "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                requireActivity().startActivityForResult(intent, Constraint.RETURN);
            }
        }, false);


    }

    private void modifySystemSettingsAsk() {
        Utils.showAlertDialog(requireContext(), getString(R.string.modify_system_settings_text), "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.youDesirePermissionCode(requireActivity());
            }
        }, false);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean askForPopUpPermission() {

        if (!Settings.canDrawOverlays(requireContext())) {
            Utils.showAlertDialog(requireContext(), getString(R.string.display_over_the_app), "Ok", new DialogInterface.OnClickListener() {
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
        if (permissionDone.getPermissionName().equals(Constraint.REDME))
        {
            grandExtraAccess=true;
        }
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
