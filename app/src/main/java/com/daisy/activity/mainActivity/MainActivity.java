package com.daisy.activity.mainActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.ObjectDetection.CameraSurfaceView;
import com.daisy.ObjectDetection.cam.FaceDetectionCamera;
import com.daisy.ObjectDetection.cam.FrontCameraRetriever;
import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.deleteCard.DeleteCardViewModel;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.ActivityMainBinding;
import com.daisy.interfaces.CallBack;
import com.daisy.pojo.response.DeleteCardResponse;
import com.daisy.pojo.response.Download;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.pojo.response.LoginResponse;
import com.daisy.pojo.response.Pricing;
import com.daisy.pojo.response.Promotion;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends BaseActivity implements CallBack, View.OnClickListener, FrontCameraRetriever.Listener, FaceDetectionCamera.Listener {
//    public class MainActivity extends BaseActivity implements CallBack, View.OnClickListener {

    private ActivityMainBinding mBinding;
    private SessionManager sessionManager;
    private MainActivityViewModel mViewModel;
    private Context context;
    private boolean isRedirected;
    private int i = 0;
    private   WebViewClient yourWebClient;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initService();
        setOnClickListener();
    }


    private void initService() {
        long time1 = TimeUnit.SECONDS.toMillis(Constraint.ONE);
        Utils.constructJobForBackground(time1, getApplicationContext());
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {

        setNoTitleBar(this);
        mBinding = DataBindingUtil.setContentView(this, (R.layout.activity_main));
        context = this;
        FrontCameraRetriever.retrieveFor(this);
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        sessionManager = SessionManager.get();
        sessionManager.onBoarding(true);
        PermissionManager.checkPermission(this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE_MAIN);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadURL();
      String available=  getIntent().getStringExtra(Constraint.PROMOTION);
        String priceavailable=  getIntent().getStringExtra(Constraint.PRICING);

        if (available!=null && !available.equals(""))
        {
            updatePromotion(new Promotion());
        }
        if (priceavailable!=null && !priceavailable.equals(""))
        {
            deleteCard();
        }
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


    private void getDownloadData() {
        try {

            if (CheckForSDCard.isSDCardPresent()) {

                List<Promotion> promotions = sessionManager.getPromotion();
                List<Download> downloads = new ArrayList<>();
                //check if app has permission to write to the external storage.
                if (checkPermission()) {
                    //Get the URL entered
                    final String url = Utils.getPath();
                    if (url != null) {
                        Download download = new Download();
                        download.setPath(url);
                        download.setType("");
                        downloads.add(download);
                        if (promotions != null) {
                            for (Promotion promotion : promotions) {
                                Download downloadPromotion = new Download();
                                downloadPromotion.setPath(promotion.getFileName());
                                downloadPromotion.setType(getString(R.string.promotion));
                                downloads.add(downloadPromotion);
                            }
                        }
                        new DownloadFile(MainActivity.this, MainActivity.this, downloads).execute(url);

                    } else {
                        editorToolOpen();
                    }
                }
            } else {
                ValidationHelper.showToast(this, getString(R.string.storage_not_available));
            }
        }
        catch (Exception e)
        {

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

        ViewGroup.LayoutParams params = mBinding.webView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mBinding.webView.requestLayout();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
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
                loadURL();
            }
        });

    }

    @SuppressLint("JavascriptInterface")
    private void loadURL() {
        if (sessionManager.getLocation() != null && !sessionManager.getLocation().equals("")) {
//            mBinding.webView.clearCache(true);
//            mBinding.webView.clearHistory();
          //  clearCookies(getApplicationContext());
            mBinding.webView.setWebChromeClient(new WebClient());
            setWebViewClient();
            mBinding.webView.getSettings().setAllowFileAccessFromFileURLs(true);
            mBinding.webView.getSettings().setAllowFileAccess(true);
            mBinding.webView.setSoundEffectsEnabled(true);
            mBinding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mBinding.webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
              mBinding.webView.getSettings().setAppCacheEnabled(true);

//            mBinding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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

            mBinding.webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");


            String val = sessionManager.getLocation();
            boolean isDelete = sessionManager.getCardDeleted();

            Log.e("ka;lio",isDelete+"");
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
                            File file3 = new File(sessionManager.getLocation() + Constraint.SLASH + file1.getName() + Constraint.SLASH + file1.getName() + ".html");

                            if (file2.exists()) {
                                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + Constraint.FILE_NAME);
                                sessionManager.setMainFilePath(sessionManager.getLocation());

                                if (!isDelete)
                                    deleteCard();
                            } else if (file3.exists()) {
                                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + file1.getName() + Constraint.SLASH + file1.getName() + ".html");
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
            Log.e("checking...", "worki");
            DeleteCardViewModel deleteCardViewModel = new ViewModelProvider(this).get(DeleteCardViewModel.class);
            deleteCardViewModel.setMutableLiveData(getDeleteCardRequest());
            LiveData<GlobalResponse<DeleteCardResponse>> liveData = deleteCardViewModel.getLiveData();
            liveData.observe(this, new Observer<GlobalResponse<DeleteCardResponse>>() {
                @Override
                public void onChanged(GlobalResponse<DeleteCardResponse> deleteCardResponseGlobalResponse) {
                    Log.e("checking...", "working");

                    sessionManager.setCardDeleted(true);
                    handleDeleteResponse(deleteCardResponseGlobalResponse);
                }
            });
        }
    }


    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //  Log.d(C.TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            //   Log.d(C.TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
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
//                if (url.contains(Constraint.FILE)) {
//                    DBCaller.storeLogInDatabase(context, Constraint.WEB_PAGE_LOAD, Constraint.WEBPAGE_LOAD_DESCRIPTION, url, Constraint.CARD_LOGS);
//                }

            }


            @Override
            public void onPageFinished(WebView view, final String url) {
            try {

                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                try {
                    List<Pricing> pricing = sessionManager.getPricing();
                    Pricing pricing1 = null;
                    if (pricing != null && !pricing.isEmpty()) {
                        for (int i = 0; i < pricing.size(); i++) {
                            LoginResponse loginResponse = sessionManager.getLoginResponse();
                            if (loginResponse.getPricingPlanID().equals(pricing.get(i).getPricingPlanID())) {
                                pricing1 = pricing.get(i);
                            }
                        }
                        if (pricing1 == null) {
                            for (int i = 0; i < pricing.size(); i++) {
                                if (pricing.get(i).getIsDefault() != null && pricing.get(i).getIsDefault().equals("1")) {
                                    pricing1 = pricing.get(i);
                                }

                            }
                        }
                        if (pricing1 != null) {
                            jsonObject.put("idproductFluid", pricing1.getIdproductFluid());
                            jsonObject.put("idproductStatic", pricing1.getIdproductStatic());
                            jsonObject.put("dateEffective", pricing1.getDateEffective());
                            jsonObject.put("timeEffective", pricing1.getTimeEffective());
                            jsonObject.put("msrp", pricing1.getMsrp());
                            jsonObject.put("ourprice", pricing1.getOurprice());
                            jsonObject.put("saleprice", pricing1.getSaleprice());
                            jsonObject.put("planAprice", pricing1.getPlanAprice());
                            jsonObject.put("planBprice", pricing1.getPlanBprice());
                            jsonObject.put("planCprice", pricing1.getPlanCprice());
                            jsonObject.put("planDprice", pricing1.getPlanDprice());
                            jsonObject.put("downprice", pricing1.getDownprice());
                            jsonObject.put("monthlyprice", pricing1.getMonthlyprice());
                            jsonObject.put("config1", pricing1.getConfig1());
                            jsonObject.put("config2", pricing1.getConfig2());
                            jsonObject.put("config3", pricing1.getConfig3());
                            jsonObject.put("config4", pricing1.getConfig4());
                            jsonArray.put(jsonObject);

                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }

                if (jsonArray.length() > 0)
                    mBinding.webView.loadUrl("javascript:handlePriceDynamically(" + jsonArray + ")");
                promotionSettings();

            }
            catch (Exception e)
            {

            }
            }
                




        };
        mBinding.webView.setWebViewClient(yourWebClient);

    }

    private void promotionSettings() {
        JSONArray elements = new JSONArray();
        try {
            JSONArray promotionsArray = sessionManager.getPromotions();
            if (promotionsArray != null) {
                for (int i = 0; i < promotionsArray.length(); i++) {
                    JSONObject jsonObject1 = new JSONObject();
                    JSONObject promtotionJsonObect=promotionsArray.getJSONObject(i);
                    Iterator<String> keys = promtotionJsonObect.keys();
                    // get some_name_i_wont_know in str_Name
                    String str_Name=keys.next();
                    // get the value i care about
                    String value = promtotionJsonObect.optString(str_Name);
                    if (value.contains(Constraint.PROMOTION)) {
                        File file = new File(value + Constraint.FILE_NAME);
                         File check=new File(value);
                        File mainCheck=new File(value+check.getName()+Constraint.EXTENTION);
                        Log.e("kali", promotionsArray.get(i).toString());
                        if (file.exists()) {
                            jsonObject1.put("promotion" + i, Constraint.PROMOTION + Constraint.SLASH +check.getName() + Constraint.SLASH + Constraint.FILE_NAME);
                            elements.put(jsonObject1);
                        }
                        else if (mainCheck.exists())
                        {
                            jsonObject1.put("promotion" + i, Constraint.PROMOTION + Constraint.SLASH +check.getName() + Constraint.SLASH + check.getName()+Constraint.EXTENTION);
                            elements.put(jsonObject1);

                        }


                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("work in progress",elements.toString());
        if (elements.length()>0)
        mBinding.webView.loadUrl("javascript:handlePrmotion(" + elements + ")");
      //  deleteCard();
    }

    @Override
    public void onFaceDetected() {
         DBCaller.storeLogInDatabase(context, "Face detected", "", "", Constraint.APPLICATION_LOGS);
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
//        if (Utils.getDeviceName().contains(Constraint.PIXEL) || Utils.getDeviceName().contains(getString(R.string.pixel_emulator))) {
//            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 1221);
//        } else {
        Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        if (Utils.getDeviceName().contains(getString(R.string.onePlus))) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        }
        this.startActivityForResult(intent, 9900);
        //}

    }

    private void settingClick() {
        DBCaller.storeLogInDatabase(context, Constraint.SETTINGS, Constraint.SETTINGS_DESCRIPTION, "", Constraint.APPLICATION_LOGS);
        editorToolOpenwithValue();
    }


    private void settingHeader() {
        if (mViewModel.isSettingVisible()) {
            mViewModel.setSettingVisible(false);
            mBinding.setting.setVisibility(View.GONE);
        } else {
            mViewModel.setSettingVisible(true);
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
        Log.e("kali", "back");
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
        // fotoapparatSwitcher.start();
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
       loadURL();
    }
    @Subscribe(threadMode=ThreadMode.MAIN)
    public void updatePromotion(Promotion promotionss)
    {
        i=0;
        Utils.deletePromotion();
        sessionManager.deletePromotions();
        List<Promotion> promotions = sessionManager.getPromotion();

        List<Download> downloads = new ArrayList<>();
        //check if app has permission to write to the external storage.
        if (checkPermission()) {
               for (Promotion promotion : promotions) {
                Download downloadPromotion = new Download();
                downloadPromotion.setPath(promotion.getFileName());
                downloadPromotion.setType(getString(R.string.promotion));
                downloads.add(downloadPromotion);
            }

            new DownloadFile(MainActivity.this, MainActivity.this, downloads).execute();
            sessionManager.setCardDeleted(false);
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
            ;
            if (consoleMessage.message().contains("MobilePriceCard is not defined"))
            {
                Log.e("time","to refresh");
                loadURL();
            }
            Log.e("kali-check", consoleMessage.message());
            return false;
        }


    }


}
