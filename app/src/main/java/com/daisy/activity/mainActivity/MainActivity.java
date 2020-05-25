package com.daisy.activity.mainActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.daisy.R;
import com.daisy.activity.BaseActivity;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityMainBinding;
import com.daisy.interfaces.CallBack;
import com.daisy.utils.CheckForSDCard;
import com.daisy.utils.DownloadFile;
import com.daisy.utils.OnSwipeTouchListener;
import com.daisy.utils.PermissionManager;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.io.File;

public class MainActivity extends BaseActivity implements CallBack, View.OnClickListener {

    private ActivityMainBinding mBinding;
    private SessionManager sessionManager;
    private MainActivityViewModel mViewModel;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setOnClickListener();
    }

    private void setOnClickListener() {
    mBinding.settingHeader.setOnClickListener(this);
    mBinding.setting.setOnClickListener(this);


        mBinding.swipeclick.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                settingHeader();
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        setNoTitleBar(this);
        mBinding = DataBindingUtil.setContentView(this, (R.layout.activity_main));
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        sessionManager = SessionManager.get();
        PermissionManager.checkPermission(this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE_MAIN);
          loadURL();
    }




    private void getDownloadData() {
        if (CheckForSDCard.isSDCardPresent()) {

            //check if app has permission to write to the external storage.
            if (checkPermission()) {
                //Get the URL entered
                final String url = Utils.getPath();
                if (url != null) {
                    new DownloadFile(MainActivity.this, MainActivity.this).execute(url);
                }
                else
                {
                    editorToolOpen();
                }
            }
        } else {
            ValidationHelper.showToast(this,getString(R.string.storage_not_available));
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


    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setUseWideViewPort(true);
            mBinding.webView.setWebChromeClient(new WebChromeClient() {
            });
            File file = new File(sessionManager.getLocation() + Constraint.SLASH + Utils.getFileName() + Constraint.SLASH + Constraint.FILE_NAME);
            if (file.exists()) {
                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + Utils.getFileName() + Constraint.SLASH + Constraint.FILE_NAME);
            } else {
                File file1=new File(sessionManager.getLocation() + Constraint.SLASH + Constraint.FILE_NAME);
                if (file1.exists())
                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + Constraint.FILE_NAME);
                else
                {
                    sessionManager.deleteLocation();
                    getDownloadData();
                }
            }
        } else {

            getDownloadData();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.settingHeader:
            {
                settingHeader();
                break;
            }
            case R.id.setting:
            {
                settingClick();
                break;
            }
        }
    }

    private void settingClick() {
        editorToolOpenwithValue();
    }



    private void settingHeader() {
        if (mViewModel.isSettingVisible())
        {
            mViewModel.setSettingVisible(false);
            mBinding.setting.setVisibility(View.GONE);
        }
        else
        {
            mViewModel.setSettingVisible(true);
            mBinding.setting.setVisibility(View.VISIBLE);
            hideSettingsIcon();
        }
    }
    private void editorToolOpen() {
        Intent intent = new Intent(MainActivity.this, EditorTool.class);
        startActivity(intent);
        finish();
    }
    private void editorToolOpenwithValue() {
        Intent intent = new Intent(MainActivity.this, EditorTool.class);
        Bundle bundle=new Bundle();
        bundle.putString(Constraint.CALLFROM,Constraint.SETTINGS);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    void hideSettingsIcon()
    {
        Runnable mRunnable;
        Handler mHandler=new Handler();
        mRunnable=new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mBinding.setting.getVisibility() == View.VISIBLE) {
                    mBinding.setting.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
                }
            }
        };
        mHandler.postDelayed(mRunnable,Constraint.TWENTY*Constraint.THOUSAND);
    }


}
