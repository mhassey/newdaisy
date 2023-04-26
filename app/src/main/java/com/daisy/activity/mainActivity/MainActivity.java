package com.daisy.activity.mainActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.TextView;

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
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.ActivityMainBinding;
import com.daisy.interfaces.CallBack;
import com.daisy.pojo.DimBrighness;
import com.daisy.pojo.response.ApkDetails;
import com.daisy.pojo.response.DeleteCardResponse;
import com.daisy.pojo.response.Download;
import com.daisy.pojo.response.DownloadFail;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.Interactor;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.pojo.response.Inversion;
import com.daisy.pojo.response.PriceCard;
import com.daisy.pojo.response.Pricing;
import com.daisy.pojo.response.Promotion;
import com.daisy.pojo.response.Promotions;
import com.daisy.pojo.response.Sanitised;
import com.daisy.pojo.response.SocketEvent;
import com.daisy.pojo.response.UpdateCards;
import com.daisy.service.BackgroundService;
import com.daisy.service.LogGenerateService;
import com.daisy.utils.CheckForSDCard;
import com.daisy.utils.Constraint;
import com.daisy.utils.DownloadFile;
import com.daisy.utils.DownloadJSFile;
import com.daisy.utils.OnSwipeTouchListener;
import com.daisy.utils.PermissionManager;
import com.daisy.utils.SanitisedSingletonObject;
import com.daisy.utils.SettingLockSingletonObject;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * Purpose -  MainActivity is an activity that show cards and promotions and pricing and handling all things related to price cards
 * Responsibility - Its loads cards,promotion send pricing to js and its also handles logs related price card and promotions
 **/
public class MainActivity extends BaseActivity implements CallBack, View.OnTouchListener, View.OnClickListener {
    private ActivityMainBinding mBinding;
    private SessionManager sessionManager;
    private MainActivityViewModel mViewModel;
    private Context context;
    private WebViewClient yourWebClient;
    private UpdateProductViewModel updateProductViewModel;
    private GetCardViewModel getCardViewModel;
    private AlertDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        SessionManager.get().logout(false);
        context = this;
        sessionWork();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        permissionAsking();
        windowWork();
        loadURL();
        intentWork();

    }


    /**
     * Check for permission
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
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


    private void handleTwentySecondTimeout() {
        SettingLockSingletonObject lockSingletonObject = SettingLockSingletonObject.getInstance();
        Handler countDownTimer = lockSingletonObject.getLockCounDownTimer();
        if (countDownTimer != null) {
            countDownTimer.removeCallbacksAndMessages(null);
        } else {
            countDownTimer = new Handler();
            lockSingletonObject.setCOunter(countDownTimer);

        }
        countDownTimer.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        }, Constraint.TWENTY_THOUSAND);

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
    }

    /**
     * Button clicks initializing
     */
    private void setOnClickListener() {

        mBinding.offLineIcon.setOnClickListener(this);


        mBinding.webView.setOnTouchListener(this);


    }


    /**
     * On resume have a functionality for making this screen landscape or portrait mode
     */
    @Override
    protected void onResume() {
        super.onResume();
        handleResumeWork();
        mBinding.webView.resumeTimers();
        mBinding.webView.onResume();

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
        checkWifiState();
    }


    /**
     * Handle all downloaded data
     */
    private void getDownloadData() {
        try {

            if (CheckForSDCard.isSDCardPresent()) {
                List<Promotion> promotions = sessionManager.getPromotion();
                List<Download> downloads = new ArrayList<>();

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

    void insertNewFile() {
        try {
            String val = sessionManager.getLocation();
            File directory = new File(val);
            File file[] = directory.listFiles();
            if (file != null) {
                for (File file1 : file) {
                    if (file1.isDirectory() && !file1.getAbsolutePath().contains(Constraint.MACOS)) {
                        File[] part = file1.listFiles();
                        for (File internalFile : part) {
                            if (internalFile.isDirectory() && internalFile.getAbsolutePath().contains(Constraint.hyperesources)) {
                                File[] resourceFile = internalFile.listFiles();
                                if (resourceFile != null) {
                                    for (File internalFiles : resourceFile) {
                                        if (internalFiles.getName().contains(Constraint.MobilePriceCard)) {
                                            internalFiles.delete();
                                        }
                                    }
                                    String filePathToWrite = internalFile.getAbsolutePath();
                                    Download download = new Download();
                                    download.setPath("https://mpc-android.s3.us-west-2.amazonaws.com/MobilePriceCard.zip");
                                    download.setType("");
                                    new DownloadJSFile(this
                                            , this, download, filePathToWrite).execute();

                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("JavascriptInterface")
    private void loadURL() {
        if (sessionManager.getLocation() != null && !sessionManager.getLocation().equals("")) {
            mBinding.webView.addJavascriptInterface(new WebAppInterface(this), Constraint.ANDROID); // To call methods in Android from using js in the html, AndroidInterface.showToast, AndroidInterface.getAndroidVersion etc
            mBinding.webView.setWebChromeClient(new WebClient());
//            long value = sessionManager.getDefaultTiming();
//            long elapsedTimeNs = System.currentTimeMillis() - value;
//            ValidationHelper.showToast(getApplicationContext(), (elapsedTimeNs/60000) + "");
            setWebViewClient();
            mBinding.webView.getSettings().setAllowFileAccessFromFileURLs(Constraint.TRUE);
            mBinding.webView.getSettings().setAllowFileAccess(Constraint.TRUE);
            mBinding.webView.setSoundEffectsEnabled(Constraint.TRUE);
            mBinding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(Constraint.TRUE);
            mBinding.webView.getSettings().setAllowUniversalAccessFromFileURLs(Constraint.TRUE);
//            mBinding.webView.getSettings().setAppCacheEnabled(Constraint.TRUE);
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
            mBinding.webView.getSettings().setMediaPlaybackRequiresUserGesture(Constraint.FALSE);
            if (Build.VERSION.SDK_INT >= Constraint.TWENTY_ONE) {
                mBinding.webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                CookieManager.getInstance().setAcceptThirdPartyCookies(mBinding.webView, Constraint.TRUE);
            }
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
                if (jsonArray.length() > 0) {
                    mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");
                }
                super.onFormResubmission(view, dontResend, resend);


            }

            @Override
            public void onLoadResource(WebView view, String url) {
                JSONObject jsonArray = pricingUpdateStart();
                if (jsonArray.length() > 0) {
                    mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");
                    mViewModel.setExceptionInHtml(false);
                }
                super.onLoadResource(view, url);

            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                JSONObject jsonArray = pricingUpdateStart();
                if (jsonArray.length() > 0) {
                    mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");
                    mViewModel.setExceptionInHtml(false);
                }
                super.onPageCommitVisible(view, url);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                JSONObject jsonArray = pricingUpdateStart();
                if (jsonArray.length() > 0) {
                    mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");
                    mViewModel.setExceptionInHtml(false);
                }
                super.onPageStarted(view, url, favicon);


            }


            @Override
            public void onPageFinished(WebView view, final String url) {
                try {

                    JSONObject jsonArray = pricingUpdateStart();
                    if (jsonArray.length() > 0) {
                        mBinding.webView.loadUrl("javascript:MobilePriceCard.setData(" + jsonArray + ")");
                        mViewModel.setExceptionInHtml(false);
                    }
                    promotionSettings();
                    boolean b = Utils.getInvertedTime();
                    if (b) {
                        mBinding.webView.loadUrl("javascript:MobilePriceCard.setNightmode(true)");
                    } else {
                        mBinding.webView.loadUrl("javascript:MobilePriceCard.setNightmode(false)");
                    }


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

                            if (dateEffective != null && !dateEffective.after(todayDate)) {
                                if (futureDate != null && futureDate.after(todayDate)) {
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
                                if (dateEffective != null && !dateEffective.after(todayDate)) {
                                    if (futureDate != null && futureDate.after(todayDate)) {
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void interactWithFaceDetection(Interactor interactor) {
        cancelDimBrightness();
        if (!SessionManager.get().isBrighnessDefault())
            SessionManager.get().setBrightness(0.9f);
        else
            SessionManager.get().setBrightness((Float.parseFloat(SessionManager.get().getMaxBrightness() + "") / 10));
        Utils.setFullBrightNess();

    }

    private void handleUperLayoutClick() {

        fireThirtySecondCounter();

        DBCaller.storeLogInDatabase(context, Constraint.TOUCH, Constraint.TOUCHES_DESCRIPTION, "", Constraint.APPLICATION_LOGS);
        Inversion inversion = new Inversion();
        inversion.setInvert(Utils.getInvertedTime());
        inverted(inversion);
        Sanitised(new Sanitised());
        boolean value = sessionManager.getUpdateNotShow();
        boolean isDialogOpen = sessionManager.getupdateDialog();
        if (!isDialogOpen) {
            if (!value) {
                ApkDetails apkDetails = sessionManager.getApkDetails();
                if (apkDetails != null) {
                    updateApk(apkDetails);
                }
            }
        }

    }


    /**
     * Purpose - checkWifiState method checks the wifi state and handle the ui accordingly
     */
    private void checkWifiState() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            InternetResponse internetResponse = new InternetResponse();
            try {
                if (!wifiManager.isWifiEnabled()) {
                    internetResponse.setAvailable(true);
                } else {
                    internetResponse.setAvailable(false);

                }
                InternetAvailability(internetResponse);

            } catch (Exception e) {
            }

        } catch (Exception e) {

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




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.webView.destroy();

    }


    /**
     * open editor tool activity
     */
    private void editorToolOpen() {
        Intent intent = new Intent(MainActivity.this, EditorTool.class);
        startActivity(intent);
        finish();
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
        if (mBinding.supportWebViewLayout.getVisibility() == View.VISIBLE) {
            openMainWebView();
        } else {

            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dimBrighness(DimBrighness dimm) {
        dimBrightness();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SendCustomEvent(SocketEvent socketEvent) {
        mBinding.webView.loadUrl("javascript:MobilePriceCard.triggerCustomEvent('" + socketEvent.getMessage() + "')");

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

            List<Promotion> promotions = sessionManager.getPromotion();
            JSONArray listOfPromo = sessionManager.getPromotions();
            List<Download> downloads = new ArrayList<>();

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

        } catch (Exception e) {

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCards(UpdateCards updateCards) {
        getDownloadData();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
//       startService(new Intent(this, BackgroundService.class));
        int action = event.getAction();
        switch(action & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_POINTER_DOWN:
                // multitouch!! - touch down
                int count = event.getPointerCount(); // Number of 'fingers' in this time
                if (count==3)
                {
                    settingClick();
                }
                break;
            case MotionEvent.ACTION_UP:
                handleUperLayoutClick();
                break;

        }


        return false;
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
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
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
            if (cmd.equals(Constraint.click)) {

                SessionManager.get().clckPerform(true);
                if (!isMyServiceRunning(LogGenerateService.class)) {
                    startService(new Intent(MainActivity.this, LogGenerateService.class));
                }


                //IpSearched("Some value");
            }


        }

        @JavascriptInterface
        public void systemEvent(String cmd, JSONArray msg) {


        }

        private boolean isMyServiceRunning(Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }

        @JavascriptInterface
        public void heartbeat(String msg) {
        }


        @JavascriptInterface
        public void callFromJS() {

        }

        @JavascriptInterface
        public void openApplication(String event) {
            if (event.contains(Constraint.HTTP) || event.contains(Constraint.HTTPS)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onSupportWebView(event);
                    }
                });

            } else if (event.contains(Constraint.HOME_SCREEN)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moveTaskToBack(true);
                    }
                });
            } else

                launchApp(event);
        }

        @JavascriptInterface
        public void openBrowser(String event, int time) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onSupportWebView(event);
                    setDeleteTimer(time);
                }
            });


        }

    }


    private void setDeleteTimer(int time) {
        int second = time * 1000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBinding.supportWebViewLayout.getVisibility() == View.VISIBLE) {
                            openMainWebView();
                        }
                    }
                });

                timer.cancel();
            }
        }, second, second);

    }


    private void onSupportWebView(String url) {
        mBinding.supportWebView.getSettings().setAllowFileAccessFromFileURLs(Constraint.TRUE);
        mBinding.supportWebView.getSettings().setAllowFileAccess(Constraint.TRUE);
        mBinding.supportWebView.setSoundEffectsEnabled(Constraint.TRUE);
        mBinding.supportWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(Constraint.TRUE);
        mBinding.supportWebView.getSettings().setAllowUniversalAccessFromFileURLs(Constraint.TRUE);
//        mBinding.supportWebView.getSettings().setAppCacheEnabled(Constraint.TRUE);
        mBinding.supportWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mBinding.supportWebView.getSettings().setAllowContentAccess(Constraint.TRUE);
        mBinding.supportWebView.getSettings().setDomStorageEnabled(Constraint.TRUE);
        mBinding.supportWebView.getSettings().setJavaScriptEnabled(Constraint.TRUE); // enable javascript
        mBinding.supportWebView.getSettings().setBuiltInZoomControls(Constraint.TRUE);
        mBinding.supportWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mBinding.supportWebView.getSettings().setLoadWithOverviewMode(Constraint.TRUE);
        mBinding.supportWebView.getSettings().setUseWideViewPort(Constraint.TRUE);

        mBinding.supportWebView.getSettings().setBuiltInZoomControls(Constraint.TRUE);
        mBinding.supportWebView.getSettings().setDisplayZoomControls(Constraint.FALSE);

        mBinding.supportWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mBinding.supportWebView.setScrollbarFadingEnabled(Constraint.FALSE);
        mBinding.webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        mBinding.supportWebView.getSettings().setPluginState(WebSettings.PluginState.ON);

        mBinding.supportWebView.getSettings().setMediaPlaybackRequiresUserGesture(Constraint.FALSE);

        if (Build.VERSION.SDK_INT >= Constraint.TWENTY_ONE) {
            mBinding.supportWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(mBinding.supportWebView, Constraint.TRUE);
        }
        mBinding.webView.getSettings().setUserAgentString(Constraint.GIVEN_BROWSER);

        mBinding.supportWebView.loadUrl(url);

        mBinding.webViewLayout.setVisibility(View.GONE);
        mBinding.supportWebViewLayout.setVisibility(View.VISIBLE);
        mBinding.supportWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                showHideProgressDialog(true);

                if (progress == Constraint.HUNDERD)
                    showHideProgressDialog(false);

            }
        });


    }

    private void openMainWebView() {
        mBinding.webViewLayout.setVisibility(View.VISIBLE);
        mBinding.supportWebViewLayout.setVisibility(View.GONE);
    }


    /**
     * lunch other app
     */
    private void launchApp(String name) {
        try {
            if (name.equals(Constraint.MOTO_RETAIL_MAIN_ACTIVITY)) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName componentName = new ComponentName(Constraint.MOTO_RETAIL_APP, Constraint.MOTO_RETAIL_MAIN_ACTIVITY);
                intent.setComponent(componentName);
                startActivity(intent);

            } else {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(name);
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {

                    ValidationHelper.showToast(MainActivity.this, getString(R.string.app_is_not_installed));
                }
            }


        } catch (Exception e) {

        }
    }


    @Override
    protected void onPause() {

        super.onPause();
        mBinding.webView.onPause();
        mBinding.webView.pauseTimers();
    }

    /**
     * open config screen
     */
    private void openConfigSettings() {


        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.password_layout, null);
        final EditText password = alertLayout.findViewById(R.id.password);
        final TextView storeName = alertLayout.findViewById(R.id.store_name);
        final TextView cancle = alertLayout.findViewById(R.id.cancel);
        final TextView unLock = alertLayout.findViewById(R.id.unlock);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setView(alertLayout);
        alert.setCancelable(false);

        storeName.setText(SessionManager.get().getLoginResponse().getStoreName());
        dialog = alert.create();
        InsetDrawable insetDrawable = new InsetDrawable(new ColorDrawable(Color.TRANSPARENT), 150, 0, 150, 0);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                nullAndVoidTimer();


            }
        });
        unLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String passwordString = password.getText().toString();
                String lockPassword = sessionManager.getPasswordLock();
                if (passwordString.equals("")) {
                    ValidationHelper.showToast(context, getString(R.string.empty_password));

                } else if (passwordString.equals(lockPassword)) {
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, ConfigSettings.class);
                    startActivity(intent);
                    nullAndVoidTimer();
                } else {
                    ValidationHelper.showToast(context, getString(R.string.invalid_password));
                }
            }

        });
        dialog.show();
        handleTwentySecondTimeout();

    }

    private void nullAndVoidTimer() {
        SettingLockSingletonObject lockSingletonObject = SettingLockSingletonObject.getInstance();
        Handler countDownTimer = lockSingletonObject.getLockCounDownTimer();
        if (countDownTimer != null) {
            countDownTimer.removeCallbacksAndMessages(null);
        }
    }


    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }


    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

}

