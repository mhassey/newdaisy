package com.daisy.activity.welcomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.onBoarding.slider.OnBaording;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityWelcomeScreenBinding;
import com.daisy.utils.Constraint;

import java.util.Locale;

public class WelcomeScreen extends BaseActivity implements View.OnClickListener {

    private ActivityWelcomeScreenBinding mBinding;
    private SessionManager sessionManager;
    final int sdk = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome_screen);
        initView();
        initClick();
    }


    /**
     * Initial data setup
     */
    private void initView() {
        sessionManager = SessionManager.get();
        setNoTitleBar(this);

    }

    /**
     * Button clicks initializing
     */
    private void initClick() {
        mBinding.begin.setOnClickListener(this);
    }

    /**
     * Handle Clicks listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begin: {
                goToOnBording();
                break;
            }
        }
    }

    /**
     * Go to on board screen
     */
    private void goToOnBording() {
        Intent intent = new Intent(WelcomeScreen.this, OnBaording.class);
        startActivity(intent);
    }

    /**
     * Change system ui to full screen when any change perform in activity
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }


    }

    /**
     * Handle full screen mode
     */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    @Override
    protected void onStart() {
        if (Locale.getDefault().getLanguage().equals(Constraint.AR)) {
             if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mBinding.curveLayout.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_purple_rtl) );
            } else {
                mBinding.curveLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_purple_rtl));
            }
        }
        super.onStart();

    }


}
