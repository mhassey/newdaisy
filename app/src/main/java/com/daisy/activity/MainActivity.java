package com.daisy.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.daisy.R;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityMainBinding;
import com.daisy.interfaces.CallBack;
import com.daisy.utils.CheckForSDCard;
import com.daisy.utils.DownloadFile;
import com.daisy.utils.PermissionManager;

import java.io.File;

public class MainActivity extends AppCompatActivity implements CallBack {

    private ActivityMainBinding mBinding;
    private SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setNoTitleBar();
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, (R.layout.activity_main));
        initView();
    }

    private void setNoTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();
        sessionManager=SessionManager.get();
        PermissionManager.checkPermission(this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);
        loadURL();
    }

    private void getDownloadData() {
        if (CheckForSDCard.isSDCardPresent()) {

            //check if app has permission to write to the external storage.
            if (checkPermission()) {
                //Get the URL entered
                new DownloadFile(this,this).execute(Constraint.zipUrl+Constraint.serverFileName+Constraint.serverFileExtention);
            } else {

            }


        } else {
            Toast.makeText(getApplicationContext(),
                    R.string.storage_not_available, Toast.LENGTH_LONG).show();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
            if (!showRationale) {

            } else {
                // If request is cancelled, the result arrays are empty.
            PermissionManager.checkPermission(MainActivity.this,Constraint.STORAGE_PERMISSION,Constraint.RESPONSE_CODE);
            }
        }
        else {
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
        if (sessionManager.getLocation()!=null && !sessionManager.getLocation().equals("")) {
            WebSettings settings = mBinding.webView.getSettings();
            mBinding.webView.addJavascriptInterface(this, Constraint.ANDROID);
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setLoadWithOverviewMode(true);
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setUseWideViewPort(true);
            mBinding.webView.setWebChromeClient(new WebChromeClient(){
            });
            File file=new File(sessionManager.getLocation()+ Constraint.SLASH +Constraint.serverFileName+Constraint.SLASH+ Constraint.FILE_NAME);
            if (file.exists()) {
                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + Constraint.serverFileName + Constraint.SLASH + Constraint.FILE_NAME);
            }else
            {
                mBinding.webView.loadUrl(Constraint.FILE + sessionManager.getLocation() + Constraint.SLASH + Constraint.FILE_NAME);

            }
            }
        else
        {
            getDownloadData();
        }
    }
}
