package com.daisy.overlay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.daisy.R;
import com.daisy.activity.base.BaseFragment;
import com.daisy.activity.mainActivity.MainActivityViewModel;
import com.daisy.database.DBCaller;
import com.daisy.utils.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.FragmentOverlayBinding;
import com.daisy.pojo.response.InternetResponse;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.io.File;

public class OverlayFragment extends BaseFragment {


    private FragmentOverlayBinding mBinding;
    private SessionManager sessionManager;
    private MainActivityViewModel mViewModel;
    private Context context;
    private boolean isRedirected;

    public static OverlayFragment newInstance() {
        OverlayFragment fragment = new OverlayFragment();
        return fragment;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_overlay,container,false);
        setNoTitleBar(getActivity());
        initView();

        checkWifiState();
        return mBinding.getRoot();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
               context = getContext();
         sessionManager = SessionManager.get();
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
            settings.setAppCachePath(context.getFilesDir().getAbsolutePath() + getString(R.string.chche));
            settings.setDatabaseEnabled(true);
            settings.setDatabasePath(context.getFilesDir().getAbsolutePath() + getString(R.string.databse));
            settings.setMediaPlaybackRequiresUserGesture(false);
            mBinding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            mBinding.webView. setWebChromeClient(new WebChromeClientCustomPoster());
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

                }
            }
        } else {

        }
    }

    private void setWebViewClient() {

        mBinding.webView.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView mWebView, int i, String s, String d1) {
                ValidationHelper.showToast(context, getString(R.string.no_internet_available));

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
                if (url.contains(Constraint.FILE)) {
                    DBCaller.storeLogInDatabase(context, Constraint.WEB_PAGE_LOAD, Constraint.WEBPAGE_LOAD_DESCRIPTION, url, Constraint.CARD_LOGS);
                }

            }

            @Override
            public void onPageFinished(WebView view, final String url) {

                super.onPageFinished(view, url);
                if (url.contains(Constraint.FILE)) {
                    DBCaller.storeLogInDatabase(context, Constraint.WEB_PAGE_LOAD_FINISH, Constraint.WEBPAGE_LOAD_FINISH_DESCRIPTION, url, Constraint.CARD_LOGS);
                }


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (!isRedirected) {
                    DBCaller.storeLogInDatabase(context, Constraint.WEB_PAGE_CHANGE, Constraint.WEB_PAGE_CHANGE_DESCRIPTION, url, Constraint.CARD_LOGS);
                    isRedirected=true;
                }
                else
                {
                    isRedirected=false;
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
    private void checkWifiState() {

        WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        InternetResponse internetResponse = new InternetResponse();

        if (wifiManager.isWifiEnabled()) {
            mBinding.offlineLayout.setVisibility(View.GONE);

        } else {
            mBinding.offlineLayout.setVisibility(View.VISIBLE);

        }
    }





}
