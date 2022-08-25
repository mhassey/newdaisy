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
import com.daisy.activity.onBoarding.slider.screenAdd.ScreenAddViewModel;
import com.daisy.activity.onBoarding.slider.screenAdd.vo.ScreenAddResponse;
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreen;
import com.daisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.daisy.activity.onBoarding.slider.slides.permissionAsk.PermissionAsk;
import com.daisy.activity.onBoarding.slider.slides.signup.SignUpViewModel;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.daisy.adapter.SliderAdapter;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.ActivityOnBaordingBinding;
import com.daisy.pojo.response.Carrier;
import com.daisy.pojo.response.GeneralResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.LoginResponse;
import com.daisy.pojo.response.OsType;
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
    private SignUpViewModel signUpViewModel;
    public AddScreenViewModel mViewModel;


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
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        mViewModel = new ViewModelProvider(this).get(AddScreenViewModel.class);

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
//        fragmentList.add(SecurityAsk.getInstance(mBinding));
//        fragmentList.add(SignUp.getInstance(OnBoarding.this));
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

        if (count == Constraint.ONE) {

            doSignUp();

//            SecurityAsk securityAsk = (SecurityAsk) fragmentList.get(count - Constraint.ONE);
//            if (securityAsk != null && securityAsk.securityAskBinding != null) {
//                try {
//                    if (securityAsk.securityAskBinding.deletePhoto.isChecked()) {
//                        sessionManager.setDeletePhoto(true);
//                    } else {
//                        sessionManager.setDeletePhoto(false);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (securityAsk.securityAskBinding.lockToBrowser.isChecked()) {
//                    sessionManager.setLockOnBrowser(true);
//                } else {
//                    sessionManager.setLockOnBrowser(false);
//
//                }
//                if (securityAsk.securityAskBinding.lockToMessage.isChecked()) {
//                    sessionManager.setLockOnMessage(true);
//                } else {
//                    sessionManager.setLockOnMessage(false);
//
//                }
//                if (securityAsk.securityAskBinding.lock.isChecked()) {
//                    sessionManager.setLock(true);
//                } else {
//                    sessionManager.setLock(false);
//
//                }
//
//            }

        }
//        else if (count == Constraint.THREE) {
//            SignUp signUp = (SignUp) fragmentList.get(Constraint.TWO);
//
//            signUp.loginBinding.singup.performClick();
//        }
        else if (count == Constraint.TWO) {
//            AddScreen screen = (AddScreen) fragmentList.get(Constraint.THREE);

//            handleCreateScreen(screen.mViewModel.getAutoSelctedProduct());
            handleCreateScreen(null, true);
        }

//        if (count == Constraint.ONE || count == Constraint.TWO) {
//            mBinding.pager.setCurrentItem(count);
//        }
    }


    /**
     * Purpose - doSignUp method handles sign up api
     */
    private void doSignUp() {
        if (Utils.getNetworkState(context)) {
            showHideProgressDialog(true);
            HashMap<String, String> signUpRequest = getSignUpRequest();
            signUpViewModel.setSignUpRequestMutableLiveData(signUpRequest);
            LiveData<SignUpResponse> liveData = signUpViewModel.getResponseLiveData();
            if (!liveData.hasActiveObservers()) {
                liveData.observe(this, new Observer<SignUpResponse>() {
                    @Override
                    public void onChanged(SignUpResponse signUpResponse) {
                        handleResponse(signUpResponse);
                    }
                });
            }

        } else {
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
            counterMinus();
        }
    }

    /**
     * Purpose - handleResponse method handle sign up response
     *
     * @param signUpResponse
     */
    private void handleResponse(SignUpResponse signUpResponse) {
        showHideProgressDialog(false);
        if (signUpResponse != null) {
            if (signUpResponse.isApi_status()) {
                DBCaller.storeLogInDatabase(context, Constraint.LOGIN_SUCCESSFULL, "", "", Constraint.APPLICATION_LOGS);
                sessionManager.setPasswordForLock("moto1");
                sessionManager.setOpenTime(signUpResponse.getData().getOpen());
                sessionManager.setCloseTime(signUpResponse.getData().getClosed());
                sessionManager.setOffset(signUpResponse.getData().getUTCOffset());
                screenAddViewModel.setDeviceId(signUpResponse.getData().getDeviceId());

                sessionManager.setSenitized(signUpResponse.getData().getDeviceSanitize());
                sessionManager.setDeviceSecurity(signUpResponse.getData().getDeviceSecurity());
                sessionManager.setPricingPlainId(signUpResponse.getData().getPricingPlanID());
                sessionManager.setServerTime(signUpResponse.getData().getCurrentTime());
                sessionManager.setSignUpData(signUpResponse.getData());
                List<Carrier> carriers = sessionManager.getLoginResponse().getCarrier();

                if (screenAddViewModel.getDeviceId() != null && !screenAddViewModel.getDeviceId().equals("") && !screenAddViewModel.getDeviceId().equals("0")) {
                    getGeneralResponseForProductSelection(screenAddViewModel.getDeviceId(), carriers.get(0));

                } else
                    counterPlus(signUpResponse.getData().getDeviceId());
            } else {

                counterMinus();
                ValidationHelper.showToast(context, signUpResponse.getMessage());
            }
        } else {

            counterMinus();
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }

    }

    /**
     * Responsibility - getGeneralResponse method is used for fire general api and get response and send response to handleResponse method
     * Parameters - Its takes Carrier,Manufacture object as parameter
     **/
    private void getGeneralResponseForProductSelection(String deviceId, Carrier carrier) {
        String id = deviceId;
        if (Utils.getNetworkState(context)) {
            showHideProgressDialog(true);
            HashMap<String, String> generalRequest = getGeneralRequest(deviceId, carrier);
            mViewModel.setGeneralRequestForDeviceSpecific(generalRequest);
            LiveData<GlobalResponse<GeneralResponse>> liveData = mViewModel.getGeneralResponseLiveDataForDeviceSpecific();
            if (!liveData.hasActiveObservers()) {
                liveData.observe(this, new Observer<GlobalResponse<GeneralResponse>>() {
                    @Override
                    public void onChanged(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {
                        showHideProgressDialog(false);
                        if (generalResponseGlobalResponse.isApi_status()) {
                            handleProductListData(generalResponseGlobalResponse, id);
                        }
                    }
                });
            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }
    }

    /**
     * Purpose - handleProductListData handles the auto detected screen response
     *
     * @param generalResponseGlobalResponse
     * @param id
     */
    private void handleProductListData(GlobalResponse<GeneralResponse> generalResponseGlobalResponse, String id) {
        if (generalResponseGlobalResponse.getResult().getProducts() != null) {

            List<Product> products = generalResponseGlobalResponse.getResult().getProducts();
            sessionManager.setOSType(generalResponseGlobalResponse.getResult().getOsTypes());

            if (products != null && products.size() > 0) {
                mViewModel.isManufactureSelected = false;
                Product product = products.get(0);
                mViewModel.setAutoSelectProduct(product);
                if (mViewModel.getAutoSelctedProduct() != null) {

                    handleCreateScreen(null, false);
                }
            } else {
                counterPlus(id);

            }
        }
    }

    /**
     * Responsibility - getGeneralRequest method is used for create general api request
     **/
    private HashMap<String, String> getGeneralRequest(String deviceId, Carrier carrier) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.DEVICE_ID, deviceId);
        hashMap.put(Constraint.CARRIER_ID, carrier.getIdcarrier() + "");
        return hashMap;
    }


    /**
     * Purpose - Create signup request
     *
     * @return
     */
    private HashMap<String, String> getSignUpRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.STORE_CODE, "moto1");
        hashMap.put(Constraint.PASSWORD_ID, "moto1");
        hashMap.put(Constraint.DEVICENAME, Utils.ModelNumber());
        return hashMap;
    }

    public void handleCreateScreen(Product product, boolean callFrom) {
        if (Utils.getNetworkState(context)) {
            showHideProgressDialog(true);
            screenAddViewModel.setMutableLiveData(getAddScreenRequest(product, callFrom));
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

        } else {
            count = Constraint.THREE;
            ValidationHelper.showToast(context, getString(R.string.no_internet_available));
        }

    }

    /**
     * Responsibility - Handle screen add response and set value in session and call getCardData method
     * Parameters - Its takes GlobalResponse<ScreenAddResponse> and AddScreen object as parameter
     **/
    private void handleScreenAddResponse(GlobalResponse<ScreenAddResponse> screenAddResponseGlobalResponse) {
        if (screenAddResponseGlobalResponse.isApi_status()) {
            DBCaller.storeLogInDatabase(context, screenAddResponseGlobalResponse.getResult().getId() + Constraint.SCREEN_ADD, "", "", Constraint.APPLICATION_LOGS);
            mBinding.saveAndStartMpcHeader.setVisibility(View.GONE);
            sessionManager.setDeviceId(screenAddResponseGlobalResponse.getResult().getIddevice());
            sessionManager.setScreenID(screenAddResponseGlobalResponse.getResult().getId());
            sessionManager.setDeviceToken(screenAddResponseGlobalResponse.getResult().getToken());
            sessionManager.setScreenPosition(screenAddResponseGlobalResponse.getResult().getScreenPosition());
            sessionManager.setOrientation(getString(R.string.defaultt));
            getCardData();


        } else {
            ValidationHelper.showToast(context, screenAddResponseGlobalResponse.getMessage());
        }
    }

    /**
     * Responsibility - Create add screen request
     * Parameters - Its takes AddScreen object as parameter
     **/
    private HashMap<String, String> getAddScreenRequest(Product product, boolean callFrom) {


        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.ISLE, Constraint.ONE_STRING);
        hashMap.put(Constraint.SHELF, Constraint.ONE_STRING);
        hashMap.put(Constraint.POSITION, Constraint.ONE_STRING);
        if (product == null) {
            if (screenAddViewModel.getDeviceId() != null && !screenAddViewModel.getDeviceId().equals("") && !screenAddViewModel.getDeviceId().equals("0")) {
                hashMap.put(Constraint.DEVICEID, screenAddViewModel.getDeviceId());
            } else {
                hashMap.put(Constraint.DEVICEID, "0");
                hashMap.put(Constraint.DEVICE_NAME, Utils.ModelNumber());

            }
            if (callFrom) {
                AddScreen addScreen = (AddScreen) fragmentList.get(Constraint.ONE);

                if (addScreen.mViewModel.getSelectedProduct() != null) {
                    if (addScreen.mViewModel.getSelectedProduct().getIdproductStatic() != null)
                        hashMap.put(Constraint.ID_PRODUCT_STATIC, addScreen.mViewModel.getSelectedProduct().getIdproductStatic());

                } else if (addScreen.mViewModel.getAutoSelctedProduct() != null) {
                    if (addScreen.mViewModel.getAutoSelctedProduct().getIdproductStatic() != null)
                        hashMap.put(Constraint.ID_PRODUCT_STATIC, addScreen.mViewModel.getAutoSelctedProduct().getIdproductStatic());
                } else {
                    ValidationHelper.showToast(context, getString(R.string.product_not_available));

                }
            } else {
                if (mViewModel.getSelectedProduct() != null) {
                    if (mViewModel.getSelectedProduct().getIdproductStatic() != null)
                        hashMap.put(Constraint.ID_PRODUCT_STATIC, mViewModel.getSelectedProduct().getIdproductStatic());

                } else if (mViewModel.getAutoSelctedProduct() != null) {
                    if (mViewModel.getAutoSelctedProduct().getIdproductStatic() != null)
                        hashMap.put(Constraint.ID_PRODUCT_STATIC, mViewModel.getAutoSelctedProduct().getIdproductStatic());
                } else {
                    ValidationHelper.showToast(context, getString(R.string.product_not_available));

                }
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
        hashMap.put(Constraint.DEVICE_TOKEN, SessionManager.get().getFCMToken());
        for (OsType osType : SessionManager.get().getOsType()) {
            if (osType.getOsName().equals(Constraint.ANDROID)) {
                hashMap.put(Constraint.DEVICE_TYPE, osType.getOsID() + "");

            }


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
