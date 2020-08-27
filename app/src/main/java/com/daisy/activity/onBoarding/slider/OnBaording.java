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
import com.daisy.activity.onBoarding.slider.deviceDetection.DeviceDetectionViewModel;
import com.daisy.activity.onBoarding.slider.deviceDetection.vo.DeviceDetectRequest;
import com.daisy.activity.onBoarding.slider.deviceDetection.vo.DeviceDetectResponse;
import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.activity.onBoarding.slider.screenAdd.ScreenAddValidationHelper;
import com.daisy.activity.onBoarding.slider.screenAdd.ScreenAddViewModel;
import com.daisy.activity.onBoarding.slider.screenAdd.vo.ScreenAddResponse;
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreen;
import com.daisy.activity.onBoarding.slider.slides.deviceDetection.DeviceDetection;
import com.daisy.activity.onBoarding.slider.slides.permissionAsk.PermissionAsk;
import com.daisy.activity.onBoarding.slider.slides.securityAsk.SecurityAsk;
import com.daisy.activity.onBoarding.slider.slides.signup.SignUp;
import com.daisy.adapter.SliderAdapter;
import com.daisy.database.DBCaller;
import com.daisy.utils.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityOnBaordingBinding;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.LoginResponse;
import com.daisy.pojo.response.PermissionDone;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnBaording extends BaseActivity implements View.OnClickListener {
    private Context context;
    private List<Fragment> fragmentList = new ArrayList<>();
    private int count = 0;
    private SessionManager sessionManager;
    public ActivityOnBaordingBinding mBinding;
    private DeviceDetectionViewModel deviceDetectionViewModel;
    private ScreenAddViewModel screenAddViewModel;
    private GetCardViewModel getCardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_baording);
        initView();
        initClick();
    }

    private void initClick() {

        mBinding.nextSlide.setOnClickListener(this);
        mBinding.saveAndStartMpc.setOnClickListener(this);

    }


    private void initView() {
        context = this;
        mBinding.rootView.setVisibility(View.VISIBLE);
        sessionManager = SessionManager.get();
        //deviceDetectionViewModel=new ViewModelProvider(this).get(DeviceDetectionViewModel.class);
        screenAddViewModel = new ViewModelProvider(this).get(ScreenAddViewModel.class);
        getCardViewModel = new ViewModelProvider(this).get(GetCardViewModel.class);
        setNoTitleBar(this);
        stopSwipe();
        addFragementList();
        setPager();
        disableSwipeOnViewPager();
    }

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


    private void disableSwipeOnViewPager() {
        LinearLayout tabStrip = ((LinearLayout) mBinding.tabDotsLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }

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


    private void addFragementList() {
        fragmentList.add(PermissionAsk.getInstance(mBinding));
        fragmentList.add(SecurityAsk.getInstance());
        fragmentList.add(SignUp.getInstance(OnBaording.this));
        fragmentList.add(AddScreen.getInstance());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextSlide: {
                nextSlideClickHandler();
                break;
            }
            case R.id.saveAndStartMpc: {

                getCardData();
                // getDeviceZipFile();
            }
        }
    }

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
                            DBCaller.storeLogInDatabase(context,getCardResponseGlobalResponse.getResult().getPricecard().getPriceCardName()+getString(R.string.data_store),"","",Constraint.APPLICATION_LOGS);
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

    private void handleCardGetResponse(GlobalResponse<GetCardResponse> getCardResponseGlobalResponse) throws IOException {
        showHideProgressDialog(false);
        if (getCardResponseGlobalResponse.isApi_status()) {
            sessionManager.onBoarding(true);
            sessionManager.setPriceCard(getCardResponseGlobalResponse.getResult().getPricecard());
            sessionManager.setPromotion(getCardResponseGlobalResponse.getResult().getPromotions());
            sessionManager.setPricing(getCardResponseGlobalResponse.getResult().getPricing());
            redirectToMainHandler(getCardResponseGlobalResponse);

        } else {
            if (getCardResponseGlobalResponse.getResult().getDefaultPriceCard()!=null && !getCardResponseGlobalResponse.getResult().getDefaultPriceCard().equals(""))
            {
                 redirectToMainHandler(getCardResponseGlobalResponse);
            }
            else
            ValidationHelper.showToast(context, getCardResponseGlobalResponse.getMessage());
        }
        }

    private HashMap<String, String> getCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.SCREEN_ID, sessionManager.getScreenId() + "");
        hashMap.put(Constraint.TOKEN,sessionManager.getDeviceToken());
//        hashMap.put(Constraint.SCREEN_ID, "47");
        return hashMap;
    }

    public void nextSlideClickHandler() {
        count = count + 1;

        if (count == 2) {
            SecurityAsk securityAsk = (SecurityAsk) fragmentList.get(count - 1);
            if (securityAsk.securityAskBinding.deletePhoto.isChecked()) {
                sessionManager.setDeletePhoto(true);
            } else {
                sessionManager.setDeletePhoto(false);
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

            mBinding.nextSlide.setVisibility(View.GONE);
        }


        if (count == 4) {

            if (Utils.getNetworkState(context)) {
                AddScreen addScreen = (AddScreen) fragmentList.get(Constraint.THREE);
                ScreenAddValidationHelper screenAddValidationHelper=new ScreenAddValidationHelper(context,addScreen.mBinding);
                if (screenAddValidationHelper.isValid()) {
                    showHideProgressDialog(true);
                    screenAddViewModel.setMutableLiveData(getAddScreenRequest(addScreen));
                    LiveData<GlobalResponse<ScreenAddResponse>> liveData = screenAddViewModel.getLiveData();
                    if (!liveData.hasActiveObservers()) {
                        liveData.observe(this, new Observer<GlobalResponse<ScreenAddResponse>>() {
                            @Override
                            public void onChanged(GlobalResponse<ScreenAddResponse> screenAddResponseGlobalResponse) {
                                showHideProgressDialog(false);
                                handleScreenAddResponse(screenAddResponseGlobalResponse);
                            }
                        });
                    }
                }
                else {
                    count=3;
                }
            }
            else
            {
                count=3;
                ValidationHelper.showToast(context,getString(R.string.no_internet_available));
            }


        }

        if (count == 1 || count == 2) {
            mBinding.pager.setCurrentItem(count);
        }
    }

    private void handleScreenAddResponse(GlobalResponse<ScreenAddResponse> screenAddResponseGlobalResponse) {
        if (screenAddResponseGlobalResponse.isApi_status()) {
            DBCaller.storeLogInDatabase(context,screenAddResponseGlobalResponse.getResult().getId()+getString(R.string.screen_add),"","",Constraint.APPLICATION_LOGS);
            mBinding.nextSlide.setVisibility(View.GONE);
            mBinding.saveAndStartMpcHeader.setVisibility(View.VISIBLE);
            mBinding.pager.setCurrentItem(count);
            sessionManager.setScreenID(screenAddResponseGlobalResponse.getResult().getId());
            sessionManager.setDeviceToken(screenAddResponseGlobalResponse.getResult().getToken());
            sessionManager.setScreenPosition(screenAddResponseGlobalResponse.getResult().getScreenPosition());

            getCardData();




        } else {
            ValidationHelper.showToast(context, screenAddResponseGlobalResponse.getMessage());
        }
    }

    private HashMap<String, String> getAddScreenRequest(AddScreen addScreen) {
         HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.ISLE, addScreen.mBinding.isle.getText().toString());
        hashMap.put(Constraint.SHELF, addScreen.mBinding.shelf.getText().toString());
        hashMap.put(Constraint.POSITION, addScreen.mBinding.position.getText().toString());
        if (addScreen.selectedProduct!=null) {
            if (addScreen.selectedProduct.getIdproductStatic() != null)
                hashMap.put(Constraint.ID_PRODUCT_STATIC, addScreen.selectedProduct.getIdproductStatic());
        }
        else
        {
         ValidationHelper.showToast(context,getString(R.string.product_not_available));

        }
        hashMap.put(Constraint.DEVICE_NAME,Utils.getDeviceName());
        hashMap.put(Constraint.BUILD_VERSION, BuildConfig.VERSION_CODE+"");
        LoginResponse loginResponse = sessionManager.getLoginResponse();
        hashMap.put(Constraint.IDSTORE, loginResponse.getIdstore());
        return hashMap;
    }


    public void counterPlus() {
        count = count + 1;
        if (count == 3) {
            mBinding.nextSlide.setVisibility(View.VISIBLE);

        }
        mBinding.pager.setCurrentItem(count);

    }

    private void redirectToMainHandler(GlobalResponse<GetCardResponse> response) throws IOException {
        Utils.deleteDaisy();
       String UrlPath= response.getResult().getPricecard().getFileName();
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
                        Utils.writeFile(configFilePath,UrlPath);
                        sessionManager.deleteLocation();

                        DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                    }
                } else {
                    Utils.writeFile(configFilePath, UrlPath);
                }

                redirectToMain();

        } else if (response.getResult().getDefaultPriceCard()!=null && !response.getResult().getDefaultPriceCard().equals("")) {
             UrlPath= response.getResult().getDefaultPriceCard();
            String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;
            File directory = new File(configFilePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String path = Utils.getPath();
            if (path != null) {
                if (!path.equals(UrlPath)) {
                    Utils.deleteCardFolder();
                    Utils.writeFile(configFilePath,UrlPath);
                    sessionManager.deleteLocation();

                    DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, UrlPath, Constraint.APPLICATION_LOGS);

                }
            } else {
                Utils.writeFile(configFilePath, UrlPath);
            }

            redirectToMain();
        }
        else{
            ValidationHelper.showToast(context, getString(R.string.invalid_url));
        }

        Intent intent = new Intent(OnBaording.this, EditorTool.class);
        startActivity(intent);
        finish();
    }

    private void redirectToMain() {
        Intent intent = new Intent(this, MainActivity.class);
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
        }
        else if (requestCode==Constraint.MI_EXTRA_PERMISSION_CODE)
        {
            PermissionDone permissionDone = new PermissionDone();
            permissionDone.setPermissionName(Constraint.REDME);
            EventBus.getDefault().post(permissionDone);
        }
    }


    private void stopSwipe() {

        mBinding.pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }

        });

    }


    private void getDeviceZipFile() {
        if (Utils.getNetworkState(context)) {
            DeviceDetectRequest deviceDetectRequest = getDeviceRequest();
            deviceDetectionViewModel.setDetectRequestMutableLiveData(deviceDetectRequest);
            LiveData<DeviceDetectResponse> liveData = deviceDetectionViewModel.getResponseLiveData();
            if (!liveData.hasActiveObservers()) {
                liveData.observe(this, new Observer<DeviceDetectResponse>() {
                    @Override
                    public void onChanged(DeviceDetectResponse deviceDetectResponse) {
                        handleDeviceDetectResponse(deviceDetectResponse);
                    }
                });
            }
        }
    }

    private void handleDeviceDetectResponse(DeviceDetectResponse deviceDetectResponse) {
        // handleDetect
    }

    private DeviceDetectRequest getDeviceRequest() {
        DeviceDetectRequest deviceDetectRequest = new DeviceDetectRequest();
        deviceDetectRequest.setDeviceName(Utils.getDeviceName());
        return deviceDetectRequest;
    }

}
