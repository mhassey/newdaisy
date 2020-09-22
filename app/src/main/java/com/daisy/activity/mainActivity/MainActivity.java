package com.daisy.activity.mainActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.ObjectDetection.CameraSurfaceView;
import com.daisy.ObjectDetection.cam.FaceDetectionCamera;
import com.daisy.ObjectDetection.cam.FrontCameraRetriever;
import com.daisy.R;
import com.daisy.activity.apkUpdate.DownloadUpdateApk;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.configSettings.ConfigSettings;
import com.daisy.activity.deleteCard.DeleteCardViewModel;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.ActivityMainBinding;
import com.daisy.interfaces.CallBack;
import com.daisy.pojo.response.ApkDetails;
import com.daisy.pojo.response.DeleteCardResponse;
import com.daisy.pojo.response.Download;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.pojo.response.LoginResponse;
import com.daisy.pojo.response.Pricing;
import com.daisy.pojo.response.Promotion;
import com.daisy.pojo.response.Promotions;
import com.daisy.pojo.response.UpdateCards;
import com.daisy.utils.CheckForSDCard;
import com.daisy.utils.Constraint;
import com.daisy.utils.DownloadFile;
import com.daisy.utils.OnSwipeTouchListener;
import com.daisy.utils.PermissionManager;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends BaseActivity implements CallBack, View.OnClickListener, FrontCameraRetriever.Listener, FaceDetectionCamera.Listener {
    private ActivityMainBinding mBinding;
    private SessionManager sessionManager;
    private MainActivityViewModel mViewModel;
    private Context context;
    private int i = 0;
    private WebViewClient yourWebClient;
    boolean loadingFinished = true;
    boolean redirect = false;


    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initService();
        setOnClickListener();
        // getDownloadData();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        mBinding = DataBindingUtil.setContentView(this, (R.layout.activity_main));
        setNoTitleBar(this);
        context = this;
        sessionWork();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FrontCameraRetriever.retrieveFor(this);
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        PermissionManager.checkPermission(this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE_MAIN);
        windowWork();
        loadURL();
        intentWork();
    }


    private void sessionWork() {
        sessionManager = SessionManager.get();
        sessionManager.dialogShow(Constraint.FALSE);
        sessionManager.onBoarding(Constraint.TRUE);
    }

    private void windowWork() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }


    private void intentWork() {
        String available = getIntent().getStringExtra(Constraint.PROMOTION);
        String priceavailable = getIntent().getStringExtra(Constraint.PRICING);

        if (available != null && !available.equals("")) {
            updatePromotion(new Promotion());
        }
        if (priceavailable != null && !priceavailable.equals("")) {
            loadURL();
            deleteCard();
        }

    }


    private void initService() {
        long time1 = TimeUnit.SECONDS.toMillis(Constraint.ONE);
        Utils.constructJobForBackground(time1, getApplicationContext());
    }


    private void setOnClickListener() {
        mBinding.settingHeader.setOnClickListener(this);
        mBinding.setting.setOnClickListener(this);
        mBinding.offLineIcon.setOnClickListener(this);
        mBinding.swipeclick.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                settingHeader();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.getLocation() != null && !sessionManager.getLocation().equals("")) {

            if (!sessionManager.getOrientation().equals(getString(R.string.defaultt))) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
        }
    }


    private void getDownloadData() {
        try {

            if (CheckForSDCard.isSDCardPresent()) {

                List<Promotion> promotions = sessionManager.getPromotion();
                List<Download> downloads = new ArrayList<>();
                if (checkPermission()) {
                    final String url = Utils.getPath();
                    if (url != null) {
                        downloadFiles(url, promotions, downloads);
                    } else {
                        editorToolOpen();
                    }
                }
            } else {
                ValidationHelper.showToast(this, getString(R.string.storage_not_available));
            }
        } catch (Exception e) {

        }
    }

    private void downloadFiles(String url, List<Promotion> promotions, List<Download> downloads) {
        Download download = new Download();
        download.setPath(url);
        download.setType("");
        downloads.add(download);
        if (promotions != null) {
            for (Promotion promotion : promotions) {
                Download downloadPromotion = new Download();
                downloadPromotion.setPath(promotion.getFileName());
                downloadPromotion.setDateCreated(promotion.getDateCreated());
                downloadPromotion.setDateExpires(promotion.getDateExpires());
                downloadPromotion.setType(getString(R.string.promotion));
                downloads.add(downloadPromotion);
            }
        }
        new DownloadFile(MainActivity.this, MainActivity.this, downloads).execute(url);
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

        ViewGroup.LayoutParams params = mBinding.webView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mBinding.webView.requestLayout();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[Constraint.ZERO] == PackageManager.PERMISSION_DENIED) {
            boolean showRationale = shouldShowRequestPermissionRationale(permissions[Constraint.ZERO]);
            if (!showRationale) {
            } else {
                // If request is cancelled, the result arrays are empty.
                PermissionManager.checkPermission(MainActivity.this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE_MAIN);
            }
        } else {
            if (grantResults.length > Constraint.ZERO
                    && grantResults[Constraint.ZERO] == PackageManager.PERMISSION_GRANTED) {
                getDownloadData();

            }
        }
        return;

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void callBack(String data) {
        mBinding.webView.post(new Runnable() {
            @Override
            public void run() {
                if (!sessionManager.getOrientation().equals(getString(R.string.defaultt))) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                }
                loadURL();
            }
        });

    }

    @Override
    public void callBackApkUpdate(String data) {
        sessionManager.uninstallShow(true);
        installApk();
        sessionManager.deleteApkVersion();
        this.finishAffinity();

    }

    private void installApk() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("JavascriptInterface")
    private void loadURL() {
        if (sessionManager.getLocation() != null && !sessionManager.getLocation().equals("")) {
            mBinding.webView.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface"); // To call methods in Android from using js in the html, AndroidInterface.showToast, AndroidInterface.getAndroidVersion etc

            mBinding.webView.setWebChromeClient(new WebClient());
            setWebViewClient();
            mBinding.webView.getSettings().setAllowFileAccessFromFileURLs(true);
            mBinding.webView.getSettings().setAllowFileAccess(true);
            mBinding.webView.setSoundEffectsEnabled(true);
            mBinding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mBinding.webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            mBinding.webView.getSettings().setAppCacheEnabled(true);
            mBinding.webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            mBinding.webView.getSettings().setAllowContentAccess(true);
            mBinding.webView.getSettings().setDomStorageEnabled(true);
            mBinding.webView.getSettings().setJavaScriptEnabled(true); // enable javascript
            mBinding.webView.getSettings().setBuiltInZoomControls(true);
            mBinding.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            mBinding.webView.getSettings().setLoadWithOverviewMode(true);
            mBinding.webView.getSettings().setUseWideViewPort(true);

            mBinding.webView.getSettings().setBuiltInZoomControls(true);
            mBinding.webView.getSettings().setDisplayZoomControls(false);

            mBinding.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            mBinding.webView.setScrollbarFadingEnabled(false);
            mBinding.webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
            mBinding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

            if (Build.VERSION.SDK_INT >= 21) {
                mBinding.webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                CookieManager.getInstance().setAcceptThirdPartyCookies(mBinding.webView, true);
            }
            mBinding.webView.getSettings().setUserAgentString(Constraint.GIVEN_BROWSER);
            String val = sessionManager.getLocation();
            boolean isDelete = sessionManager.getCardDeleted();
            File f = new File(val);
            File file[] = f.listFiles();
            if (file != null) {
                for (File file1 : file) {
                    if (file1.isDirectory() && !file1.getAbsolutePath().contains("_MACOSX")) {
                        File mainFile = new File(file1.getAbsoluteFile() + Constraint.SLASH + Constraint.FILE_NAME);
                        if (mainFile.exists()) {
                            mBinding.webView.loadUrl(Constraint.FILE + file1.getAbsoluteFile() + Constraint.SLASH + Constraint.FILE_NAME);
                            sessionManager.setMainFilePath(file1.getAbsoluteFile().toString());
                            if (!isDelete)
                                deleteCard();
                        } else {

                            File file2 = new File(sessionManager.getLocation() + Constraint.SLASH + Constraint.FILE_NAME);
                            File file3 = new File(sessionManager.getLocation() + Constraint.SLASH + file1.getName() + Constraint.SLASH + file1.getName() + Constraint.HTML);

                            if (file2.exists()) {
                                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + Constraint.FILE_NAME);
                                sessionManager.setMainFilePath(sessionManager.getLocation());

                                if (!isDelete)
                                    deleteCard();
                            } else if (file3.exists()) {
                                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + file1.getName() + Constraint.SLASH + file1.getName() + Constraint.HTML);
                                sessionManager.setMainFilePath(sessionManager.getLocation() + Constraint.SLASH + file1.getName());

                                if (!isDelete)
                                    deleteCard();
                            } else {
                                sessionManager.deleteLocation();
                                getDownloadData();
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

    private HashMap<String, String> getDeleteCardRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constraint.TOKEN, sessionManager.getDeviceToken());
        return hashMap;
    }

    private void handleDeleteResponse(GlobalResponse<DeleteCardResponse> deleteCardResponseGlobalResponse) {
        if (deleteCardResponseGlobalResponse.isApi_status()) {

        }
    }


    private void setWebViewClient() {


        yourWebClient = new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pricingUpdateStart();


            }


            @Override
            public void onPageFinished(WebView view, final String url) {
                try {

                    if (mViewModel.getJsonArray() != null) {
                        if (mViewModel.getJsonArray().length() > 0) {

                            mBinding.webView.loadUrl("javascript:handlePriceDynamically(" + mViewModel.getJsonArray() + ")");
                            mViewModel.setExceptionInHtml(false);

                        }
                    }


                    // pricingUpdate();
                    promotionSettings();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        };
        mBinding.webView.setWebViewClient(yourWebClient);

    }

    private void pricingUpdateStart() {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            List<Pricing> pricing = sessionManager.getPricing();
            Pricing pricing1 = null;
            if (pricing != null && !pricing.isEmpty()) {
                OUTER_LOOP:
                for (int i = (pricing.size() - Constraint.ONE); i >= Constraint.ZERO; i--) {
                    LoginResponse loginResponse = sessionManager.getLoginResponse();
                    try {
                        if (loginResponse.getPricingPlanID().equals(pricing.get(i).getPricingPlanID())) {
                            SimpleDateFormat sdf = new SimpleDateFormat(Constraint.YYY_MM_DD);
                            Date futureDate;
                            if (pricing.get(i).getTimeExpires() != null) {
                                futureDate = sdf.parse(pricing.get(i).getDateExpires() + " " + pricing.get(i).getTimeExpires());

                            } else {
                                futureDate = sdf.parse(pricing.get(i).getDateExpires() + " " + "00:00:00");

                            }
                            Date dateEffective;
                            if (pricing.get(i).getTimeEffective() != null) {
                                dateEffective = sdf.parse(pricing.get(i).getDateEffective() + " " + pricing.get(i).getTimeEffective());

                            } else {
                                dateEffective = sdf.parse(pricing.get(i).getDateEffective() + " " + "00:00:00");

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

                    }
                }

                if (pricing1 == null) {
                    for (int i = 0; i < pricing.size(); i++) {
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
                    jsonObject.put(Constraint.MSRP, pricing1.getMsrp());
                    jsonObject.put(Constraint.OUR_PRICE, pricing1.getOurprice());
                    jsonObject.put(Constraint.SALE_PRICE, pricing1.getSaleprice());
                    jsonObject.put(Constraint.PLAN_A_PRICE, pricing1.getPlanAprice());
                    jsonObject.put(Constraint.PLAN_B_PRICE, pricing1.getPlanBprice());
                    jsonObject.put(Constraint.PLAN_C_PRICE, pricing1.getPlanCprice());
                    jsonObject.put(Constraint.PLAN_D_PRICE, pricing1.getPlanDprice());
                    jsonObject.put(Constraint.DOWN_PRICE, pricing1.getDownprice());
                    jsonObject.put(Constraint.MONTHLY_PRICE, pricing1.getMonthlyprice());
                    jsonObject.put(Constraint.CONFIG_ONE, pricing1.getConfig1());
                    jsonObject.put(Constraint.CONFIG_TWO, pricing1.getConfig2());
                    jsonObject.put(Constraint.CONFIG_THREE, pricing1.getConfig3());
                    jsonObject.put(Constraint.CONFIG_FOUR, pricing1.getConfig4());
                    jsonArray.put(jsonObject);

                }


            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        mViewModel.setJsonArray(jsonArray);
    }

    private void pricingUpdate() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            List<Pricing> pricing = sessionManager.getPricing();
            Pricing pricing1 = null;
            if (pricing != null && !pricing.isEmpty()) {
                OUTER_LOOP:
                for (int i = (pricing.size() - Constraint.ONE); i >= Constraint.ZERO; i--) {
                    LoginResponse loginResponse = sessionManager.getLoginResponse();
                    try {
                        if (loginResponse.getPricingPlanID().equals(pricing.get(i).getPricingPlanID())) {
                            SimpleDateFormat sdf = new SimpleDateFormat(Constraint.YYY_MM_DD);
                            Date futureDate;
                            futureDate = sdf.parse(pricing.get(i).getDateExpires());
                            Date dateEffective;
                            dateEffective = sdf.parse(pricing.get(i).getDateEffective());
                            Date todayDate = new Date();

                            if (!dateEffective.after(todayDate)) {
                                if (futureDate.after(todayDate)) {
                                    pricing1 = pricing.get(i);
                                    break OUTER_LOOP;
                                }

                            }
                        }
                    } catch (Exception e) {

                    }
                }

                if (pricing1 == null) {
                    for (int i = 0; i < pricing.size(); i++) {
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
                    jsonObject.put(Constraint.MSRP, pricing1.getMsrp());
                    jsonObject.put(Constraint.OUR_PRICE, pricing1.getOurprice());
                    jsonObject.put(Constraint.SALE_PRICE, pricing1.getSaleprice());
                    jsonObject.put(Constraint.PLAN_A_PRICE, pricing1.getPlanAprice());
                    jsonObject.put(Constraint.PLAN_B_PRICE, pricing1.getPlanBprice());
                    jsonObject.put(Constraint.PLAN_C_PRICE, pricing1.getPlanCprice());
                    jsonObject.put(Constraint.PLAN_D_PRICE, pricing1.getPlanDprice());
                    jsonObject.put(Constraint.DOWN_PRICE, pricing1.getDownprice());
                    jsonObject.put(Constraint.MONTHLY_PRICE, pricing1.getMonthlyprice());
                    jsonObject.put(Constraint.CONFIG_ONE, pricing1.getConfig1());
                    jsonObject.put(Constraint.CONFIG_TWO, pricing1.getConfig2());
                    jsonObject.put(Constraint.CONFIG_THREE, pricing1.getConfig3());
                    jsonObject.put(Constraint.CONFIG_FOUR, pricing1.getConfig4());
                    jsonArray.put(jsonObject);

                }


            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        Log.e("Working", jsonArray.toString());

        if (jsonArray.length() > 0)
            mBinding.webView.loadUrl("javascript:handlePriceDynamically(" + jsonArray + ")");
    }


    private void promotionSettings() {
        JSONArray elements = new JSONArray();
        try {
            JSONArray promotionsArray = sessionManager.getPromotions();
            if (promotionsArray != null) {
                for (int i = 0; i < promotionsArray.length(); i++) {
                    JSONObject jsonObject1 = new JSONObject();
                    JSONObject promtotionJsonObect = promotionsArray.getJSONObject(i);
                    String value = promtotionJsonObect.getString(Constraint.PROMOTION);
                    if (value.contains(Constraint.PROMOTION)) {
                        File file = new File(value + Constraint.FILE_NAME);
                        File check = new File(value);
                        File mainCheck = new File(value + check.getName() + Constraint.EXTENTION);
                        if (promtotionJsonObect.get("dateExpires") != null && promtotionJsonObect.get("dateCreated") != null && !promtotionJsonObect.get("dateExpires").equals("0000-00-00 00:00:00")) {
                            String dateExpire = promtotionJsonObect.get("dateExpires").toString();
                            String dateCreated = promtotionJsonObect.get("dateCreated").toString();
                            if (file.exists()) {


                                SimpleDateFormat sdf = new SimpleDateFormat(Constraint.YYY_MM_DD);
                                Date futureDate;
                                futureDate = sdf.parse(dateExpire);
                                Date dateEffective;
                                dateEffective = sdf.parse(dateCreated);
                                Date todayDate = new Date();
                                if (!dateEffective.after(todayDate)) {
                                    if (futureDate.after(todayDate)) {
                                        jsonObject1.put(Constraint.PROMOTION + i, Constraint.PROMOTION + Constraint.SLASH + check.getName() + Constraint.SLASH + Constraint.FILE_NAME);
                                        elements.put(jsonObject1);
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
                                        jsonObject1.put(Constraint.PROMOTION + i, Constraint.PROMOTION + Constraint.SLASH + check.getName() + Constraint.SLASH + check.getName() + Constraint.EXTENTION);
                                        elements.put(jsonObject1);
                                    }
                                }

                            }
                        } else {
                            if (file.exists()) {
                                jsonObject1.put(Constraint.PROMOTION + i, Constraint.PROMOTION + Constraint.SLASH + check.getName() + Constraint.SLASH + Constraint.FILE_NAME);
                                elements.put(jsonObject1);

                            } else if (mainCheck.exists()) {
                                jsonObject1.put(Constraint.PROMOTION + i, Constraint.PROMOTION + Constraint.SLASH + check.getName() + Constraint.SLASH + check.getName() + Constraint.EXTENTION);
                                elements.put(jsonObject1);
                            }
                        }


                    }
                }
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        if (elements.length() > 0)
            mBinding.webView.loadUrl("javascript:handlePrmotion(" + elements + ")");
    }

    @Override
    public void onFaceDetected() {
        DBCaller.storeLogInDatabase(context, getString(R.string.face_detected), "", "", Constraint.APPLICATION_LOGS);
    }

    @Override
    public void onFaceTimedOut() {

    }

    @Override
    public void onFaceDetectionNonRecoverableError() {

    }

    @Override
    public void onLoaded(FaceDetectionCamera camera) {
        try {
            // When the front facing camera has been retrieved we still need to ensure our display is ready
            // so we will let the camera surface view initialise the camera i.e turn face detection on
            SurfaceView cameraSurface = new CameraSurfaceView(this, camera, this);
            // Add the surface view (i.e. camera preview to our layout)
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mBinding.helloWorldCameraPreview.addView(cameraSurface);
                    // Stuff that updates the UI

                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public void onFailedToLoadFaceDetectionCamera() {

    }


    private class WebChromeClientCustomPoster extends WebChromeClient {

        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settingHeader: {
                settingHeader();
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

    private void settingClick() {
        DBCaller.storeLogInDatabase(context, Constraint.SETTINGS, Constraint.SETTINGS_DESCRIPTION, "", Constraint.APPLICATION_LOGS);
        // editorToolOpenwithValue();
        openConfigSettings();
    }


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

    private void editorToolOpen() {
        Intent intent = new Intent(MainActivity.this, EditorTool.class);
        startActivity(intent);
        finish();
    }

    private void editorToolOpenwithValue() {
        Intent intent = new Intent(MainActivity.this, EditorTool.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constraint.CALLFROM, Constraint.SETTINGS);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    void hideSettingsIcon() {
        Runnable mRunnable;
        Handler mHandler = new Handler();
        mRunnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mBinding.setting.getVisibility() == View.VISIBLE) {
                    mBinding.setting.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
                }
            }
        };
        mHandler.postDelayed(mRunnable, Constraint.TWENTY * Constraint.THOUSAND);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadUrl(Pricing pricing) {
        pricingUpdate();
        deleteCard();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPromotion(Promotions promotion)
    {
        promotionSettings();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateApk(ApkDetails apk) {

        sessionManager.dialogShow(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_available));
        builder.setPositiveButton(getString(R.string.proceed), updatePerform(apk));
        builder.setNegativeButton(getString(R.string.dismiss), dismissUpdate());
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


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


    private DialogInterface.OnClickListener updatePerform(ApkDetails apk) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String link = apk.getAndroid().getLink();
                sessionManager.dialogShow(Constraint.FALSE);
                new DownloadUpdateApk(MainActivity.this, MainActivity.this).execute(link);

            }
        };
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePromotion(Promotion promotionss) {
        i = 0;
        Utils.deletePromotion();
        sessionManager.deletePromotions();
        List<Promotion> promotions = sessionManager.getPromotion();
        List<Download> downloads = new ArrayList<>();
        if (checkPermission()) {
            for (Promotion promotion : promotions) {
                Download downloadPromotion = new Download();
                downloadPromotion.setPath(promotion.getFileName());
                downloadPromotion.setDateExpires(promotion.getDateExpires());
                downloadPromotion.setDateCreated(promotion.getDateCreated());
                downloadPromotion.setType(getString(R.string.promotion));
                downloads.add(downloadPromotion);
            }

            new DownloadFile(MainActivity.this, MainActivity.this, downloads).execute();
            sessionManager.setCardDeleted(Constraint.FALSE);
            deleteCard();
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
                loadURL();
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

        // Show a toast from the web page
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public int getAndroidVersion() {
            return android.os.Build.VERSION.SDK_INT;
        }

        @JavascriptInterface
        public void showAndroidVersion(String versionName) {
            Toast.makeText(mContext, versionName, Toast.LENGTH_SHORT).show();
        }

    }

    private void openConfigSettings() {


        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.password_layout, null);
        final EditText password = alertLayout.findViewById(R.id.password);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.lock));
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setPositiveButton(R.string.unlockk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passwordString = password.getText().toString();
                String lockPassword = sessionManager.getPasswordLock();
                if (passwordString.equals(lockPassword)) {
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


}
