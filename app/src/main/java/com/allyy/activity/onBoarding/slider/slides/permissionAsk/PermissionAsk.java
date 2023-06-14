package com.allyy.activity.onBoarding.slider.slides.permissionAsk;

import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.allyy.R;
import com.allyy.databinding.ActivityOnBaordingBinding;
import com.allyy.databinding.FragmentPermissionAskBinding;
import com.allyy.pojo.response.PermissionDone;
import com.allyy.security.Admin;
import com.allyy.utils.Constraint;
import com.allyy.utils.PermissionManager;
import com.allyy.utils.Utils;
import com.google.android.gms.common.api.GoogleApiClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

/**
 * Purpose -  PermissionAsk is an fragment that ask for all permission from user
 * Responsibility - Its ask for predefine permission and customized permission that our app is used
 **/
public class PermissionAsk extends Fragment implements View.OnClickListener {
    private static ActivityOnBaordingBinding onBaordingBindingMain;
    private FragmentPermissionAskBinding permissionAskBinding;
    private PermissionAskViewModel permissionAskViewModel;
    private Context context;
    final int sdk = android.os.Build.VERSION.SDK_INT;
    public static GoogleApiClient googleApiClient;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;

    private static final int REQUEST_ENABLE = 123;

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




    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        context = requireContext();
        permissionAskViewModel = new ViewModelProvider(this).get(PermissionAskViewModel.class);
    }


    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        permissionAskBinding.grandMediaPermission.setOnClickListener(this);
        permissionAskBinding.modifySystemSettings.setOnClickListener(this);
        permissionAskBinding.usageAccess.setOnClickListener(this);
        permissionAskBinding.displayOverTheApp.setOnClickListener(this);
        permissionAskBinding.cancel.setOnClickListener(this::onClick);
    }


    /**
     * Responsibility - check for kind of permission and maintain its visibility and clicking
     * Parameters - No parameter
     **/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionSetter() {
        checkDisplayOverTheApp();
        checkAccessUsage();
        modifySystemSettings();
        mediaPermission();

        checkForGps();
        checkAdminPermission();
        String name = Utils.getDeviceName();

        if (false) {
            if (permissionAskViewModel.isGrandMediaPermission() && permissionAskViewModel.isGrandGpsEnable() && permissionAskViewModel.isGrandAdminPermission() && permissionAskViewModel.isGrandModifySystemSettings() && permissionAskViewModel.isGrandUsageAccess() && permissionAskViewModel.isGrandDisplayOverTheApp() && permissionAskViewModel.isGrandExtraAccess()) {
                onBaordingBindingMain.nextSlide.setVisibility(View.VISIBLE);
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    if (Locale.getDefault().getLanguage().equals(Constraint.AR))
                        onBaordingBindingMain.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_light_red_rtl));
                    else
                        onBaordingBindingMain.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_light_red));
                } else {
                    if (Locale.getDefault().getLanguage().equals(Constraint.AR))
                        onBaordingBindingMain.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_light_red_rtl));
                    else
                        onBaordingBindingMain.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_light_red));
                }
            } else {
                onBaordingBindingMain.nextSlide.setVisibility(View.GONE);

            }
        } else {
            if (permissionAskViewModel.isGrandMediaPermission() && permissionAskViewModel.isGrandModifySystemSettings() && permissionAskViewModel.isGrandUsageAccess() && permissionAskViewModel.isGrandDisplayOverTheApp()) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {

                    if (Locale.getDefault().getLanguage().equals(Constraint.AR))
                        onBaordingBindingMain.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_light_red_rtl));
                    else
                        onBaordingBindingMain.nextSlide.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ovel_light_red));
                } else {
                    if (Locale.getDefault().getLanguage().equals(Constraint.AR))
                        onBaordingBindingMain.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_light_red_rtl));
                    else
                        onBaordingBindingMain.nextSlide.setBackground(ContextCompat.getDrawable(context, R.drawable.ovel_light_red));
                }
                onBaordingBindingMain.nextSlide.setVisibility(View.VISIBLE);
            } else {
                onBaordingBindingMain.nextSlide.setVisibility(View.GONE);

            }

        }

    }

    /**
     * Responsibility - Checks for gps
     * Parameters - No parameter
     **/
    private void checkForGps() {
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        permissionAskViewModel.setGrandGpsEnable(Constraint.TRUE);

    }


    /**
     * Responsibility - mainAdminAsk is an method that check is admin is activated then disable admin asking permission button else enable admin asking permission button
     * Parameters - No parameter
     **/
    private void checkAdminPermission() {
        mDPM = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(getActivity(), Admin.class);
        if (!mDPM.isAdminActive(mAdminName)) {
            //      if (false){
            permissionAskViewModel.setGrandAdminPermission(false);
            } else {

            permissionAskViewModel.setGrandAdminPermission(true);

        }
    }






    /**
     * Responsibility - check for media permission
     * Parameters - No parameter
     **/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void mediaPermission() {
        boolean b;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Do something for lollipop and above versions
            b = PermissionManager.checkPermissionOnly(requireActivity(), Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);
        } else {
            b = PermissionManager.checkPermissionOnly(requireActivity(), Constraint.STORAGE_PERMISSION_WITHOUT_SENSOR, Constraint.RESPONSE_CODE);
            // do something for phones running an SDK before lollipop
        }
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

    /**
     * Responsibility - check for modify system settings  permission
     * Parameters - No parameter
     **/
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

    /**
     * Responsibility - check for access usage  permission
     * Parameters - No parameter
     **/
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

    /**
     * Responsibility - check for display over the app permission
     * Parameters - No parameter
     **/
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


    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.grandMediaPermission: {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Do something for lollipop and above versions
                    PermissionManager.checkPermission(requireActivity(), Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);
                } else {
                    PermissionManager.checkPermission(requireActivity(), Constraint.STORAGE_PERMISSION_WITHOUT_SENSOR, Constraint.RESPONSE_CODE);
                    // do something for phones running an SDK before lollipop
                }

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

            case R.id.cancel: {
                getActivity().onBackPressed();
                break;
            }

        }
    }

    /**
     * Responsibility - enable gps method check if gps is enable if not then buildAlertMessageNoGps method
     * Parameters - No parameter
     **/
    private void enableGps() {
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    /**
     * Responsibility - buildAlertMessageNoGps method is used for open alert for asking enable gps
     * Parameters - No parameter
     **/
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.enable_gps)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        getActivity().startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constraint.GPS_ENABLE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Responsibility - batteryUsage method is used for asking battery optimization permission
     * Parameters - No parameter
     **/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void batteryUsage() {
        final PowerManager pm = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(getString(R.string.packageName))) {
            Utils.showAlertDialog(requireContext(), getString(R.string.battery_optimized), "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Intent intent = new Intent();
                            String packageName = context.getPackageName();
                            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                intent.setData(Uri.parse("package:" + packageName));
                                requireActivity().startActivityForResult(intent, Constraint.BATTRY_OPTIMIZATION_CODE);
                            }
                        }
                    }
                }
            }, false);

        }
    }

    /**
     * Responsibility - Open Usage data access alert dialog
     * Parameters - No parameter
     **/
    private void callUsageAccessSettings() {
        Utils.showAlertDialog(requireContext(), getString(R.string.allow_data_access), "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                requireActivity().startActivityForResult(intent, Constraint.RETURN);
            }
        }, false);


    }

    /**
     * Responsibility - Open modify System Settings alert dialog
     * Parameters - No parameter
     **/
    private void modifySystemSettingsAsk() {
        Utils.showAlertDialog(requireContext(), getString(R.string.modify_system_settings_text), "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.youDesirePermissionCode(requireActivity());
            }
        }, false);


    }

    /**
     * Responsibility - Open display over the app alert dialog
     * Parameters - No parameter
     **/
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
        if (permissionDone.getPermissionName().equals(Constraint.REDME)) {
            permissionAskViewModel.setGrandExtraAccess(Constraint.TRUE);

        }

        permissionSetter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void adminSetter(Admin admin) {
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

    }


    public static class DeviceAdminSampleReceiver extends DeviceAdminReceiver {
        void showToast(Context context, String msg) {
            String status = context.getString(R.string.admin_receiver_status, msg);
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED) {
                abortBroadcast();
            }
            super.onReceive(context, intent);
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_enabled));
        }

        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            return context.getString(R.string.admin_receiver_status_disable_warning);
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_disabled));
        }

        @Override
        public void onPasswordChanged(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_changed));
        }

        @Override
        public void onPasswordFailed(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_failed));
        }

        @Override
        public void onPasswordSucceeded(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_succeeded));
        }

        @Override
        public void onPasswordExpiring(Context context, Intent intent) {
            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(
                    Context.DEVICE_POLICY_SERVICE);
            long expr = dpm.getPasswordExpiration(
                    new ComponentName(context, DeviceAdminSampleReceiver.class));
            long delta = expr - System.currentTimeMillis();
            boolean expired = delta < 0L;
            String message = context.getString(expired ?
                    R.string.expiration_status_past : R.string.expiration_status_future);
            showToast(context, message);

        }
    }


}
