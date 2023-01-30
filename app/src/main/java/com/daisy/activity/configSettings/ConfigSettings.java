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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

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
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.daisy.activity.updatePosition.UpdatePosition;
import com.daisy.activity.updateProduct.UpdateProductViewModel;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.ActivityConfigSettingsBinding;
import com.daisy.databinding.BrighnessChangeLayoutBinding;
import com.daisy.databinding.UpdateProductAlertLayoutBinding;
import com.daisy.pojo.response.ApkDetails;
import com.daisy.pojo.response.Carrier;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Manufacture;
import com.daisy.pojo.response.Product;
import com.daisy.utils.Constraint;
import com.daisy.utils.LogoutDialog;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class ConfigSettings extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ActivityConfigSettingsBinding mBinding;
    private Context context;
    private SessionManager sessionManager;
    private ApkUpdateViewModel viewModel;
    private GetCardViewModel getCardViewModel;
    public AddScreenViewModel mViewModel;
    private UpdateProductViewModel updateProductViewModel;
    private UpdateProductAlertLayoutBinding alertBinding;
    private BrighnessChangeLayoutBinding brightnessBinding;
    private AlertDialog dialog;
    private AlertDialog brightnessDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_config_settings);
        alertBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.update_product_alert_layout, null, false);
        brightnessBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.brighness_change_layout, null, false);
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
        mViewModel = new ViewModelProvider(this).get(AddScreenViewModel.class);
        updateProductViewModel = new ViewModelProvider(this).get(UpdateProductViewModel.class);
        setNoTitleBar(this);
        mBinding.appVersion.setText(" " + BuildConfig.VERSION_NAME);
        initGlobalSettings();
        getDefaultUpdateTime();
        addOrientationData();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertBinding.getRoot());
        alert.setCancelable(false);
        dialog = alert.create();
        AlertDialog.Builder brightness = new AlertDialog.Builder(this);
        brightness.setView(brightnessBinding.getRoot());
        brightness.setCancelable(false);
        brightnessDialog = brightness.create();

    }

    private void initGlobalSettings() {
        globalSettingsBoldFont(mBinding.settingLabel);
        globalSettingsBoldFont(mBinding.updateProduct);
        globalSettingsBoldFont(mBinding.updatePosition);
        globalSettingsBoldFont(mBinding.directUpdate);
        globalSettingsBoldFont(mBinding.developerOption);
        globalSettingsBoldFont(mBinding.changeBrightness);
        globalSettingsBoldFont(mBinding.logoutApp);
        globalSettingsRegularFont(mBinding.versionLabel);
        globalSettingsRegularFont(mBinding.appVersion);

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
        alertBinding.updateMpcProduct.setOnClickListener(this);
        mBinding.directUpdate.setOnClickListener(this);
        mBinding.updatePosition.setOnClickListener(this);
        mBinding.logoutApp.setOnClickListener(this);
        mBinding.updateProduct.setOnClickListener(this);
        mBinding.acClose.setOnClickListener(this);
        mBinding.socketConnection.setOnClickListener(this);
        mBinding.changeBrightness.setOnClickListener(this);
        alertBinding.productName.setOnItemSelectedListener(getProductNameListener());
        alertBinding.carrierName.setOnItemSelectedListener(getCarrierListener());
        alertBinding.manufactureList.setOnItemSelectedListener(getManufactureListener());
        brightnessBinding.progressBar.setTag(Constraint.DEFAULT);
        brightnessBinding.progressBar.setOnSeekBarChangeListener(this);
        brightnessBinding.maxProgressBar.setTag(Constraint.MAX);
        brightnessBinding.maxProgressBar.setOnSeekBarChangeListener(this);

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

            case R.id.updatePosition: {
                openUpdatePositionActivity();
                break;
            }

            case R.id.cancel:
            case R.id.ac_close: {
                onBackPressed();
                break;
            }

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
            case R.id.update_mpc_product: {
                handlePriceCardGettingHandler();
                break;
            }
            case R.id.change_brightness: {
                changeBrightness();
                break;
            }
        }
    }

    private void changeBrightness() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        brightnessDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            if (!SessionManager.get().isBrighnessDefault()) {
                brightnessBinding.defaultBrightnessLevel.setText(Constraint.DEFAULT_BRIGHTNESS_LEVEL);
                brightnessBinding.maxBrightnessLevel.setText(Constraint.MAX_BRIGHTNESS_LEVEL);
                brightnessBinding.maxProgressBar.setProgress(Constraint.MAX_BRIGHTNESS_INTEGER);

                brightnessBinding.progressBar.setProgress(Constraint.DEFAULT_BRIGHTNESS_INTEGER);
            } else {
                brightnessBinding.defaultBrightnessLevel.setText((SessionManager.get().getDefaultBrightness() * 10) + Constraint.PERCENTAGE);
                brightnessBinding.maxBrightnessLevel.setText((SessionManager.get().getMaxBrightness() * 10) + Constraint.PERCENTAGE);
                brightnessBinding.maxProgressBar.setProgress(SessionManager.get().getMaxBrightness());
                brightnessBinding.progressBar.setProgress(SessionManager.get().getDefaultBrightness());

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        brightnessDialog.show();
        brightnessDialog.getWindow().setLayout((width - (width / 4)), ((height / 2) - (height / 10))); //Controlling width and height.
        brightnessBinding.reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.get().isBrightnessIsDefault(false);
                brightnessBinding.maxProgressBar.setProgress(Constraint.MAX_BRIGHTNESS_INTEGER);

                brightnessBinding.progressBar.setProgress(Constraint.DEFAULT_BRIGHTNESS_INTEGER);
            }
        });
        brightnessBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brightnessDialog.dismiss();
            }
        });


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
        getAllData();
    }

    private void openUpdateProductAlert() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.getWindow().setLayout((width - (width / 4)), ((height / 2) - (height / 10))); //Controlling width and height.
        alertBinding.closeDialog.setOnClickListener(new View.OnClickListener() {
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


    /**
     * Responsibility - handlePriceCardGettingHandler method helps  to fire update screen api and send response in handleScreenAddResponse  method
     * Parameters - No parameter
     **/
    private void handlePriceCardGettingHandler() {
        if (Utils.getNetworkState(getApplicationContext())) {
            HashMap<String, String> request = getUpdateScreenRequest();
            if (request != null) {
                showHideProgressDialog(true);
                updateProductViewModel.setMutableLiveData(request);
                LiveData<GlobalResponse> liveData = updateProductViewModel.getLiveData();
                if (!liveData.hasActiveObservers()) {
                    liveData.observe(this, new Observer<GlobalResponse>() {
                        @Override
                        public void onChanged(GlobalResponse globalResponse) {
                            showHideProgressDialog(false);
                            handleScreenAddResponse(globalResponse);
                        }
                    });
                }
            }

        } else {
            ValidationHelper.showToast(getApplicationContext(), getString(R.string.no_internet_available));
        }

    }

    /**
     * Responsibility -  getUpdateScreenRequest method create update screen request
     * Parameters - No parameter
     **/
    private HashMap<String, String> getUpdateScreenRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        if (mViewModel.getSelectedProduct() != null && alertBinding.productName.getSelectedItem() != null) {
            if (mViewModel.getSelectedProduct().getIdproductStatic() != null)
                hashMap.put(Constraint.ID_PRODUCT_STATIC, mViewModel.getSelectedProduct().getIdproductStatic());
        } else {
            ValidationHelper.showToast(context, getString(R.string.product_not_available));
            return null;
        }
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }


    /**
     * Responsibility - handleScreenAddResponse is an method that check update screen response is ok if yes then call getCardData method
     * Parameters - Its takes GlobalResponse object as an parameter
     **/
    private void handleScreenAddResponse(GlobalResponse screenAddResponseGlobalResponse) {

        if (screenAddResponseGlobalResponse != null && screenAddResponseGlobalResponse.isApi_status()) {
            sessionManager.setOrientation(alertBinding.webkitOrientation.getSelectedItem().toString());
            getCardData();
        } else {
            ValidationHelper.showToast(context, screenAddResponseGlobalResponse.getMessage());
        }
    }


    /**
     * Responsibility - When we resume the activity then we need to get all data again so  we again set carrier and product
     * Parameters - No parameter
     **/
    private void getAllData() {
        sessionManager = SessionManager.get();
        if (sessionManager.getLoginResponse() != null) {
            List<Carrier> carriers = sessionManager.getLoginResponse().getCarrier();
            if (carriers != null) {
                mViewModel.setCarriers(carriers);
                ArrayAdapter<Carrier> carrierArrayAdapter = new ArrayAdapter<Carrier>(context, android.R.layout.simple_spinner_item, mViewModel.getCarriers());
                carrierArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                alertBinding.carrierName.setAdapter(carrierArrayAdapter);

            }
            List<Manufacture> manufactures = sessionManager.getLoginResponse().getManufacturers();
            if (manufactures != null) {
                mViewModel.setManufactures(manufactures);
                ArrayAdapter<Manufacture> manufactureArrayAdapter = new ArrayAdapter<Manufacture>(context, android.R.layout.simple_spinner_item, mViewModel.getManufactures());
                manufactureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                alertBinding.manufactureList.setAdapter(manufactureArrayAdapter);

            }

        }
        ArrayAdapter<String> orientationAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mViewModel.getOrientation());
        orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertBinding.webkitOrientation.setAdapter(orientationAdapter);
    }

    /**
     * Responsibility - getCardData method fire the getCard api and pass the response to handleCardGetResponse method
     * Parameters - No parameter
     **/
    private void getCardData() {
        if (Utils.getNetworkState(context)) {
            showHideProgressDialog(true);
            getCardViewModel.setMutableLiveData(getCardRequest());
            LiveData<GlobalResponse<GetCardResponse>> liveData = getCardViewModel.getLiveData();
            if (!liveData.hasActiveObservers()) {
                liveData.observe(this, new Observer<GlobalResponse<GetCardResponse>>() {
                    @Override
                    public void onChanged(GlobalResponse<GetCardResponse> getCardResponseGlobalResponse) {
                        try {
                            DBCaller.storeLogInDatabase(context, getCardResponseGlobalResponse.getResult().getPricecard().getPriceCardName() + Constraint.DATA_STORE, "", "", Constraint.APPLICATION_LOGS);
                            handleCardGetResponse(getCardResponseGlobalResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }
    }

    /**
     * Responsibility - handleCardGetResponse method handle response provided by getCardData method if response is ok then set new value of price card promotion and pricing in session and call redirectToMainHandler method
     * Parameters - No parameter
     **/
    private void handleCardGetResponse(GlobalResponse<GetCardResponse> getCardResponseGlobalResponse) throws IOException {
        showHideProgressDialog(false);
        if (getCardResponseGlobalResponse.isApi_status()) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            try {

                sessionManager.setPriceCard(getCardResponseGlobalResponse.getResult().getPricecard());
                sessionManager.setPromotion(getCardResponseGlobalResponse.getResult().getPromotions());
                sessionManager.setPricing(getCardResponseGlobalResponse.getResult().getPricing());
                redirectToMainHandler(getCardResponseGlobalResponse);
            } catch (Exception e) {

            }

        } else {
            if (getCardResponseGlobalResponse.getResult().getDefaultPriceCard() != null && !getCardResponseGlobalResponse.getResult().getDefaultPriceCard().equals("")) {
                redirectToMainHandler(getCardResponseGlobalResponse);
            } else
                ValidationHelper.showToast(context, getCardResponseGlobalResponse.getMessage());
        }
    }

    /**
     * Responsibility - redirectToMainHandler method  delete daisy older data and set new file path from which app download the price card  and  call redirectToMain method
     * Parameters - Its takes GlobalResponse response object
     **/
    private void redirectToMainHandler(GlobalResponse<GetCardResponse> response) throws IOException {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
            Utils.deleteDaisy();
            String UrlPath;

            if (response.getResult().getPricecard().getFileName1() != null && !response.getResult().getPricecard().getFileName1().equals("")) {
                UrlPath = response.getResult().getPricecard().getFileName1();
            } else {
                UrlPath = response.getResult().getPricecard().getFileName();
            }

            if (response.getResult().getPricecard().getFileName() != null) {
                String configFilePath = Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(getExternalFilesDir(""), configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                    if (!path.equals(UrlPath)) {
                        Utils.deleteCardFolder();
                        Utils.writeFile(configFilePath, UrlPath);
                        sessionManager.deleteLocation();

                        DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                    }
                } else {
                    Utils.writeFile(configFilePath, UrlPath);
                }

                redirectToMain();

            } else if (response.getResult().getDefaultPriceCard() != null && !response.getResult().getDefaultPriceCard().equals("")) {
                UrlPath = response.getResult().getDefaultPriceCard();
                String configFilePath = Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(getExternalFilesDir(""), configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                    if (!path.equals(UrlPath)) {
                        Utils.deleteCardFolder();
                        Utils.writeFile(configFilePath, UrlPath);
                        sessionManager.deleteLocation();

                        DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                    }
                } else {
                    Utils.deleteCardFolder();
                    sessionManager.deletePriceCard();
                    sessionManager.deletePromotions();
                    sessionManager.setPricing(null);
                    sessionManager.deleteLocation();

                    Utils.writeFile(configFilePath, UrlPath);

                }

                redirectToMain();
            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_url));
            }

            Intent intent = new Intent(this, EditorTool.class);
            startActivity(intent);
            finish();
        } else {
            Utils.deleteDaisy();
            String UrlPath;

            if (response.getResult().getPricecard().getFileName1() != null && !response.getResult().getPricecard().getFileName1().equals("")) {
                UrlPath = response.getResult().getPricecard().getFileName1();
            } else {
                UrlPath = response.getResult().getPricecard().getFileName();
            }

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
                        Utils.writeFile(configFilePath, UrlPath);
                        sessionManager.deleteLocation();

                        DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                    }
                } else {
                    Utils.writeFile(configFilePath, UrlPath);
                }

                redirectToMain();

            } else if (response.getResult().getDefaultPriceCard() != null && !response.getResult().getDefaultPriceCard().equals("")) {
                UrlPath = response.getResult().getDefaultPriceCard();
                String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(configFilePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String path = Utils.getPath();
                if (path != null) {
                    if (!path.equals(UrlPath)) {
                        Utils.deleteCardFolder();
                        Utils.writeFile(configFilePath, UrlPath);
                        sessionManager.deleteLocation();

                        DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                    }
                } else {
                    Utils.deleteCardFolder();
                    sessionManager.deletePriceCard();
                    sessionManager.deletePromotions();
                    sessionManager.setPricing(null);
                    sessionManager.deleteLocation();

                    Utils.writeFile(configFilePath, UrlPath);

                }

                redirectToMain();
            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_url));
            }

            Intent intent = new Intent(this, EditorTool.class);
            startActivity(intent);
            finish();
        }
    }


    /**
     * Responsibility -  redirectToMain method redirect screen to MainActivity
     * Parameters - No parameter
     **/
    private void redirectToMain() {
        sessionManager.onBoarding(Constraint.TRUE);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    /**
     * Responsibility - getProductNameListener method is used when we select any new product in that case we need to change  selected product value in session
     * Parameters - No parameter
     **/
    private AdapterView.OnItemSelectedListener getProductNameListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.setSelectedProduct(mViewModel.getProducts().get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }


    /**
     * Responsibility - getCarrierListener method is used when we select any new carrier in that case we need to change  selected carrier value in session
     * Parameters - No parameter
     **/
    private AdapterView.OnItemSelectedListener getCarrierListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Carrier carrier = mViewModel.getCarriers().get(position);
                mViewModel.setSelectedCarrier(carrier);
                Manufacture manufacture = (Manufacture) alertBinding.manufactureList.getSelectedItem();
                mViewModel.setSelectedManufacture(manufacture);
                getGeneralResponse(carrier, manufacture);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

    }

    /**
     * Responsibility - getGeneralResponse method is used to get general response by firing general api
     * Parameters - Its take carrier and manufacture object
     **/
    private void getGeneralResponse(Carrier carrier, Manufacture manufacture) {

        if (Utils.getNetworkState(context)) {
            showHideProgressDialog(true);
            HashMap<String, String> generalRequest = getGeneralRequest(carrier, manufacture);
            mViewModel.setGeneralRequest(generalRequest);
            LiveData<GlobalResponse<GeneralResponse>> liveData = mViewModel.getGeneralResponseLiveData();
            if (!liveData.hasActiveObservers()) {
                liveData.observe(this, new Observer<GlobalResponse<GeneralResponse>>() {
                    @Override
                    public void onChanged(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {

                        handleResponse(generalResponseGlobalResponse);
                    }
                });
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }
    }


    /**
     * Responsibility - getGeneralRequest method  takes Carrier and Manufacture as parameter and create a request for general api
     * Parameters - Its takes Carrier and Manufacture object as an parameter
     **/
    private HashMap<String, String> getGeneralRequest(Carrier carrier, Manufacture manufacture) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (carrier != null)
            hashMap.put(Constraint.CARRIER_ID, carrier.getIdcarrier() + "");
        if (manufacture != null)
            hashMap.put(Constraint.MANUFACTURE_ID, manufacture.getIdterm());
        return hashMap;
    }


    /**
     * Responsibility - handleResponse method  takes GlobalResponse<GeneralResponse> as parameter and check if response is ok then set set value in product name adaptor
     * Parameters - Its takes GlobalResponse<GeneralResponse> response that help to set value in product name adaptor
     **/
    private void handleResponse(GlobalResponse<GeneralResponse> generalResponse) {
        showHideProgressDialog(false);
        if (generalResponse != null) {
            if (generalResponse.isApi_status()) {

                sessionManager.setOSType(generalResponse.getResult().getOsTypes());
                mViewModel.setProducts(generalResponse.getResult().getProducts());
                if (mViewModel.getProducts() != null) {
                    ArrayAdapter<Product> productArrayAdapter = new ArrayAdapter<Product>(context, android.R.layout.simple_spinner_item, mViewModel.getProducts());
                    productArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    alertBinding.productName.setAdapter(productArrayAdapter);
                }

            } else {
                ValidationHelper.showToast(context, generalResponse.getMessage());
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.invalid_url));
        }

    }


    /**
     * Responsibility - getManufactureListener method is used when we select any new manufacture in that case we need to change  selected manufacture value in session
     * Parameters - No parameter
     **/
    private AdapterView.OnItemSelectedListener getManufactureListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Manufacture manufacture = mViewModel.getManufactures().get(position);
                mViewModel.setSelectedManufacture(manufacture);
                Carrier carrier = (Carrier) alertBinding.carrierName.getSelectedItem();
                mViewModel.setSelectedCarrier(carrier);

                getGeneralResponse(carrier, manufacture);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    /**
     * Responsibility - addOrientationData method is used for add orientation in array and pass it to view model
     * Parameters - No parameter
     **/
    private void addOrientationData() {
        ArrayList<String> orientation = new ArrayList<>();
        orientation.add(getString(R.string.defaultt));
        orientation.add(getString(R.string.landscape));
        mViewModel.setOrientation(orientation);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        SessionManager.get().isBrightnessIsDefault(true);

        if (seekBar.getTag().equals(Constraint.DEFAULT)) {
            SessionManager.get().customDefaultBrightness(i);
            brightnessBinding.defaultBrightnessLevel.setText((i * 10) + Constraint.PERCENTAGE);
        } else {
            SessionManager.get().customHighBrightness(i);

            brightnessBinding.maxBrightnessLevel.setText((i * 10) + Constraint.PERCENTAGE);

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
