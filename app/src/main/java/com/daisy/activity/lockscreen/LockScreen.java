package com.daisy.activity.lockscreen;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.utils.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityLockScreenBinding;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.util.Locale;

public class LockScreen extends BaseActivity implements View.OnClickListener {
    private ActivityLockScreenBinding binding;
    private Context context;
    private String current_running_path;
    private SessionManager sessionManager;
    private boolean comeFromUninstall = false;
    final int sdk = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initClick();
    }

    /**
     * Initial data setup
     */
    private void initView() {
        context = this;
        setNoTitleBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen);
        sessionManager = SessionManager.get();
        extraTaskForMakeAppWorkable();
      }

    /**
     * Do some extra stuff for making app perfect
     */
    private void extraTaskForMakeAppWorkable() {
        Constraint.current_running_process = "";
        current_running_path = String.valueOf(getIntent().getStringExtra(Constraint.PACKAGE));
        String uninstall = getIntent().getStringExtra(Constraint.UNINSTALL);
        if (uninstall != null && !uninstall.equals("")) {
            comeFromUninstall = false;
        } else {
            comeFromUninstall = true;
        }

    }

    /**
     * Button clicks initializing
     */
    private void initClick() {
        binding.unlock.setOnClickListener(this);
        binding.cancel.setOnClickListener(this::onClick);
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * remove lock activity
     */
    @Override
    protected void onStop() {
        super.onStop();
       LockScreen.this.finish();
    }

    /**
     * Handle Clicks listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unlock: {
                Utils.hideKeyboard(context);
                unlockPassword();
                break;
            }
            case R.id.cancel:
            {
                sessionManager.setPasswordCorrect(Constraint.FALSE);

                redirectToMain();
              break;
            }
        }
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

        ViewGroup.LayoutParams params = binding.rootLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.rootLayout.requestLayout();

    }


    /**
     * Redirect to main screen
     */
    private void redirectToMain() {
    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
    startActivity(intent);
    finish();
    }

    /**
     * Handle Unlock work
     */
    private void unlockPassword() {
        String password = binding.password.getText().toString();
        if (password != null) {
            String realPassword=sessionManager.getPasswordLock();
            if (password.equals(realPassword)) {
                if (!comeFromUninstall)
                    sessionManager.setUninstall(Constraint.TRUE);
                finish();
                startLastActivity(current_running_path);

            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_password));

            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.enter_password));
        }
    }

    /**
     * open last activity according to package name
     */
    private void startLastActivity(String packageName) {
        if (comeFromUninstall) {
            sessionManager.setPasswordCorrect(true);
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        } else {

            onBackToHome();
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse(Constraint.DAISY_PACKAGE));
            startActivity(intent);
        }
    }

    /**
     * Back to home
     */
    public void onBackToHome() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
    @Override
    protected void onStart() {
        if (Locale.getDefault().getLanguage().equals(Constraint.AR)) {
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                binding.unlock.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_light_red_rtl) );
            } else {
                binding.unlock.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ovel_light_red_rtl));
            }
        }
        super.onStart();

    }

}
