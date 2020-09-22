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
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
    private PermissionAskViewModel permissionAskViewModel;
    private Context context;
    final int sdk = android.os.Build.VERSION.SDK_INT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        permissionAskBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_permission_ask, container, false);
        return permissionAskBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        permissionSetter();
        initClick();
    }

    private void initView() {
        context=requireContext();
        permissionAskViewModel=new ViewModelProvider(this).get(PermissionAskViewModel.class);
    }

    private void initClick() {
        permissionAskBinding.grandMediaPermission.setOnClickListener(this);
        permissionAskBinding.modifySystemSettings.setOnClickListener(this);
        permissionAskBinding.usageAccess.setOnClickListener(this);
        permissionAskBinding.displayOverTheApp.setOnClickListener(this);
        permissionAskBinding.dontOptimizedBattery.setOnClickListener(this);
        permissionAskBinding.miExtra.setOnClickListener(this::onClick);
        permissionAskBinding.cancel.setOnClickListener(this::onClick);
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

          if (name.contains(Constraint.REDME))
        {
            if (permissionAskViewModel.isGrandMediaPermission() && permissionAskViewModel.isGrandModifySystemSettings() && permissionAskViewModel.isGrandUsageAccess() && permissionAskViewModel.isGrandDisplayOverTheApp() && permissionAskViewModel.isGrandExtraAccess()) {
                onBaordingBindingMain.nextSlide.setVisibility(View.VISIBLE);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    onBaordingBindingMain.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_light_red) );
                } else {
                    onBaordingBindingMain.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_light_red));
                }
            } else {
                onBaordingBindingMain.nextSlide.setVisibility(View.GONE);

            }
        }
        else
        {
            if (permissionAskViewModel.isGrandMediaPermission() && permissionAskViewModel.isGrandModifySystemSettings() && permissionAskViewModel.isGrandUsageAccess() && permissionAskViewModel.isGrandDisplayOverTheApp() && permissionAskViewModel.isGrandBatteyOptimization()) {
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    onBaordingBindingMain.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_light_red) );
                } else {
                    onBaordingBindingMain.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_light_red));
                }
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
    if (permissionAskViewModel.isGrandExtraAccess())
    {

        permissionAskBinding.miExtraRight.setChecked(true);
        permissionAskBinding.miExtra.setEnabled(false);

    }
    else
    {
        permissionAskBinding.miExtraRight.setChecked(false);
        permissionAskBinding.miExtra.setEnabled(true);


    }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkForOnePlus() {
             final PowerManager pm = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(getString(R.string.packageName))) {
                permissionAskViewModel.setGrandBatteyOptimization(Constraint.FALSE);
                permissionAskBinding.batteryUsages.setChecked(false);
                permissionAskBinding.dontOptimizedBattery.setEnabled(Constraint.TRUE);
            } else {
                permissionAskViewModel.setGrandBatteyOptimization(Constraint.TRUE);
                permissionAskBinding.batteryUsages.setChecked(true);
                permissionAskBinding.dontOptimizedBattery.setEnabled(Constraint.FALSE);

            }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void mediaPermission() {
        boolean b = PermissionManager.checkPermissionOnly(requireActivity(), Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);
        if (!b) {
            permissionAskBinding.grandMediaPermissionDone.setChecked(false);
            permissionAskBinding.grandMediaPermission.setEnabled(Constraint.TRUE);
            permissionAskViewModel.setGrandMediaPermission(Constraint.FALSE);
        } else {
            permissionAskBinding.grandMediaPermissionDone.setChecked(true);
            permissionAskBinding.grandMediaPermission.setEnabled(Constraint.FALSE);
            permissionAskViewModel.setGrandMediaPermission(Constraint.TRUE);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void modifySystemSettings() {
        if (!Settings.System.canWrite(requireContext())) {
             permissionAskBinding.modifySystemSettingsDone.setChecked(false);
            permissionAskBinding.modifySystemSettings.setEnabled(Constraint.TRUE);
            permissionAskViewModel.setGrandModifySystemSettings(Constraint.FALSE);
        } else {
            permissionAskBinding.modifySystemSettingsDone.setChecked(true);
            permissionAskBinding.modifySystemSettings.setEnabled(Constraint.FALSE);
            permissionAskViewModel.setGrandModifySystemSettings(Constraint.TRUE);
        }
    }

    private void checkAccessUsage() {
        if (Utils.isAccessGranted(requireContext())) {
            permissionAskViewModel.setGrandUsageAccess(Constraint.TRUE);
            permissionAskBinding.usageAccessDone.setChecked(true);
            permissionAskBinding.usageAccess.setEnabled(Constraint.FALSE);

        } else {
            permissionAskViewModel.setGrandUsageAccess(Constraint.FALSE);
            permissionAskBinding.usageAccessDone.setChecked(false);
            permissionAskBinding.usageAccess.setEnabled(Constraint.TRUE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkDisplayOverTheApp() {
        if (Settings.canDrawOverlays(requireContext())) {
            permissionAskBinding.displayOverTheAppDone.setChecked(true);
            permissionAskBinding.displayOverTheApp.setEnabled(Constraint.FALSE);
            permissionAskViewModel.setGrandDisplayOverTheApp(Constraint.TRUE);
        } else {
            permissionAskBinding.displayOverTheAppDone.setChecked(false);
            permissionAskBinding.displayOverTheApp.setEnabled(Constraint.TRUE);
            permissionAskViewModel.setGrandDisplayOverTheApp(Constraint.FALSE);

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
            case R.id.cancel:
            {
                getActivity().onBackPressed();
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
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Intent intent = new Intent();
                            String packageName = context.getPackageName();
                            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                intent.setData(Uri.parse("package:" + packageName));
                                requireActivity().startActivityForResult(intent,Constraint.BATTRY_OPTIMIZATION_CODE);
                            }
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
            permissionAskViewModel.setGrandExtraAccess(Constraint.TRUE);

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

    @Override
    public void onResume() {
        super.onResume();
        designWork();
           }

    private void designWork() {
        onBaordingBindingMain.tabDotsLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.selected_dot_red));
        onBaordingBindingMain.tabDotsLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.default_dot));
        onBaordingBindingMain.tabDotsLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.default_dot));
        onBaordingBindingMain.tabDotsLayout.getTabAt(3).setIcon(getResources().getDrawable(R.drawable.default_dot));

    }

}
