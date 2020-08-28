package com.daisy.activity.lockscreen;

import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.utils.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityLockScreenBinding;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

public class LockScreen extends BaseActivity implements View.OnClickListener {
    private ActivityLockScreenBinding binding;
    private Context context;
    private String current_running_path;
    private SessionManager sessionManager;
    private boolean comeFromUninstall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNoTitleBar(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen);
        initView();
        initClick();
    }


    private void initView() {
        context = this;
        Constraint.current_running_process = "";
        current_running_path = String.valueOf(getIntent().getStringExtra(Constraint.PACKAGE));
        String uninstall = getIntent().getStringExtra(Constraint.UNINSTALL);
        if (uninstall != null && !uninstall.equals("")) {
            comeFromUninstall = false;
        } else {
            comeFromUninstall = true;
        }
        sessionManager = SessionManager.get();
    }

    private void initClick() {
        binding.unlock.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStop() {
        super.onStop();
       LockScreen.this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unlock: {
                Utils.hideKeyboard(context);
                unlockPassword();
            }
        }
    }

    private void unlockPassword() {
        String password = binding.password.getText().toString();
        if (password != null) {
            if (password.equals(Constraint.PASSWORD)) {
                if (!comeFromUninstall)
                    sessionManager.setUninstall(true);
                finish();
                startLastActivity(current_running_path);

            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_password));

            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.enter_password));
        }
    }

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
            intent.setData(Uri.parse("package:com.daisy"));
            startActivity(intent);
        }
    }

    public void onBackToHome() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

}
