package com.daisy.overlay;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class OverlayActivity extends AppCompatActivity {

    private static final String TAG = OverlayActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "[onCreate]");
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentType.OVERLAY.getTag());
        if (fragment == null) {
            fragment = OverlayFragment.newInstance();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, fragment, FragmentType.OVERLAY.getTag());
        ft.commit();
    }

    private enum FragmentType {
        OVERLAY("overlay");
        private String tag;

        private FragmentType(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }
}
