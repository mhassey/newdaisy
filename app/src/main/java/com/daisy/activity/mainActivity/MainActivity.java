package com.daisy.activity.mainActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.daisy.R;
import com.daisy.activity.apkUpdate.DownloadUpdateApk;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.configSettings.ConfigSettings;
import com.daisy.activity.deleteCard.DeleteCardViewModel;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.onBoarding.slider.getCard.GetCardViewModel;
import com.daisy.activity.onBoarding.slider.getCard.vo.GetCardResponse;
import com.daisy.activity.updateProduct.UpdateProductViewModel;
import com.daisy.checkCardAvailability.CheckCardAvailability;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.ActivityMainBinding;
import com.daisy.interfaces.CallBack;
import com.daisy.pojo.response.ApkDetails;
import com.daisy.pojo.response.DeleteCardResponse;
import com.daisy.pojo.response.Download;
import com.daisy.pojo.response.DownloadFail;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.pojo.response.Inversion;
import com.daisy.pojo.response.PriceCard;
import com.daisy.pojo.response.Pricing;
import com.daisy.pojo.response.Promotion;
import com.daisy.pojo.response.Promotions;
import com.daisy.pojo.response.Sanitised;
import com.daisy.pojo.response.UpdateCards;
import com.daisy.security.Admin;
import com.daisy.utils.CheckForSDCard;
import com.daisy.utils.Constraint;
import com.daisy.utils.DownloadFile;
import com.daisy.utils.OnSwipeTouchListener;
import com.daisy.utils.PermissionManager;
import com.daisy.utils.SanitisedSingletonObject;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Purpose -  MainActivity is an activity that show cards and promotions and pricing and handling all things related to price cards
 * Responsibility - Its loads cards,promotion send pricing to js and its also handles logs related price card and promotions
 **/
public class MainActivity extends BaseActivity implements CallBack, View.OnClickListener {
    private ActivityMainBinding mBinding;
    private SessionManager sessionManager;
    private MainActivityViewModel mViewModel;
    private Context context;
    private WebViewClient yourWebClient;
    private CountDownTimer sanitisedTimer;
    private UpdateProductViewModel updateProductViewModel;
    private GetCardViewModel getCardViewModel;


    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAdminPermission();
        initView();
        initService();
        setOnClickListener();
    }

    /**
     * Initial data setup
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        mBinding = DataBindingUtil.setContentView(this, (R.layout.activity_main));
        updateProductViewModel = new ViewModelProvider(this).get(UpdateProductViewModel.class);
        getCardViewModel = new ViewModelProvider(this).get(GetCardViewModel.class);

        setNoTitleBar(this);
        context = this;
        handleScreenRotation();
        sessionWork();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        permissionAsking();
        windowWork();
        loadURL();
        intentWork();
        sanitisedWork();
    }


    /**
     * Check for permission
     */
    private void permissionAsking() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Do something for lollipop and above versions
            PermissionManager.checkPermissionOnly(MainActivity.this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);
        } else {
            PermissionManager.checkPermissionOnly(MainActivity.this, Constraint.STORAGE_PERMISSION_WITHOUT_SENSOR, Constraint.RESPONSE_CODE);
            // do something for phones running an SDK before lollipop
        }
    }

    /**
     * Add sanitised cown down
     */
    private void setCownDownForSenitised() {
        SanitisedSingletonObject sanitisedSingletonObject = SanitisedSingletonObject.getInstance();
        CountDownTimer countDownTimer = sanitisedSingletonObject.getSanitisedCoundownTimer();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer.start();

        } else {
            countDownTimer = new CountDownTimer(Constraint.THIRTY_THOUSAND, Constraint.THOUSAND) {

                public void onTick(long millisUntilFinished) {


                }

                public void onFinish() {
                    try {
                        sessionManager.setSanitized(true);
                        mBinding.sanitisedHeader.setVisibility(View.VISIBLE);
                        Glide.with(MainActivity.this)
                                .load(R.drawable.ani)
                                .into(mBinding.senaitised);
                    } catch (Exception e) {

                    }
                }

            };
            sanitisedSingletonObject.setCOunter(countDownTimer);
            countDownTimer.start();
        }
    }


    /**
     * Handle session work
     */
    private void sessionWork() {
        sessionManager = SessionManager.get();
        sessionManager.dialogShow(Constraint.FALSE);
        sessionManager.onBoarding(Constraint.TRUE);
    }

    /**
     * Ser screen always open
     */
    private void windowWork() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }


    /**
     * handle intent work
     */
    private void intentWork() {
        String available = getIntent().getStringExtra(Constraint.PROMOTION);
        if (available != null && !available.equals("")) {
            updatePromotion(new Promotion());
        }
    }


    private void sanitisedWork() {
        if (sessionManager.getSanitized()) {
            mBinding.sanitisedHeader.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(R.drawable.ani);

        } else {
            mBinding.sanitisedHeader.setVisibility(View.GONE);
        }
    }

    /**
     * Start Service permanently
     */
    private void initService() {
        long time1 = TimeUnit.SECONDS.toMillis(Constraint.ONE);
        Utils.constructJobForBackground(time1, getApplicationContext());
        //  startService(new Intent(MainActivity.this, CaptureImageService.class));
    }

    /**
     * Button clicks initializing
     */
    private void setOnClickListener() {
        mBinding.settingHeader.setOnClickListener(this);
        mBinding.setting.setOnClickListener(this);
        mBinding.offLineIcon.setOnClickListener(this);
        mBinding.invert.setOnClickListener(this::onClick);
        mBinding.swipeclick.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                settingHeader();
            }
        });

    }


    /**
     * On resume have a functionality for making this screen landscape or portrait mode
     */
    @Override
    protected void onResume() {
        super.onResume();
        handleResumeWork();
    }

    //TODO Handle resume work
    private void handleResumeWork() {
        if (sessionManager.getLocation() != null && !sessionManager.getLocation().equals("")) {
            if (!sessionManager.getOrientation().equals(getString(R.string.defaultt))) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }
        if (sessionManager.getSanitized()) {
            if (sessionManager.getComeConfig()) {
                sessionManager.setComeFromConfig(false);
                setCownDownForSenitised();
            } else {
                Glide.with(this)
                        .load(R.drawable.ani)
                        .into(mBinding.senaitised);
            }
        } else {
            mBinding.sanitisedHeader.setVisibility(View.GONE);
        }
    }


    /**
     * Handle all downloaded data
     */
    private void getDownloadData() {
        try {

            if (CheckForSDCard.isSDCardPresent()) {

                List<Promotion> promotions = sessionManager.getPromotion();
                List<Download> downloads = new ArrayList<>();
                if (checkPermission()) {
                    final String url = Utils.getPath();
                    if (url != null) {
                        if (sessionManager.getPriceCard() != null) {
                            if (sessionManager.getPriceCard().getFileName1() != null)
                                downloadFiles(sessionManager.getPriceCard().getFileName1(), promotions, downloads);
                            else if (sessionManager.getPriceCard().getFileName() != null)
                                downloadFiles(sessionManager.getPriceCard().getFileName(), promotions, downloads);
                            else
                                DownloadFail(new DownloadFail());

                        } else {
                            downloadFiles(url, promotions, downloads);

                        }
                    } else {
                        editorToolOpen();
                    }
                }
            } else {
                ValidationHelper.showToast(this, getString(R.string.storage_not_available));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create download file and send to download manager
     */
    private void downloadFiles(String url, List<Promotion> promotions, List<Download> downloads) {
        Download download = new Download();
        download.setPath(url);
        if (sessionManager.getPriceCard() != null)
            download.setPath(sessionManager.getPriceCard().getFileName());
        download.setType("");
        JSONArray listOfPromo = sessionManager.getPromotions();
        downloads.add(download);
        if (promotions != null) {
            for (Promotion promotion : promotions) {
                if (listOfPromo != null) {
                    for (int promowork = Constraint.ZERO; promowork < listOfPromo.length(); promowork++) {
                        try {
                            JSONObject jsonObject = (JSONObject) listOfPromo.get(promowork);
                            if (promotion.getIdpromotion().equals(jsonObject.getString(Constraint.PROMOTION_ID))) {
                                listOfPromo.remove(promowork);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Download downloadPromotion = new Download();
                if (promotion.getFileName1() != null) {
                    downloadPromotion.setPath(promotion.getFileName1());

                } else {
                    downloadPromotion.setPath(promotion.getFileName());

                }
                ;
                downloadPromotion.setPath1(promotion.getFileName());

                downloadPromotion.setDateCreated(promotion.getDateCreated());
                downloadPromotion.setDateExpires(promotion.getDateExpires());
                downloadPromotion.setType(Constraint.PROMOTION);
                downloadPromotion.setPromotionId(promotion.getIdpromotion());
                downloads.add(downloadPromotion);

            }
        }
        if (listOfPromo != null)
            sessionManager.setPromotions(listOfPromo);
        new DownloadFile(MainActivity.this, MainActivity.this, downloads).execute(url);
    }


    /**
     * Handle orientation to 180 degree
     */
    private void handleScreenRotation() {


    }


    /**
     * Change system ui to full screen when any change perform in activity
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }


    /**
     * Handle full screen mode
     */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ViewGroup.LayoutParams params = mBinding.webView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mBinding.webView.requestLayout();

    }


    /**
     * Permission grand handler
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > Constraint.ZERO) {
            if (grantResults[Constraint.ZERO] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = shouldShowRequestPermissionRationale(permissions[Constraint.ZERO]);
                if (!showRationale) {
                } else {
                    boolean b;

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Do something for lollipop and above versions
                        b = PermissionManager.checkPermissionOnly(MainActivity.this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);
                    } else {
                        b = PermissionManager.checkPermissionOnly(MainActivity.this, Constraint.STORAGE_PERMISSION_WITHOUT_SENSOR, Constraint.RESPONSE_CODE);
                        // do something for phones running an SDK before lollipop
                    }

                }
            } else {
                if (grantResults.length > Constraint.ZERO
                        && grantResults[Constraint.ZERO] == PackageManager.PERMISSION_GRANTED) {
                    getDownloadData();

                }
            }
        }
        return;

    }

    /**
     * check permission
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * reload every thing
     */
    @Override
    public void callBack(String data) {

        Intent selfIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(selfIntent);
        finish();
        overridePendingTransition(Constraint.ZERO, Constraint.ZERO);

    }

    /**
     * Time to take apk update
     */
    @Override
    public void callBackApkUpdate(String data) {
        sessionManager.uninstallShow(true);
        installApk();
        sessionManager.deleteApkVersion();
        this.finishAffinity();

    }

    /**
     * Install apk
     */
    private void installApk() {
        try {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
                String PATH = Constraint.DAISY + Constraint.SLASH + Constraint.DAISYAPK;
                File file = new File(getExternalFilesDir(""), PATH);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Constraint.TWENTY_FOUR) {
                    Uri downloaded_apk = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + Constraint.PROVIDER, file);
                    intent.setDataAndType(downloaded_apk, Constraint.ANDROID_PACKAGE_ARCHIVE);
                    List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        getApplicationContext().grantUriPermission(getApplicationContext().getApplicationContext().getPackageName() + Constraint.PROVIDER, downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent.setDataAndType(Uri.fromFile(file), Constraint.ANDROID_PACKAGE_ARCHIVE);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                }
                startActivity(intent);
            } else {
                String PATH = Environment.getExternalStorageDirectory() + Constraint.SLASH + Constraint.DAISY + Constraint.SLASH + Constraint.DAISYAPK;
                File file = new File(PATH);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Constraint.TWENTY_FOUR) {
                    Uri downloaded_apk = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + Constraint.PROVIDER, file);
                    intent.setDataAndType(downloaded_apk, Constraint.ANDROID_PACKAGE_ARCHIVE);
                    List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        getApplicationContext().grantUriPermission(getApplicationContext().getApplicationContext().getPackageName() + Constraint.PROVIDER, downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent.setDataAndType(Uri.fromFile(file), Constraint.ANDROID_PACKAGE_ARCHIVE);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                }
                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("JavascriptInterface")
    private void loadURL() {
        if (sessionManager.getLocation() != null && !sessionManager.getLocation().equals("")) {
            mBinding.webView.addJavascriptInterface(new WebAppInterface(this), "Android"); // To call methods in Android from using js in the html, AndroidInterface.showToast, AndroidInterface.getAndroidVersion etc

            mBinding.webView.setWebChromeClient(new WebClient());

            setWebViewClient();
            mBinding.webView.getSettings().setAllowFileAccessFromFileURLs(Constraint.TRUE);
            mBinding.webView.getSettings().setAllowFileAccess(Constraint.TRUE);
            mBinding.webView.setSoundEffectsEnabled(Constraint.TRUE);
            mBinding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(Constraint.TRUE);
            mBinding.webView.getSettings().setAllowUniversalAccessFromFileURLs(Constraint.TRUE);
            mBinding.webView.getSettings().setAppCacheEnabled(Constraint.TRUE);
            mBinding.webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            mBinding.webView.getSettings().setAllowContentAccess(Constraint.TRUE);
            mBinding.webView.getSettings().setDomStorageEnabled(Constraint.TRUE);
            mBinding.webView.getSettings().setJavaScriptEnabled(Constraint.TRUE); // enable javascript
            mBinding.webView.getSettings().setBuiltInZoomControls(Constraint.TRUE);
            mBinding.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            mBinding.webView.getSettings().setLoadWithOverviewMode(Constraint.TRUE);
            mBinding.webView.getSettings().setUseWideViewPort(Constraint.TRUE);

            mBinding.webView.getSettings().setBuiltInZoomControls(Constraint.TRUE);
            mBinding.webView.getSettings().setDisplayZoomControls(Constraint.FALSE);

            mBinding.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            mBinding.webView.setScrollbarFadingEnabled(Constraint.FALSE);
            //mBinding.webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
            mBinding.webView.getSettings().setPluginState(WebSettings.PluginState.ON);

            mBinding.webView.getSettings().setMediaPlaybackRequiresUserGesture(Constraint.FALSE);

            if (Build.VERSION.SDK_INT >= Constraint.TWENTY_ONE) {
                mBinding.webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                CookieManager.getInstance().setAcceptThirdPartyCookies(mBinding.webView, Constraint.TRUE);
            }
            // mBinding.webView.getSettings().setUserAgentString(Constraint.GIVEN_BROWSER);
            String val = sessionManager.getLocation();
            boolean isDelete = sessionManager.getCardDeleted();
            File f = new File(val);
            File file[] = f.listFiles();
            if (file != null) {
                for (File file1 : file) {
                    if (file1.isDirectory() && !file1.getAbsolutePath().contains(Constraint.MACOS)) {
                        String fileName;
                        if (sessionManager.getOrientation().equals(Constraint.PORTRAIT)) {
                            fileName = Constraint.VERTICAL + Constraint.HTML;
                        } else {
                            fileName = Constraint.HORIZONTAL + Constraint.HTML;

                        }
                        File mainFile = new File(file1.getAbsoluteFile() + Constraint.SLASH + fileName);
                        File mainFileMain = new File(file1.getAbsoluteFile() + Constraint.SLASH + Constraint.FILE_NAME);

                        if (mainFile.exists()) {
                            mBinding.webView.loadUrl(Constraint.FILE + file1.getAbsoluteFile() + Constraint.SLASH + fileName);
                            sessionManager.setMainFilePath(file1.getAbsoluteFile().toString());
                            if (!isDelete)
                                deleteCard();
                        } else {

                            File file2 = new File(sessionManager.getLocation() + Constraint.SLASH + fileName);
                            File file3 = new File(sessionManager.getLocation() + Constraint.SLASH + file1.getName() + Constraint.SLASH + fileName);

                            if (file2.exists()) {
                                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + fileName);
                                sessionManager.setMainFilePath(sessionManager.getLocation());

                                if (!isDelete)
                                    deleteCard();
                            } else if (file3.exists()) {
                                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + file1.getName() + Constraint.SLASH + fileName);
                                sessionManager.setMainFilePath(sessionManager.getLocation() + Constraint.SLASH + fileName);

                                if (!isDelete)
                                    deleteCard();
                            } else if (mainFileMain.exists()) {
                                mBinding.webView.loadUrl(Constraint.FILE + file1.getAbsoluteFile() + Constraint.SLASH + Constraint.FILE_NAME);
                                sessionManager.setMainFilePath(file1.getAbsoluteFile().toString());
                                if (!isDelete)
                                    deleteCard();
                            } else {

                                File file2Main = new File(sessionManager.getLocation() + Constraint.SLASH + Constraint.FILE_NAME);
                                File file3Main = new File(sessionManager.getLocation() + Constraint.SLASH + file1.getName() + Constraint.SLASH + file1.getName() + Constraint.HTML);

                                if (file2Main.exists()) {
                                    mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + Constraint.FILE_NAME);
                                    sessionManager.setMainFilePath(sessionManager.getLocation());

                                    if (!isDelete)
                                        deleteCard();
                                } else if (file3Main.exists()) {
                                    mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + file1.getName() + Constraint.SLASH + file1.getName() + Constraint.HTML);
                                    sessionManager.setMainFilePath(sessionManager.getLocation() + Constraint.SLASH + file1.getName());

                                    if (!isDelete)
                                        deleteCard();
                                } else {
                                    sessionManager.deleteLocation();
                                    getDownloadData();
                                }
                            }


                        }


                        return;
                    }

                }

            } else {

                getDownloadData();
            }
        } else {

            getDownloadData();
        }


    }

    /**
     * delete card
     */
    private void deleteCard() {
        if (!sessionManager.getCardDeleted()) {
            DeleteCardViewModel deleteCardViewModel = new ViewModelProvider(this).get(DeleteCardViewModel.class);
            deleteCardViewModel.setMutableLiveData(getDeleteCardRequest());
            LiveData<GlobalResponse<DeleteCardResponse>> liveData = deleteCardViewModel.getLiveData();
            liveData.observe(this, new Observer<GlobalResponse<DeleteCardResponse>>() {
                @Override
                public void onChanged(GlobalResponse<DeleteCardResponse> deleteCardResponseGlobalResponse) {
                    sessionManager.setCardDeleted(true);
                    handleDeleteResponse(deleteCardResponseGlobalResponse);
                }
            });
        }
    }

    /**
     * create card delete request
     */
    private HashMap<String, String> getDeleteCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }

    /**
     * handle delete response
     */
    private void handleDeleteResponse(GlobalResponse<DeleteCardResponse> deleteCardResponseGlobalResponse) {
        if (deleteCardResponseGlobalResponse.isApi_status()) {

        }
    }


    /**
     * set webview client to webview
     */
    private void setWebViewClient() {


        yourWebClient = new WebViewClient() {

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                JSONObject jsonArray = pricingUpdateStart();
                if (jsonArray != null) {
                    if (jsonArray.length() > 0) {
                        mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");
                    }
                }
                super.onFormResubmission(view, dontResend, resend);


            }

            @Override
            public void onLoadResource(WebView view, String url) {
                 JSONObject jsonArray = pricingUpdateStart();
                if (jsonArray != null) {
                    if (jsonArray.length() > 0) {

                        mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");
                        mViewModel.setExceptionInHtml(false);

                    }
                }
                super.onLoadResource(view, url);

            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                 JSONObject jsonArray = pricingUpdateStart();
                if (jsonArray != null) {
                    if (jsonArray.length() > 0) {

                        mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");
                        mViewModel.setExceptionInHtml(false);

                    }
                }
                super.onPageCommitVisible(view, url);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                JSONObject jsonArray = pricingUpdateStart();
                if (jsonArray != null) {
                    if (jsonArray.length() > 0) {

                        mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");
                        mViewModel.setExceptionInHtml(false);

                    }
                }
                super.onPageStarted(view, url, favicon);



            }


            @Override
            public void onPageFinished(WebView view, final String url) {
                try {
                    JSONObject jsonArray = pricingUpdateStart();
                    if (jsonArray != null) {
                        if (jsonArray.length() > 0) {
                            mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");
                            mViewModel.setExceptionInHtml(false);

                        }
                    }
                    promotionSettings();
                    boolean b = Utils.getInvertedTime();
                    if (b) {
                        mBinding.webView.loadUrl("javascript:MobilePriceCard.setNightmode(true)");
                    } else {
                        mBinding.webView.loadUrl("javascript:MobilePriceCard.setNightmode(false)");
                    }


//                    if (!SessionManager.get().isNewApk()) {
//                        handlePriceCardGettingHandler();
//                           SessionManager.get().setNewApk(true);
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        };

        mBinding.webView.setWebViewClient(yourWebClient);

    }

    /**
     * change pricing in webview
     */
    private JSONObject pricingUpdateStart() {

        JSONObject jsonObject = new JSONObject();
        try {
            List<Pricing> pricing = sessionManager.getPricing();
            Pricing pricing1 = null;
            if (pricing != null && !pricing.isEmpty()) {
                OUTER_LOOP:
                for (int i = (pricing.size() - Constraint.ONE); i >= Constraint.ZERO; i--) {
                    try {
                        if (sessionManager.getPricingPlainId().equals(pricing.get(i).getPricingPlanID())) {
                            SimpleDateFormat sdf = new SimpleDateFormat(Constraint.YYY_MM_DD);
                            Date futureDate;
                            if (pricing.get(i).getTimeExpires() != null) {
                                futureDate = sdf.parse(pricing.get(i).getDateExpires() + " " + pricing.get(i).getTimeExpires());

                            } else {
                                futureDate = sdf.parse(pricing.get(i).getDateExpires() + " " + Constraint.DEFAULT_HOURS_MINUTES);

                            }
                            Date dateEffective;
                            if (pricing.get(i).getTimeEffective() != null) {
                                dateEffective = sdf.parse(pricing.get(i).getDateEffective() + " " + pricing.get(i).getTimeEffective());

                            } else {
                                dateEffective = sdf.parse(pricing.get(i).getDateEffective() + " " + Constraint.DEFAULT_HOURS_MINUTES);

                            }
                            Date todayDate = new Date();

                            if (!dateEffective.after(todayDate)) {
                                if (futureDate.after(todayDate)) {
                                    pricing1 = pricing.get(i);
                                    break OUTER_LOOP;
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (pricing1 == null) {
                    for (int i = Constraint.ZERO; i < pricing.size(); i++) {
                        if (pricing.get(i).getIsDefault() != null && pricing.get(i).getIsDefault().equals(Constraint.ONE_STRING)) {
                            pricing1 = pricing.get(i);
                        }

                    }
                }
                if (pricing1 != null) {
                    jsonObject.put(Constraint.ID_PRODUCT_FLUID, pricing1.getIdproductFluid());
                    jsonObject.put(Constraint.ID_PRODUCT_STATIC, pricing1.getIdproductStatic());
                    jsonObject.put(Constraint.DATE_EFFECTIVE, pricing1.getDateEffective());
                    jsonObject.put(Constraint.TIME_EFFECTIVE, pricing1.getTimeEffective());
                    jsonObject.put(Constraint.PFV1, pricing1.getPfv1());
                    jsonObject.put(Constraint.PFV2, pricing1.getPfv2());
                    jsonObject.put(Constraint.PFV3, pricing1.getPfv3());
                    jsonObject.put(Constraint.PFV4, pricing1.getPfv4());
                    jsonObject.put(Constraint.PFV5, pricing1.getPfv5());
                    jsonObject.put(Constraint.PFV6, pricing1.getPfv6());
                    jsonObject.put(Constraint.PFV7, pricing1.getPfv7());
                    jsonObject.put(Constraint.PFV8, pricing1.getPfv8());
                    jsonObject.put(Constraint.PFV9, pricing1.getPfv9());
                    jsonObject.put(Constraint.PFV10, pricing1.getPfv10());
                    jsonObject.put(Constraint.PFV11, pricing1.getPfv11());
                    jsonObject.put(Constraint.PFV12, pricing1.getPfv12());
                    jsonObject.put(Constraint.PFV13, pricing1.getPfv13());
                    jsonObject.put(Constraint.PFV14, pricing1.getPfv14());
                    jsonObject.put(Constraint.PFV15, pricing1.getPfv15());
                    jsonObject.put(Constraint.PFV16, pricing1.getPfv16());
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return jsonObject;
    }

    /**
     * load pricing in webview
     */
    private void pricingUpdate() {
        JSONObject jsonArray = pricingUpdateStart();

        if (jsonArray.length() > 0)
            mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");

//        mBinding.webView.loadUrl("javascript:handlePriceDynamically(" + jsonArray + ")");
    }


    private void handlePriceCardGettingHandler() {
        showHideProgressDialog(true);
        updateProductViewModel.setMutableLiveData(getUpdateScreenRequest());
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

    /**
     * Responsibility -  getUpdateScreenRequest method create update screen request
     * Parameters - No parameter
     **/
    private HashMap<String, String> getUpdateScreenRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.ID_PRODUCT_STATIC, SessionManager.get().getPriceCard().getIdproductStatic());
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }


    /**
     * Responsibility - handleScreenAddResponse is an method that check update screen response is ok if yes then call getCardData method
     * Parameters - Its takes GlobalResponse object as an parameter
     **/
    private void handleScreenAddResponse(GlobalResponse screenAddResponseGlobalResponse) {
        if (screenAddResponseGlobalResponse.isApi_status()) {
            getCardData();
        } else {
            ValidationHelper.showToast(context, screenAddResponseGlobalResponse.getMessage());
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
                            DBCaller.storeLogInDatabase(context, getCardResponseGlobalResponse.getResult().getPricecard().getPriceCardName() + getString(R.string.data_store), "", "", Constraint.APPLICATION_LOGS);
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
     * Responsibility -  getCardRequest method create card request
     * Parameters - No parameter
     **/
    private HashMap<String, String> getCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.SCREEN_ID, sessionManager.getScreenId() + "");
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }


    /**
     * Responsibility - handleCardGetResponse method handle response provided by getCardData method if response is ok then set new value of price card promotion and pricing in session and call redirectToMainHandler method
     * Parameters - No parameter
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


    private void redirectToMainHandler(GlobalResponse<GetCardResponse> response) throws IOException {
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
     * create promotion that will load in web view
     */
    private void promotionSettings() {
        ArrayList<String> pro = new ArrayList<>();
        try {
            JSONArray promotionsArray = sessionManager.getPromotions();
            if (promotionsArray != null) {
                for (int i = Constraint.ZERO; i < promotionsArray.length(); i++) {
                    JSONObject promtotionJsonObect = promotionsArray.getJSONObject(i);
                    String value = promtotionJsonObect.getString(Constraint.PROMOTION);
                    if (value.contains(Constraint.PROMOTION)) {
                        File file = new File(value + Constraint.FILE_NAME);
                        File check = new File(value);
                        File mainCheck = new File(value + check.getName() + Constraint.EXTENTION);
                        if (promtotionJsonObect.get(Constraint.DATE_EXPIRES) != null && promtotionJsonObect.get(Constraint.DATE_CREATE) != null && !promtotionJsonObect.get("dateExpires").equals("0000-00-00 00:00:00")) {
                            String dateExpire = promtotionJsonObect.get(Constraint.DATE_EXPIRES).toString();
                            String dateCreated = promtotionJsonObect.get(Constraint.DATE_CREATE).toString();
                            if (file.exists()) {


                                SimpleDateFormat sdf = new SimpleDateFormat(Constraint.YYY_MM_DD);
                                Date futureDate;
                                futureDate = sdf.parse(dateExpire);
                                Date dateEffective;
                                dateEffective = sdf.parse(dateCreated);
                                Date todayDate = new Date();
                                if (!dateEffective.after(todayDate)) {
                                    if (futureDate.after(todayDate)) {
                                        pro.add(Constraint.PROMOTION + Constraint.SLASH + check.getName() + Constraint.SLASH + Constraint.FILE_NAME);
                                    }
                                }
                            } else if (mainCheck.exists()) {
                                SimpleDateFormat sdf = new SimpleDateFormat(Constraint.YYY_MM_DD);
                                Date futureDate;
                                futureDate = sdf.parse(dateExpire);
                                Date dateEffective;
                                dateEffective = sdf.parse(dateCreated);
                                Date todayDate = new Date();
                                if (!dateEffective.after(todayDate)) {
                                    if (futureDate.after(todayDate)) {
                                        pro.add(Constraint.PROMOTION + Constraint.SLASH + check.getName() + Constraint.SLASH + check.getName() + Constraint.EXTENTION);
                                    }
                                }

                            }
                        } else {
                            if (file.exists()) {
                                pro.add(Constraint.PROMOTION + Constraint.SLASH + check.getName() + Constraint.SLASH + Constraint.FILE_NAME);
                            } else if (mainCheck.exists()) {
                                pro.add(Constraint.PROMOTION + Constraint.SLASH + check.getName() + Constraint.SLASH + check.getName() + Constraint.EXTENTION);
                            }
                        }


                    }
                }
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        if (pro.size() > 0) {
            mBinding.webView.loadUrl("javascript:MobilePriceCard.setAdBundle([" + Utils.stringify(pro) + "])");
        } else {
            mBinding.webView.loadUrl("javascript:MobilePriceCard.setAdBundle()");

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Sanitised(Sanitised sanitised) {
        if (sessionManager.getSanitized()) {
            sessionManager.setSanitized(false);
            mBinding.sanitisedHeader.setVisibility(View.GONE);


        }

    }


    /**
     * Handle Clicks listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settingHeader: {
                settingHeader();
                break;
            }
            case R.id.invert: {

                mBinding.webView.loadUrl("javascript:MobilePriceCard.setNightmode(true)");
                break;

            }
            case R.id.setting: {
                settingClick();
                break;
            }
            case R.id.offLineIcon: {

                goToWifi();
                break;
            }
        }
    }

    /**
     * Ge to wifi screen
     */
    private void goToWifi() {
        sessionManager.setPasswordCorrect(true);
        Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        if (Utils.getDeviceName().contains(getString(R.string.onePlus))) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        }
        this.startActivityForResult(intent, Constraint.NINE_THOUSANT_NINE_HUNDRED);
    }

    /**
     * Setting icon click
     */
    private void settingClick() {
        DBCaller.storeLogInDatabase(context, Constraint.SETTINGS, Constraint.SETTINGS_DESCRIPTION, "", Constraint.APPLICATION_LOGS);
        openConfigSettings();
    }


    /**
     * setting hader visibility
     */
    private void settingHeader() {
        if (mViewModel.isSettingVisible()) {
            mViewModel.setSettingVisible(Constraint.FALSE);
            mBinding.setting.setVisibility(View.GONE);
        } else {
            mViewModel.setSettingVisible(Constraint.TRUE);
            mBinding.setting.setVisibility(View.VISIBLE);
            hideSettingsIcon();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    /**
     * open editor tool activity
     */
    private void editorToolOpen() {
        Intent intent = new Intent(MainActivity.this, EditorTool.class);
        startActivity(intent);
        finish();
    }


    /**
     * hide setting icon
     */
    void hideSettingsIcon() {
        try {
            Runnable mRunnable;
            Handler mHandler = new Handler();
            mRunnable = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if (mBinding.setting.getVisibility() == View.VISIBLE) {
                            mBinding.setting.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
                        }
                    } catch (Exception e) {

                    }
                }

            };
            mHandler.postDelayed(mRunnable, Constraint.TWENTY * Constraint.THOUSAND);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mBinding.webView.canGoBack()) {
                        mBinding.webView.goBack();
                    } else {
                        onBackToHome();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        onBackToHome();
    }

    public void onBackToHome() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void InternetAvailability(InternetResponse internetResponse) {
        if (internetResponse.isAvailable()) {
            mBinding.offlineLayout.setVisibility(View.VISIBLE);
        } else {
            mBinding.offlineLayout.setVisibility(View.GONE);

        }
    }

    /**
     * if download fail then show alert
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void DownloadFail(DownloadFail internetResponse) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.file_has_issue));
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            if (!alertDialog.isShowing())
                alertDialog.show();
            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, 400); //Controlling width and height.
        } catch (Exception e) {

        }
    }

    /**
     * change pricing
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadUrl(Pricing pricing) {

        pricingUpdate();
        deleteCard();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadUrl(PriceCard pricing) {
        loadURL();
        deleteCard();
    }

    /**
     * invert price card
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void inverted(Inversion inversion) {
        if (inversion.isInvert()) {
            mBinding.webView.loadUrl("javascript:MobilePriceCard.setNightmode(true)");


        } else {
            mBinding.webView.loadUrl("javascript:MobilePriceCard.setNightmode(false)");

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPromotion(Promotions promotion) {
        promotionSettings();
    }

    /**
     * update apk from background
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateApk(ApkDetails apk) {
        if (!sessionManager.getupdateDialog()) {
            sessionManager.dialogShow(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.update_available));
            builder.setPositiveButton(getString(R.string.proceed), updatePerform(apk));
            builder.setNegativeButton(getString(R.string.dismiss), dismissUpdate());
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

    private DialogInterface.OnClickListener dismissUpdate() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sessionManager.dialogShow(false);
                sessionManager.setUpdateNotShow(true);
                setCownDown();
            }
        };
    }

    public void setCownDown() {
        new CountDownTimer(Constraint.SIXTY * Constraint.TEN * Constraint.THOUSAND, Constraint.THOUSAND) {

            public void onTick(long millisUntilFinished) {


            }

            public void onFinish() {
                sessionManager.setUpdateNotShow(Constraint.FALSE);
            }
        }.start();
    }


    /**
     * perform apk update
     */
    private DialogInterface.OnClickListener updatePerform(ApkDetails apk) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sessionManager.setVersionDetails(null);
                String link = apk.getAndroid().getLink();
                sessionManager.dialogShow(Constraint.FALSE);
                new DownloadUpdateApk(MainActivity.this, MainActivity.this).execute(link);

            }
        };
    }

    /**
     * update promotion
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePromotion(Promotion promotionss) {
        try {

            // Utils.deletePromotion();
            // sessionManager.deletePromotions();
            List<Promotion> promotions = sessionManager.getPromotion();
            JSONArray listOfPromo = sessionManager.getPromotions();
            List<Download> downloads = new ArrayList<>();
            if (checkPermission()) {
                for (Promotion promotion : promotions) {
                    if (listOfPromo != null) {
                        for (int promowork = 0; promowork < listOfPromo.length(); promowork++) {
                            try {
                                JSONObject jsonObject = (JSONObject) listOfPromo.get(promowork);
                                if (promotion.getIdpromotion().equals(jsonObject.getString(Constraint.PROMOTION_ID))) {
                                    listOfPromo.remove(promowork);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Download downloadPromotion = new Download();
                    if (promotion.getFileName1() != null)
                        downloadPromotion.setPath(promotion.getFileName1());
                    else
                        downloadPromotion.setPath(promotion.getFileName());

                    downloadPromotion.setPath1(promotion.getFileName());
                    downloadPromotion.setDateExpires(promotion.getDateExpires());
                    downloadPromotion.setDateCreated(promotion.getDateCreated());
                    downloadPromotion.setPromotionId(promotion.getIdpromotion());
                    downloadPromotion.setType(Constraint.PROMOTION);
                    downloads.add(downloadPromotion);
                }
                if (listOfPromo != null)
                    sessionManager.setPromotions(listOfPromo);

                new DownloadFile(MainActivity.this, MainActivity.this, downloads).execute();
                sessionManager.setCardDeleted(Constraint.FALSE);
            }
        } catch (Exception e) {

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCards(UpdateCards updateCards) {
        getDownloadData();
    }


    public class WebClient extends WebChromeClient {


        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {


            if (consoleMessage.message().contains(Constraint.MOBILE_PRICE_CARD_NOT_DEFINE)) {
            } else if (consoleMessage.message().contains(Constraint.PRICING_NOT_DEFINE)) {
                pricingUpdate();
            }

            return false;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, final JsPromptResult result) {
            return true;
        }


    }


    public class WebAppInterface {
        Context mContext;

        // Instantiate the interface and set the context
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void logEvent(String cmd, String msg) {
            Log.e("Cjeclomg", cmd + "--" + msg);
            if (cmd.equals(Constraint.adFrameUrl)) {

                maintainPromotionShowWithUrl(msg);
            } else if (msg.contains(Constraint.price)) {

                storePriceCardIfFaceDetected(msg);
            } else if (cmd.equals(Constraint.click)) {

                storeClickOnPromotionOrPriceCard(msg);
            }

        }

        @JavascriptInterface
        public void heartbeat(String msg) {
            // DBCaller.storeLogInDatabase(context,msg,msg,"",Constraint.PROMOTION);
        }


        @JavascriptInterface
        public void callFromJS() {
            launchApp();
        }

        @JavascriptInterface
        public void callFromJS(String event) {
        }
    }

    private void storePriceCardIfFaceDetected(String msg) {
        if (sessionManager.getUserFaceDetectionEnable()) {
            DBCaller.storeLogInDatabase(getApplicationContext(), Constraint.USER_SEEN_PRICECARD__, "", "", Constraint.PRICECARD_LOG);
        }

    }

    private void storeClickOnPromotionOrPriceCard(String msg) {
        try {
            if (sessionManager != null) {
                sessionManager = SessionManager.get();
            }

            if (msg.contains(Constraint.PROMOTION)) {
                String promotionPath = msg.split("\\?")[0];
                try {

                    if (promotionPath.contains("file://")) {
                        String data[] = promotionPath.split(Constraint.PROMOTION);
                        if (data.length > 0) {
                            promotionPath = Constraint.PROMOTION + "" + data[1];
                        }
                    }
                } catch (Exception e) {

                }
                int id = Utils.searchPromotionUsingPath(promotionPath);
                if (id != Constraint.ZERO)
                    DBCaller.storeLogInDatabase(context, Constraint.Impression, id + "", msg, Constraint.PROMOTION);

            } else {
                DBCaller.storeLogInDatabase(context, Constraint.Impression, Constraint.Impression, msg, Constraint.PRICECARD_LOG);
            }
        } catch (Exception e) {

        }

    }

    private void maintainPromotionShowWithUrl(String msg) {
        try {
            String promotionPath = msg.split("\\?")[0];
            int id = Utils.searchPromotionUsingPath(promotionPath);

            if (id != Constraint.ZERO) {
                if (sessionManager == null) {
                    sessionManager = SessionManager.get();
                }
                if (sessionManager.getUserFaceDetectionEnable()) {
                    DBCaller.storeLogInDatabase(context, Constraint.USER_SEEN_PRMOTION, id + "", promotionPath, Constraint.PROMOTION);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * lunch other app
     */
    private void launchApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(Constraint.YOU_TUBE_PATH);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            ValidationHelper.showToast(MainActivity.this, getString(R.string.package_not_available));
        }
    }

    /**
     * open config screen
     */
    private void openConfigSettings() {


        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.password_layout, null);
        final EditText password = alertLayout.findViewById(R.id.password);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.lock));
        alert.setView(alertLayout);
        alert.setCancelable(false);

        alert.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton(R.string.unlockk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passwordString = password.getText().toString();
                String lockPassword = sessionManager.getPasswordLock();
                if (passwordString.equals("")) {
                    ValidationHelper.showToast(context, getString(R.string.empty_password));

                } else if (passwordString.equals(lockPassword)) {
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, ConfigSettings.class);
                    startActivity(intent);
                } else {
                    ValidationHelper.showToast(context, getString(R.string.invalid_password));
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();

    }


    private void checkAdminPermission() {

        DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(this, Admin.class);
        if (!mDPM.isAdminActive(mAdminName)) {
            if (sessionManager == null)
                sessionManager = SessionManager.get();
            sessionManager.setPasswordCorrect(true);

            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    mAdminName);

            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Your Explanation for requesting these Admin Capabilities.");
            startActivityForResult(intent, Constraint.ADMIN_REQUEST_CODE);

        }
    }


}