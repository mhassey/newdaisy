package com.daisy.activity.onBoarding.slider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.daisy.BuildConfig;
import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.activity.onBoarding.slider.screenAdd.ScreenAddValidationHelper;
import com.daisy.activity.onBoarding.slider.screenAdd.ScreenAddViewModel;
import com.daisy.activity.onBoarding.slider.screenAdd.vo.ScreenAddResponse;
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreen;
import com.daisy.activity.onBoarding.slider.slides.permissionAsk.PermissionAsk;
import com.daisy.activity.onBoarding.slider.slides.securityAsk.SecurityAsk;
import com.daisy.activity.onBoarding.slider.slides.signup.SignUp;
import com.daisy.adapter.SliderAdapter;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.ActivityOnBaordingBinding;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.LoginResponse;
import com.daisy.pojo.response.PermissionDone;
import com.daisy.pojo.response.Product;
import com.daisy.security.Admin;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Purpose -  OnBoarding is an activity that handle all onBoarding pages
 * Responsibility - Its handle sign up and screen add  and card response
 **/
public class OnBoarding extends BaseActivity implements View.OnClickListener {
    private Context context;
    private List<Fragment> fragmentList = new ArrayList<>();
    private int count = 0;
    private SessionManager sessionManager;
    public ActivityOnBaordingBinding mBinding;
    public ScreenAddViewModel screenAddViewModel;
    private GetCardViewModel getCardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_baording);
        initView();
        initClick();
    }

    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {

        mBinding.nextSlide.setOnClickListener(this);
        mBinding.saveAndStartMpc.setOnClickListener(this);

    }

    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {
        context = this;
        mBinding.rootView.setVisibility(View.VISIBLE);
        sessionManager = SessionManager.get();
        screenAddViewModel = new ViewModelProvider(this).get(ScreenAddViewModel.class);
        getCardViewModel = new ViewModelProvider(this).get(GetCardViewModel.class);
        setNoTitleBar(this);
        stopSwipe();
        addFragmentList();
        setPager();
        disableSwipeOnViewPager();

    }


    /**
     * Responsibility - setPager method is used for setup pager and its behaviour
     * Parameters - No parameter
     **/
    private void setPager() {
        mBinding.pager.setAdapter(new SliderAdapter(getSupportFragmentManager(), 1, fragmentList));
        mBinding.tabDotsLayout.setupWithViewPager(mBinding.pager);

        mBinding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                count = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
    }


    /**
     * Responsibility - Disable swipe on view pager
     * Parameters - No parameter
     **/
    private void disableSwipeOnViewPager() {
        LinearLayout tabStrip = ((LinearLayout) mBinding.tabDotsLayout.getChildAt(Constraint.ZERO));
        for (int i = Constraint.ZERO; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
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
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }


    /**
     * Responsibility - Add all fragment to list that should display on view pager
     * Parameters - No parameter
     **/
    private void addFragmentList() {
        fragmentList.add(PermissionAsk.getInstance(mBinding));
        fragmentList.add(SecurityAsk.getInstance(mBinding));
        fragmentList.add(SignUp.getInstance(OnBoarding.this));
        fragmentList.add(AddScreen.getInstance(OnBoarding.this));


    }

    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextSlide: {
                nextSlideClickHandler();
                break;
            }
            case R.id.saveAndStartMpc: {

                getCardData();
            }
        }
    }

    /**
     * Responsibility -getCardData method is used for sending card request and accessing response
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
     * Responsibility -Handle card response if response is ok then set promotion ,pricing and promotion values and call redirectToMainHandler method
     * Parameters - Its takes GlobalResponse<GetCardResponse> object as parameter
     **/
    private void handleCardGetResponse(GlobalResponse<GetCardResponse> getCardResponseGlobalResponse) throws IOException {
        showHideProgressDialog(false);
        if (getCardResponseGlobalResponse.isApi_status()) {
            sessionManager.setPriceCard(getCardResponseGlobalResponse.getResult().getPricecard());
            sessionManager.setPromotion(getCardResponseGlobalResponse.getResult().getPromotions());
            sessionManager.setPricing(getCardResponseGlobalResponse.getResult().getPricing());
            redirectToMainHandler(getCardResponseGlobalResponse);

        } else {
            if (getCardResponseGlobalResponse.getResult().getDefaultPriceCard() != null && !getCardResponseGlobalResponse.getResult().getDefaultPriceCard().equals("")) {
                redirectToMainHandler(getCardResponseGlobalResponse);
            } else
                ValidationHelper.showToast(context, getCardResponseGlobalResponse.getMessage());
        }
    }

    /**
     * Responsibility -getCardRequest method create card request
     * Parameters - No parameter
     **/
    private HashMap<String, String> getCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.SCREEN_ID, sessionManager.getScreenId() + "");
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }

    /**
     * Responsibility -Handle Slide click
     * Parameters - No parameter
     **/
    public void nextSlideClickHandler() {

        count = count + Constraint.ONE;

        if (count == Constraint.TWO) {

            SecurityAsk securityAsk = (SecurityAsk) fragmentList.get(count - Constraint.ONE);
            if (securityAsk != null && securityAsk.securityAskBinding != null) {
                try {
                    if (securityAsk.securityAskBinding.deletePhoto.isChecked()) {
                        sessionManager.setDeletePhoto(true);
                    } else {
                        sessionManager.setDeletePhoto(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (securityAsk.securityAskBinding.lockToBrowser.isChecked()) {
                    sessionManager.setLockOnBrowser(true);
                } else {
                    sessionManager.setLockOnBrowser(false);

                }
                if (securityAsk.securityAskBinding.lockToMessage.isChecked()) {
                    sessionManager.setLockOnMessage(true);
                } else {
                    sessionManager.setLockOnMessage(false);

                }
                if (securityAsk.securityAskBinding.lock.isChecked()) {
                    sessionManager.setLock(true);
                } else {
                    sessionManager.setLock(false);

                }

            }

        } else if (count == Constraint.THREE) {
            SignUp signUp = (SignUp) fragmentList.get(Constraint.TWO);

            signUp.loginBinding.singup.performClick();
        } else if (count == Constraint.FOUR) {
//            AddScreen screen = (AddScreen) fragmentList.get(Constraint.THREE);

//            handleCreateScreen(screen.mViewModel.getAutoSelctedProduct());
            handleCreateScreen(null);
        }

        if (count == Constraint.ONE || count == Constraint.TWO) {
            mBinding.pager.setCurrentItem(count);
        }
    }

    public void handleCreateScreen(Product product) {
        if (Utils.getNetworkState(context)) {
            AddScreen addScreen = (AddScreen) fragmentList.get(Constraint.THREE);
            ScreenAddValidationHelper screenAddValidationHelper = new ScreenAddValidationHelper(context, addScreen.mBinding);
            if (screenAddValidationHelper.isValid()) {
                showHideProgressDialog(true);
                screenAddViewModel.setMutableLiveData(getAddScreenRequest(addScreen, product));
                LiveData<GlobalResponse<ScreenAddResponse>> liveData = screenAddViewModel.getLiveData();
                if (!liveData.hasActiveObservers()) {
                    liveData.observe(this, new Observer<GlobalResponse<ScreenAddResponse>>() {
                        @Override
                        public void onChanged(GlobalResponse<ScreenAddResponse> screenAddResponseGlobalResponse) {
                            showHideProgressDialog(false);
                            handleScreenAddResponse(screenAddResponseGlobalResponse, addScreen);
                        }
                    });
                }
            } else {
                count = Constraint.THREE;
            }
        } else {
            count = Constraint.THREE;
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }

    }

    /**
     * Responsibility - Handle screen add response and set value in session and call getCardData method
     * Parameters - Its takes GlobalResponse<ScreenAddResponse> and AddScreen object as parameter
     **/
    private void handleScreenAddResponse(GlobalResponse<ScreenAddResponse> screenAddResponseGlobalResponse, AddScreen addScreen) {
        if (screenAddResponseGlobalResponse.isApi_status()) {
            DBCaller.storeLogInDatabase(context, screenAddResponseGlobalResponse.getResult().getId() + Constraint.SCREEN_ADD, "", "", Constraint.APPLICATION_LOGS);
            mBinding.nextSlide.setVisibility(View.GONE);
            mBinding.saveAndStartMpcHeader.setVisibility(View.GONE);
            mBinding.pager.setCurrentItem(count);
            sessionManager.setDeviceId(screenAddResponseGlobalResponse.getResult().getIddevice());
            sessionManager.setScreenID(screenAddResponseGlobalResponse.getResult().getId());
            sessionManager.setDeviceToken(screenAddResponseGlobalResponse.getResult().getToken());
            sessionManager.setScreenPosition(screenAddResponseGlobalResponse.getResult().getScreenPosition());
            sessionManager.setOrientation(addScreen.mBinding.webkitOrientation.getSelectedItem().toString());
            getCardData();


        } else {
            ValidationHelper.showToast(context, screenAddResponseGlobalResponse.getMessage());
        }
    }

    /**
     * Responsibility - Create add screen request
     * Parameters - Its takes AddScreen object as parameter
     **/
    private HashMap<String, String> getAddScreenRequest(AddScreen addScreen, Product product) {
        if (addScreen != null) {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constraint.ISLE, addScreen.mBinding.isle.getText().toString());
            hashMap.put(Constraint.SHELF, addScreen.mBinding.shelf.getText().toString());
            hashMap.put(Constraint.POSITION, addScreen.mBinding.position.getText().toString());
            if (product == null) {
                if (screenAddViewModel.getDeviceId() != null && !screenAddViewModel.getDeviceId().equals("") && !screenAddViewModel.getDeviceId().equals("0")) {
                    hashMap.put(Constraint.DEVICEID, screenAddViewModel.getDeviceId());
                } else {
                    hashMap.put(Constraint.DEVICEID, "0");
                    hashMap.put(Constraint.DEVICE_NAME, Utils.ModelNumber());

                }
                if (addScreen.mViewModel.getSelectedProduct() != null) {
                    if (addScreen.mViewModel.getSelectedProduct().getIdproductStatic() != null)
                        hashMap.put(Constraint.ID_PRODUCT_STATIC, addScreen.mViewModel.getSelectedProduct().getIdproductStatic());

                } else {
                    ValidationHelper.showToast(context, getString(R.string.product_not_available));

                }
            } else {
                if (product.getIdproductStatic() != null) {
                    hashMap.put(Constraint.ID_PRODUCT_STATIC, product.getIdproductStatic());
                    hashMap.put(Constraint.DEVICEID, screenAddViewModel.getDeviceId());
                    hashMap.put(Constraint.DEVICE_NAME, "");

                }
            }
            hashMap.put(Constraint.BUILD_VERSION, BuildConfig.VERSION_NAME + "");
            LoginResponse loginResponse = sessionManager.getLoginResponse();
            if (loginResponse != null)
                hashMap.put(Constraint.IDSTORE, loginResponse.getIdstore());

            hashMap.put(Constraint.MAC_ADDRESS, Utils.getMacAddress(getApplicationContext()));
            return hashMap;
        }
        return new HashMap<>();
    }

    /**
     * Responsibility - Increase counter of pager with dat
     * Parameters - No parameter
     **/
    public void counterPlus(String deviceId) {
        count = count + Constraint.ONE;
        if (count == Constraint.THREE) {
            mBinding.nextSlide.setVisibility(View.VISIBLE);

        }
        screenAddViewModel.setDeviceId(deviceId);

        mBinding.pager.setCurrentItem(count);

    }

    /**
     * Responsibility - Increase counter of pager
     * Parameters - No parameter
     **/
    public void counterPlus() {
        count = count + Constraint.ONE;
        if (count == Constraint.THREE) {
            mBinding.nextSlide.setVisibility(View.VISIBLE);

        }
        mBinding.pager.setCurrentItem(count);

    }

    /**
     * Responsibility - Decrease counter of pager
     * Parameters - No parameter
     **/
    public void counterMinus() {
        count = count - Constraint.ONE;
        if (count == Constraint.THREE) {
            mBinding.nextSlide.setVisibility(View.VISIBLE);

        }
        mBinding.pager.setCurrentItem(count);

    }

    /**
     * Responsibility -  Its delete daisy data and check if new price card is available in response then add new file path and call redirectToMain method
     * * Parameters -  Its takes GlobalResponse<GetCardResponse> object as parameter
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
                File directory;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    directory = new File(getExternalFilesDir(""), configFilePath);
                } else {
                    directory = new File(configFilePath);

                }
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
                    Utils.writeFile(configFilePath, UrlPath);
                }

                redirectToMain();
            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_url));
            }

            Intent intent = new Intent(OnBoarding.this, EditorTool.class);
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
                File directory;
                directory = new File(configFilePath);
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
                    Utils.writeFile(configFilePath, UrlPath);
                }

                redirectToMain();
            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_url));
            }

            Intent intent = new Intent(OnBoarding.this, EditorTool.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Responsibility - redirectToMain method is used for call Main Activity
     * Parameters - No parameter
     **/
    private void redirectToMain() {
        sessionManager.onBoarding(Constraint.TRUE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean request = true;
        if (requestCode == Constraint.RESPONSE_CODE) {

            for (int val : grantResults) {
                if (val == PackageManager.PERMISSION_DENIED) {
                    request = false;
                }
            }
        }

        if (request) {
            PermissionDone permissionDone = new PermissionDone();

            permissionDone.setPermissionName(Constraint.MEDIA_PERMISSION);
            EventBus.getDefault().post(permissionDone);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constraint.CODE_WRITE_SETTINGS_PERMISSION) {
            if (Settings.System.canWrite(this)) {
                PermissionDone permissionDone = new PermissionDone();
                permissionDone.setPermissionName(Constraint.MODIFY_SYSTEM_SET);
                EventBus.getDefault().post(permissionDone);
            }
        } else if (requestCode == Constraint.ADMIN) {
            Admin admin = new Admin();
            EventBus.getDefault().post(admin);
        } else if (requestCode == Constraint.POP_UP_RESPONSE) {
            if (Settings.canDrawOverlays(this)) {
                PermissionDone permissionDone = new PermissionDone();
                permissionDone.setPermissionName(Constraint.DISPLAY_OVER_THE_APP);
                EventBus.getDefault().post(permissionDone);
            }
        } else if (requestCode == Constraint.RETURN) {
            if (Utils.isAccessGranted(getApplicationContext())) {
                PermissionDone permissionDone = new PermissionDone();
                permissionDone.setPermissionName(Constraint.GRAND_USAGE_ACCESS);
                EventBus.getDefault().post(permissionDone);
            }
        } else if (requestCode == Constraint.BATTRY_OPTIMIZATION_CODE) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

            if (pm.isIgnoringBatteryOptimizations(getString(R.string.packageName))) {
                PermissionDone permissionDone = new PermissionDone();
                permissionDone.setPermissionName(Constraint.BATTRY_OPTI);
                EventBus.getDefault().post(permissionDone);

            }
        } else if (requestCode == Constraint.MI_EXTRA_PERMISSION_CODE) {
            PermissionDone permissionDone = new PermissionDone();
            permissionDone.setPermissionName(Constraint.REDME);
            EventBus.getDefault().post(permissionDone);
        } else if (requestCode == Constraint.GPS_ENABLE) {
            PermissionDone permissionDone = new PermissionDone();
            permissionDone.setPermissionName(Constraint.GPS);
            EventBus.getDefault().post(permissionDone);
        }
    }


    /**
     * Responsibility - stopSwipe restrict the view pager to not swipe
     * Parameters - No parameter
     **/
    private void stopSwipe() {

        mBinding.pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }

        });

    }


}
