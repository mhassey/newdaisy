package com.daisy.activity.configSettings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.BuildConfig;
import com.daisy.R;
import com.daisy.activity.DeveloperActivity;
import com.daisy.activity.apkUpdate.ApkUpdateViewModel;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.baseUrl.BaseUrlSettings;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.activity.updateBaseUrl.UpdateBaseUrl;
import com.daisy.activity.updatePosition.UpdatePosition;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityConfigSettingsBinding;
import com.daisy.pojo.response.ApkDetails;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.utils.Constraint;
import com.daisy.utils.LogoutDialog;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Purpose -  ConfigSettings is an activity that contains all setting level works
 * Responsibility - Its show app version and last updated time
 * link for go to logs
 * link for go to UpdatePosition
 * link for go to SetRefreshTimer
 * Enable and disable sanitised feature
 * link for go to Change language
 * Handle apk update
 * Handle security feature
 * Handle alarm feature
 * Handle Update product
 **/
public class ConfigSettings extends BaseActivity implements View.OnClickListener {

    private ActivityConfigSettingsBinding mBinding;
    private Context context;
    private SessionManager sessionManager;
    private ApkUpdateViewModel viewModel;
    private GetCardViewModel getCardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_config_settings);
        initView();
        initClick();

    }

    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        context = this;
        sessionManager = SessionManager.get();
        viewModel = new ViewModelProvider(this).get(ApkUpdateViewModel.class);
        getCardViewModel = new ViewModelProvider(this).get(GetCardViewModel.class);

        setNoTitleBar(this);
//        sessionWork();
        mBinding.appVersion.setText(" " + BuildConfig.VERSION_NAME);
        getDefaultUpdateTime();
    }

    /**
     * Responsibility - getDefaultUpdateTime method is used for print last apk update time
     * Parameters - No parameter
     **/
    private void getDefaultUpdateTime() {
        try {
            String val = Utils.getLastUpdateDate(ConfigSettings.this);
            mBinding.updatetime.setText(" " + val);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {


        mBinding.developerOption.setOnClickListener(this);
        mBinding.directUpdate.setOnClickListener(this);
//        mBinding.updateBaseUrl.setOnClickListener(this::onClick);
        mBinding.updatePosition.setOnClickListener(this::onClick);
//        mBinding.logout.setOnClickListener(this::onClick);
//        mBinding.cancel.setOnClickListener(this::onClick);
//        mBinding.lunchApp.setOnClickListener(this::onClick);

        mBinding.updateProduct.setOnClickListener(this::onClick);

//        mBinding.logoutApp.setOnClickListener(this::onClick);
        mBinding.socketConnection.setOnClickListener(this::onClick);


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
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ViewGroup.LayoutParams params = mBinding.rootLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mBinding.rootLayout.requestLayout();

    }


    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.developer_option: {
                openDeveloperActivity();
                break;
            }
            case R.id.directUpdate: {
                directUpdate();
                break;
            }

//            case R.id.updateBaseUrl: {
//                updateBaseUrl();
//                break;
//            }
            case R.id.updatePosition: {
                openUpdatePositionActivity();
                break;
            }

            case R.id.cancel: {
                onBackPressed();
                break;
            }
//            case R.id.logout: {
//                logout();
//                break;
//            }

//            case R.id.lunchApp: {
//                launchApp();
//                break;
//            }
            case R.id.update_product: {
                openUpdateProductActivity();
                break;
            }

            case R.id.direct_apk_update: {
                handleApkUpdateDirectly();
                break;
            }
            case R.id.logout_app: {
                logoutAlert();
                break;
            }
        }
    }


    private void openDeveloperActivity() {
        Intent intent = new Intent(this, DeveloperActivity.class);
        startActivity(intent);

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

                    Intent i = new Intent(ConfigSettings.this, MainActivity.class);
                    if (response.getResult().getPricing() != null && !response.getResult().getPricing().isEmpty()) {
                        sessionManager.setPricing(response.getResult().getPricing());
                    }
                    i.putExtra(Constraint.PROMOTION, Constraint.TRUE_STR);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else if (response.getResult().getPricing() != null && !response.getResult().getPricing().isEmpty()) {
                    sessionManager.setPricing(response.getResult().getPricing());
                    Intent i = new Intent(ConfigSettings.this, MainActivity.class);
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
                    Intent i = new Intent(ConfigSettings.this, MainActivity.class);
                    i.putExtra(Constraint.PROMOTION, Constraint.TRUE_STR);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else if (response.getResult().getPricing() != null && !response.getResult().getPricing().isEmpty()) {
                    sessionManager.setPricing(response.getResult().getPricing());
                    Intent i = new Intent(ConfigSettings.this, MainActivity.class);
                    i.putExtra(Constraint.PRICING, Constraint.TRUE_STR);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                } else {
                    sessionManager.setPricing(null);
                    Intent i = new Intent(ConfigSettings.this, MainActivity.class);
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
                File directory = new File(getExternalFilesDir(""), configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                    Utils.deleteCardFolder();
                    try {
                        Utils.writeFile(configFilePath, UrlPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sessionManager.deleteLocation();

                } else {
                    try {
                        Utils.writeFile(configFilePath, UrlPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                sessionManager.onBoarding(Constraint.TRUE);

                Intent i = new Intent(ConfigSettings.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        } else {

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

                Intent i = new Intent(ConfigSettings.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }
    }


    /**
     * Purpose - logoutAlert method is used for open logout alert dialog
     */
    public void logoutAlert() {
        LogoutDialog dateTimePermissionDIalog = new LogoutDialog();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dateTimePermissionDIalog.show(ft, null);


    }


    /**
     * Responsibility - openUpdateProductActivity is an method to redirect page to  UpdateProduct
     * Parameters - No parameter
     **/
    private void openUpdateProductActivity() {
        openUpdateProductAlert();
//        Intent intent = new Intent(context, UpdateProduct.class);
//        startActivity(intent);
    }

    private void openUpdateProductAlert() {
        LayoutInflater inflater = getLayoutInflater();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View alertLayout = inflater.inflate(R.layout.update_product_alert_layout, null);

        alert.setView(alertLayout);
        Spinner manufactureList = alertLayout.findViewById(R.id.manufactureList);
        Spinner productName = alertLayout.findViewById(R.id.productName);
        Spinner webkitOrientation = alertLayout.findViewById(R.id.webkitOrientation);
        ImageView close = alertLayout.findViewById(R.id.close_dialog);


        alert.setCancelable(false);
        AlertDialog dialog = alert.create();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.getWindow().setLayout((width - (width / 4)), ((height / 2) - (height / 10))); //Controlling width and height.
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



    }


    /**
     * Responsibility - handleApkUpdateDirectly is an method that check any apk update is available or not
     * Parameters - No parameter
     **/
    private void handleApkUpdateDirectly() {
        showHideProgressDialog(true);
        viewModel.setRequest(new HashMap());
        LiveData<GlobalResponse<GeneralResponse>> liveData = viewModel.getResponseLiveData();
        liveData.observe(this, new Observer<GlobalResponse<GeneralResponse>>() {
            @Override
            public void onChanged(GlobalResponse<GeneralResponse> response) {
                showHideProgressDialog(false);
                handleAPkUpdateResponse(response);
            }
        });
    }

    /**
     * Responsibility - handleAPkUpdateResponse is an method that takes response from handleApkUpdateDirectly and if any new version comes then change session value and redirect  to main
     * Parameters - Its take GlobalResponse<GeneralResponse> response
     **/
    private void handleAPkUpdateResponse(GlobalResponse<GeneralResponse> response) {
        if (response != null) {

            GlobalResponse<GeneralResponse> globalResponse = response;
            if (globalResponse.isApi_status()) {
                ApkDetails apkDetails = globalResponse.getResult().getApkDetails();
                if (apkDetails != null) {
                    if (apkDetails.getAndroid().getVersion() != null) {
                        if (sessionManager == null)
                            sessionManager = SessionManager.get();
                        double apkVersion = Double.parseDouble(apkDetails.getAndroid().getVersion());
                        double ourVersion = Double.parseDouble(BuildConfig.VERSION_NAME);
                        if (apkVersion > ourVersion) {
                            sessionManager.setApkVersion(BuildConfig.VERSION_NAME);
                            sessionManager.setVersionDetails(apkDetails);
                            openMainActivity();
                        } else {
                            ValidationHelper.showToast(context, getString(R.string.no_update_available));
                        }
                    }

                }


            }
        }
    }


    /**
     * Responsibility - launchApp method is used for launch other application its just an demo
     * Parameters - No parameter
     **/
    private void launchApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            ValidationHelper.showToast(ConfigSettings.this, "There is no package available in android");
        }
    }


    /**
     * Responsibility - logout method is used for logout the app but not in used
     * Parameters - No parameter
     **/
    private void logout() {
        sessionManager.removeSession();
        Intent intent = new Intent(ConfigSettings.this, BaseUrlSettings.class);
        ProcessPhoenix.triggerRebirth(ConfigSettings.this, intent);
    }


    /**
     * Responsibility - updateBaseUrl method is used for open UpdateBaseUrl activity
     * Parameters - No parameter
     **/
    private void updateBaseUrl() {
        Intent intent = new Intent(ConfigSettings.this, UpdateBaseUrl.class);
        startActivity(intent);

    }


    /**
     * Responsibility - openUpdatePositionActivity method is used for open UpdatePosition activity
     * Parameters - No parameter
     **/
    private void openUpdatePositionActivity() {
        Intent intent = new Intent(ConfigSettings.this, UpdatePosition.class);
        startActivity(intent);
    }


    /**
     * Responsibility - openMainActivity method is used for open MainActivity activity
     * Parameters - No parameter
     **/
    private void openMainActivity() {
        Intent intent = new Intent(ConfigSettings.this, MainActivity.class);
        startActivity(intent);
    }

}
