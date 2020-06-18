package com.daisy.activity.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.daisy.Daisy;
import com.daisy.R;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.notification.NotificationHelper;
import com.daisy.service.StickyService;
import com.daisy.utils.Utils;

import java.util.concurrent.TimeUnit;

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
