package com.daisy.activity.base;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.daisy.R;
import com.daisy.activity.splash.SplashScreen;
import com.daisy.common.session.SessionManager;

public class BaseActivity extends AppCompatActivity {
    private int brightness;
    private ContentResolver cResolver;
    private Window window;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        sessionManager=SessionManager.get();
        boolean b= sessionManager.getDarkTheme();
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

}
