package com.daisy.activity.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.daisy.R;
import com.daisy.common.session.SessionManager;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        sessionManager = SessionManager.get();
        if (sessionManager.getLang() != null)
            setLang(sessionManager.getLang());
        boolean b = sessionManager.getDarkTheme();
        if (b)
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_editor_tool);
    }


    public void setNoTitleBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void setLang(String s) {
        Locale locale = new Locale(s);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        sessionManager.setLang(s);
    }


    public void showHideProgressDialog(boolean iShow) {
        try {
            if (progressDialog != null) {
                if (iShow)
                    progressDialog.show();
                else {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }
            } else {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setCancelable(false);
                showHideProgressDialog(iShow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
