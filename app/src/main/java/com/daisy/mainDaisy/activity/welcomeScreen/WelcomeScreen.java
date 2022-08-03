package com.daisy.mainDaisy.activity.welcomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.databinding.ActivityWelcomeScreenBinding;
import com.daisy.mainDaisy.activity.base.BaseActivity;
import com.daisy.mainDaisy.activity.onBoarding.slider.OnBoarding;
import com.daisy.mainDaisy.activity.onBoarding.slider.slides.addScreen.AddScreenViewModel;
import com.daisy.mainDaisy.common.session.SessionManager;
import com.daisy.mainDaisy.dialogFragment.DateTimePermissionDIalog;
import com.daisy.mainDaisy.pojo.response.GeneralResponse;
import com.daisy.mainDaisy.pojo.response.GlobalResponse;
import com.daisy.mainDaisy.pojo.response.KeyToUrlResponse;
import com.daisy.mainDaisy.utils.Constraint;
import com.daisy.mainDaisy.utils.Utils;
import com.daisy.mainDaisy.utils.ValidationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Locale;

/**
 * Purpose -  WelcomeScreen is an activity that show some content as welcome page to user
 * Responsibility - Its show some useful content for user and its also has begin method that redirect screen to on boarding
 **/
public class WelcomeScreen extends BaseActivity implements View.OnClickListener {

    private ActivityWelcomeScreenBinding mBinding;
    private SessionManager sessionManager;
    final int sdk = android.os.Build.VERSION.SDK_INT;
    private WelcomeValidationHelper welcomeValidationHelper;
    private WelcomeViewModel welcomeViewModel;
    private String myBaseUrls[] = {
            "https://id1.mobilepricecards.com",
            "https://id2.mobilepricecards.com",
            "https://id3.mobilepricecards.com",

    };
    private int listIndex = 0;
    private AddScreenViewModel addScreenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome_screen);
        welcomeValidationHelper = new WelcomeValidationHelper(this, mBinding);
        welcomeViewModel = new ViewModelProvider(this).get(WelcomeViewModel.class);
        addScreenViewModel = new ViewModelProvider(this).get(AddScreenViewModel.class);

        initView();
        initClick();
        firebaseConfiguration();
        defineKeyToUrlObserver();
        handleGeneralApiResponse();
    }


    private void firebaseConfiguration() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        SessionManager.get().setFCMToken(token);
                    }
                });
    }


    /**
     * Responsibility - initView method is used for initiate all object and perform some initial level task
     * Parameters - No parameter
     **/
    private void initView() {

        sessionManager = SessionManager.get();
        setNoTitleBar(this);


    }


    private void handleGeneralApiResponse() {
        LiveData<GlobalResponse<GeneralResponse>> liveData = addScreenViewModel.getGeneralResponseLiveData();
        if (!liveData.hasActiveObservers()) {
            liveData.observe(this, new Observer<GlobalResponse<GeneralResponse>>() {
                @Override
                public void onChanged(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {
                    handleGeneralResponse(generalResponseGlobalResponse);
                }
            });
        }
    }


    /**
     * Responsibility - handleGeneralResponse is an method check  response is correct or not if response is good  that redirect screen to splash else its show error message
     * Parameters - Its take GlobalResponse<GeneralResponse> generalResponseGlobalResponse that help to know url is correct or not
     **/
    private void handleGeneralResponse(GlobalResponse<GeneralResponse> generalResponseGlobalResponse) {
        showHideProgressDialog(false);
        if (generalResponseGlobalResponse != null) {
            if (generalResponseGlobalResponse.isApi_status()) {
                sessionManager.setBaseUrlChange(true);
                goToOnBoarding();

            } else {
                sessionManager.removeBaseUrl();
                ValidationHelper.showToast(WelcomeScreen.this, getString(R.string.enter_valid_url));

            }
        } else {
            sessionManager.removeBaseUrl();
            ValidationHelper.showToast(this, getString(R.string.enter_valid_url));
        }
    }

    /**
     * Responsibility - defineKeyToUrlObserver handles key to url response
     */
    private void defineKeyToUrlObserver() {
        LiveData<GlobalResponse<KeyToUrlResponse>> globalResponseLiveData = welcomeViewModel.getResponseLiveData();
        if (!globalResponseLiveData.hasActiveObservers()) {
            globalResponseLiveData.observe(this, new Observer<GlobalResponse<KeyToUrlResponse>>() {
                @Override
                public void onChanged(GlobalResponse<KeyToUrlResponse> keyToUrlResponseGlobalResponse) {
                    showHideProgressDialog(false);
                    if (keyToUrlResponseGlobalResponse != null) {
                        handleKeyToUrlResponse(keyToUrlResponseGlobalResponse);
                    } else {
                        checkLoadedKey();
                    }
                }
            });
        }
    }

    /**
     * Responsibility - handleKeyToUrlResponse method manipulate server
     *
     * @param result
     */
    private void handleKeyToUrlResponse(GlobalResponse<KeyToUrlResponse> result) {
        if (result != null) {
            if (result.getResult().isMatched_status()) {
                handleGeneralApi(result.getResult().getMatched_url() + Constraint.SLASH);
            } else {
                ValidationHelper.showToast(this, getResources().getString(R.string.mpc_key_not_correct));
            }
        } else {
            checkLoadedKey();
        }
    }

    private void handleGeneralApi(String s) {
        sessionManager.setBaseUrl(s);
        showHideProgressDialog(true);
        addScreenViewModel.setGeneralRequest(new HashMap<>());


    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean permissionAvailable = Utils.isTimeAutomatic(this);
        if (!permissionAvailable) {
            showAlertIfTimeIsNotCorrect();
        }
    }

    /**
     * Responsibility - initClick is an method that used for initiate clicks
     * Parameters - No parameter
     **/
    private void initClick() {
        mBinding.begin.setOnClickListener(this);
    }

    /**
     * Responsibility - onClick is an predefine method that calls when any click perform
     * Parameters - Its takes view that contains if from which we can know which item is clicked
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begin: {
                listIndex = 0;
                checkLoadedKey();
                break;
            }
        }
    }

    private void checkLoadedKey() {
        if (mBinding.keyName.getText().toString().contains(Constraint.HTTP)) {
            updateBaseUrl();

        } else {
            if (listIndex <= 2) {
                if (welcomeValidationHelper.isValid()) {
                    if (Utils.getNetworkState(this)) {
                        showHideProgressDialog(true);
                        welcomeViewModel.setRequestLiveData(createKeyToUrlRequest());
                    } else {
                        ValidationHelper.showToast(this, getString(R.string.no_internet_available));
                    }
                }
            } else if (listIndex == 3) {
                ValidationHelper.showToast(this, getString(R.string.technical_issue));
            }


        }
    }

    private HashMap<String, String> createKeyToUrlRequest() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(Constraint.customerID, mBinding.keyName.getText().toString());
        stringStringHashMap.put(Constraint.TOKEN, SessionManager.get().getDeviceToken());
        stringStringHashMap.put(Constraint.ID_BASE_URL, myBaseUrls[listIndex++]);
        return stringStringHashMap;
    }

    /**
     * Responsibility - goToOnBoarding method redirect screen to OnBaording activity
     * Parameters - No parameter
     **/
    private void goToOnBoarding() {
        Intent intent = new Intent(WelcomeScreen.this, OnBoarding.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constraint.EXIT_CAPITAL, true);
        startActivity(intent);
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
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    @Override
    protected void onStart() {
        if (Locale.getDefault().getLanguage().equals(Constraint.AR)) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mBinding.curveLayout.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_purple_rtl));
            } else {
                mBinding.curveLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_purple_rtl));
            }
        }
        super.onStart();

    }


    /**
     * show alert if timezone is not correct
     **/
    public void showAlertIfTimeIsNotCorrect() {
        DateTimePermissionDIalog dateTimePermissionDIalog = new DateTimePermissionDIalog();
        dateTimePermissionDIalog.setCancelable(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dateTimePermissionDIalog.show(ft, null);


    }


    /**
     * Responsibility - updateBaseUrl is an method that takes url from ui and call general api with same server url and if its return response then send it to  handleGeneralResponse
     * Parameters - No parameter
     **/
    private void updateBaseUrl() {
        try {

            String url = mBinding.keyName.getText().toString();
            if (url != null && !url.equals("")) {
                String urlLastChar = url.substring(url.length() - 1);
                if (urlLastChar.equals(Constraint.SLASH)) {
                    boolean b = Utils.isValidUrl(url);
                    if (b) {
                        if (Utils.getNetworkState(this)) {
                            sessionManager.setBaseUrl(url);
                            showHideProgressDialog(true);
                            addScreenViewModel.setGeneralRequest(new HashMap<>());
                        } else {
                            ValidationHelper.showToast(this, getString(R.string.no_internet_available));
                        }
                    } else {
                        ValidationHelper.showToast(this, getString(R.string.enter_valid_url));
                    }
                } else {
                    ValidationHelper.showToast(this, getString(R.string.url_must_end_with_slash));
                }
            } else {
                ValidationHelper.showToast(this, getString(R.string.baseurl_can_not_be_empty));
            }
        } catch (Exception e) {

        }
    }


}
