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
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityMainBinding;
import com.daisy.interfaces.CallBack;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.utils.CheckForSDCard;
import com.daisy.utils.DownloadFile;
import com.daisy.utils.OnSwipeTouchListener;
import com.daisy.utils.PermissionManager;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity implements CallBack, View.OnClickListener {

    private ActivityMainBinding mBinding;
    private SessionManager sessionManager;
    private MainActivityViewModel mViewModel;
    private Context context;
    private boolean isRedirected;


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
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        sessionManager = SessionManager.get();
        PermissionManager.checkPermission(this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE_MAIN);
        loadURL();
        checkWifiState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkWifiState();
    }

    private void checkWifiState() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            mBinding.offlineLayout.setVisibility(View.GONE);
        } else {
            mBinding.offlineLayout.setVisibility(View.VISIBLE);
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
        if (CheckForSDCard.isSDCardPresent()) {

            //check if app has permission to write to the external storage.
            if (checkPermission()) {
                //Get the URL entered
                final String url = Utils.getPath();
                if (url != null) {
                    new DownloadFile(MainActivity.this, MainActivity.this).execute(url);
                } else {
                    editorToolOpen();
                }
            }
        } else {
            ValidationHelper.showToast(this, getString(R.string.storage_not_available));
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
        loadURL();
    }

    @SuppressLint("JavascriptInterface")
    private void loadURL() {
        if (sessionManager.getLocation() != null && !sessionManager.getLocation().equals("")) {


            WebSettings settings = mBinding.webView.getSettings();
            mBinding.webView.addJavascriptInterface(this, Constraint.ANDROID);
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setLoadWithOverviewMode(true);
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            settings.setLoadsImagesAutomatically(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setPluginState(WebSettings.PluginState.ON);
            settings.setUseWideViewPort(true);
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            settings.setAppCacheEnabled(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            settings.setUseWideViewPort(true);
            settings.setAppCacheEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + getString(R.string.chche));
            settings.setDatabaseEnabled(true);
            settings.setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath() + getString(R.string.databse));
            settings.setMediaPlaybackRequiresUserGesture(false);
            mBinding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            mBinding.webView.setWebChromeClient(new WebChromeClientCustomPoster());
            setWebViewClient();
            File file = new File(sessionManager.getLocation() + Constraint.SLASH + Utils.getFileName() + Constraint.SLASH + Constraint.FILE_NAME);
            if (file.exists()) {
                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + Utils.getFileName() + Constraint.SLASH + Constraint.FILE_NAME);
            } else {
                File file1 = new File(sessionManager.getLocation() + Constraint.SLASH + Constraint.FILE_NAME);
                if (file1.exists())
                    mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + Constraint.FILE_NAME);
                else {
                    sessionManager.deleteLocation();
                    getDownloadData();
                }
            }
        } else {

            getDownloadData();
        }
    }

    private void setWebViewClient() {

        mBinding.webView.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView mWebView, int i, String s, String d1) {
                ValidationHelper.showToast(getApplicationContext(), getString(R.string.no_internet_available));

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
                if (url.contains(Constraint.FILE)) {
                    Utils.storeLogInDatabase(context, Constraint.WEB_PAGE_LOAD, Constraint.WEBPAGE_LOAD_DESCRIPTION, url, Constraint.CARD_LOGS);
                }

            }

            @Override
            public void onPageFinished(WebView view, final String url) {

                super.onPageFinished(view, url);
                if (url.contains(Constraint.FILE)) {
                    Utils.storeLogInDatabase(context, Constraint.WEB_PAGE_LOAD_FINISH, Constraint.WEBPAGE_LOAD_FINISH_DESCRIPTION, url, Constraint.CARD_LOGS);
                }


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (!isRedirected) {
                    Utils.storeLogInDatabase(context, Constraint.WEB_PAGE_CHANGE, Constraint.WEB_PAGE_CHANGE_DESCRIPTION, url, Constraint.CARD_LOGS);
                    isRedirected = true;
                } else {
                    isRedirected = false;
                }
                return true;
            }


        });

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
        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),Constraint.RESPONSE_CODE);
    }

    private void settingClick() {
        Utils.storeLogInDatabase(context, Constraint.SETTINGS, Constraint.SETTINGS_DESCRIPTION, "", Constraint.APPLICATION_LOGS);
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

}
