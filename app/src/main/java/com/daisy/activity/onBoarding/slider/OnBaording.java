package com.daisy.activity.onBoarding.slider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.editorTool.EditorTool;
import com.daisy.activity.onBoarding.slider.slides.DeviceDetection;
import com.daisy.activity.onBoarding.slider.slides.Login;
import com.daisy.activity.onBoarding.slider.slides.PermissionAsk;
import com.daisy.activity.onBoarding.slider.slides.SecurityAsk;
import com.daisy.adapter.SliderAdapter;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityOnBaordingBinding;
import com.daisy.pojo.response.PermissionDone;
import com.daisy.utils.PermissionManager;
import com.daisy.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class OnBaording extends BaseActivity implements View.OnClickListener {
    private Context context;
    private List<Fragment> fragmentList = new ArrayList<>();
    private int count = 0;
    private SessionManager sessionManager;
    public ActivityOnBaordingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_baording);
        initView();
        initClick();
    }

    private void initClick() {

        mBinding.nextSlide.setOnClickListener(this);
        mBinding.saveAndStartMpc.setOnClickListener(this);

    }


    private void initView() {
        context = this;
        sessionManager = SessionManager.get();
        setNoTitleBar(this);
        stopSwipe();
        mBinding.rootView.setVisibility(View.VISIBLE);
        addFragementList();
        mBinding.pager.setAdapter(new SliderAdapter(getSupportFragmentManager(), 1, fragmentList));
        mBinding.tabDotsLayout.setupWithViewPager(mBinding.pager);

        mBinding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                count = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
        LinearLayout tabStrip = ((LinearLayout)mBinding.tabDotsLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
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

    }



    private void addFragementList() {
        fragmentList.add(PermissionAsk.getInstance(mBinding));
        fragmentList.add(SecurityAsk.getInstance());
        fragmentList.add(Login.getInstance(OnBaording.this));
        fragmentList.add(DeviceDetection.getInstance());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextSlide: {
                nextSlideClickHandler();
                break;
            }
            case R.id.saveAndStartMpc:
            {
                sessionManager.onBoarding(true);
                redirectToMainHandler();
            }
        }
    }

    public void nextSlideClickHandler() {
        count = count + 1;
        if (count == (fragmentList.size())) {
            redirectToMainHandler();
        }
        if (count==2)
        {
         SecurityAsk securityAsk= (SecurityAsk) fragmentList.get(count-1);
           if (securityAsk.securityAskBinding.deletePhoto.isChecked())
           {
            sessionManager.setDeletePhoto(true);
           }
           else
           {
               sessionManager.setDeletePhoto(false);
           }
           if (securityAsk.securityAskBinding.lock.isChecked())
           {
              sessionManager.setLock(true);
           }
           else
           {
               sessionManager.setLock(false);

           }
            mBinding.nextSlide.setVisibility(View.GONE);
        }




        mBinding.pager.setCurrentItem(count);
    }


    public void counterPlus()
    {
        count = count + 1;
        mBinding.pager.setCurrentItem(count);
        if (count==3)
        {
            mBinding.saveAndStartMpcHeader.setVisibility(View.VISIBLE);
        }
    }

    private void redirectToMainHandler() {
        Intent intent = new Intent(OnBaording.this, EditorTool.class);
        startActivity(intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean request = true;
        if (requestCode == Constraint.RESPONSE_CODE) {

            for (int val : grantResults) {
                if (val == PackageManager.PERMISSION_DENIED) {
                    request = false;
                }
            }
        }
        if (request) {
             PermissionDone permissionDone = new PermissionDone();

            permissionDone.setPermissionName(Constraint.MEDIA_PERMISSION);
            EventBus.getDefault().post(permissionDone);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constraint.CODE_WRITE_SETTINGS_PERMISSION) {
            if (Settings.System.canWrite(this)) {
                PermissionDone permissionDone = new PermissionDone();
                permissionDone.setPermissionName(Constraint.MODIFY_SYSTEM_SET);
                EventBus.getDefault().post(permissionDone);
            }
        } else if (requestCode == Constraint.POP_UP_RESPONSE) {
            if (Settings.canDrawOverlays(this)) {
                PermissionDone permissionDone = new PermissionDone();
                permissionDone.setPermissionName(Constraint.DISPLAY_OVER_THE_APP);
                EventBus.getDefault().post(permissionDone);
            }
        }
        else if (requestCode == Constraint.RETURN) {
            if (Utils.isAccessGranted(getApplicationContext())) {
                PermissionDone permissionDone = new PermissionDone();
                permissionDone.setPermissionName(Constraint.GRAND_USAGE_ACCESS);
                EventBus.getDefault().post(permissionDone);
            }
        }
        else if (requestCode == Constraint.BATTRY_OPTIMIZATION_CODE) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

            if (pm.isIgnoringBatteryOptimizations(getString(R.string.packageName))) {
                PermissionDone permissionDone = new PermissionDone();
                permissionDone.setPermissionName(Constraint.BATTRY_OPTI);
                EventBus.getDefault().post(permissionDone);

            }
        }
    }


    private void stopSwipe() {

        mBinding.pager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
    }
}
