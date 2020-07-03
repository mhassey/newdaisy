package com.daisy.activity.lockscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityLockScreenBinding;
import com.daisy.utils.ValidationHelper;

public class LockScreen extends BaseActivity implements View.OnClickListener {
    private ActivityLockScreenBinding binding;
    private Context context;
    private String current_running_path;
    private SessionManager sessionManager;

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
        Constraint.current_running_process="";
        current_running_path=String.valueOf(getIntent().getStringExtra(Constraint.PACKAGE));
        sessionManager=SessionManager.get();
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
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unlock: {
                unlockPassword();
            }
        }
    }

    private void unlockPassword() {
        String password = binding.password.getText().toString();
        if (password != null) {
            if (password.equals(Constraint.PASSWORD)) {
                if (current_running_path!=null) {
                    finish();
                    startLastActivity(current_running_path);
                }
            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_password));

            }
        } else {
            ValidationHelper.showToast(context, getString(R.string.enter_password));
        }
    }

    private void startLastActivity(String packageName) {
        sessionManager.setPasswordCorrect(true);

        if (packageName.equals("wifi"))
        {
            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),Constraint.RESPONSE_CODE);

        }
        else {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Toast.makeText(LockScreen.this, "There is no package available in android", Toast.LENGTH_LONG).show();
            }
        }
    }


}
