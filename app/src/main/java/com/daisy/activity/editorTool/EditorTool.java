package com.daisy.activity.editorTool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.daisy.R;
import com.daisy.activity.base.BaseActivity;
import com.daisy.activity.configSettings.ConfigSettings;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.databinding.ActivityEditorToolBinding;
import com.daisy.service.StickyService;
import com.daisy.utils.Constraint;
import com.daisy.utils.PermissionManager;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

// Please ignore this class its an junk code
public class EditorTool extends BaseActivity implements View.OnClickListener {
    private ActivityEditorToolBinding mBinding;
    private Context context;
    private SessionManager sessionManager;
    private boolean isSettings = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startServices();
        initView();
        onAttachedToWindow();
        setThemeChanges();
        setOnClickListener();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        setNoTitleBar(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_editor_tool);
        context = EditorTool.this;
        sessionManager = SessionManager.get();
        Constraint.IS_OVER_APP_SETTING = true;
        handleBandleData();
            try {
                String path = Utils.getPath();

                if (path != null) {
                    mBinding.baseUrl.setText(path);
                }
            }
            catch (Exception e)
            {

            }
    }

    private void startServices() {
        startService(new Intent(getBaseContext(), StickyService.class));
    }


    public void onAttachedToWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void batteryUsage() {
        final PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(getString(R.string.packageName))) {
            Utils.showAlertDialog(context, getString(R.string.battery_optimized), "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String packageName = getPackageName();
                        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                            startActivityForResult(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS), Constraint.BATTRY_OPTIMIZATION_CODE);
                        }
                    }
                }
            }, false);

        }
    }

    private void setThemeChanges() {
        if (Utils.getThemeId(context) == R.style.AppThemeDark) {
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mBinding.baseUrl.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_text_format_white));
            } else {
                mBinding.baseUrl.setBackground(ContextCompat.getDrawable(context, R.drawable.edit_text_format_white));
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)

    private void setOnClickListener() {
        mBinding.saveAndLoad.setOnClickListener(this::onClick);
        mBinding.configSettings.setOnClickListener(this::onClick);
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
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ViewGroup.LayoutParams params = mBinding.rootLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mBinding.rootLayout.requestLayout();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == Constraint.RESPONSE_CODE) {
            if (grantResults[Constraint.ZERO] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = shouldShowRequestPermissionRationale(permissions[Constraint.ZERO]);
                if (!showRationale) {

                } else {
                    // If request is cancelled, the result arrays are empty.
                    boolean b = PermissionManager.checkPermission(EditorTool.this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);
                    if (!b) {
                        askForExternalPermission();
                    }
                }
            }

        }


        return;

    }


    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        handleOnActivtyResult(requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handleOnActivtyResult(int requestCode) {
        if (requestCode == Constraint.CODE_WRITE_SETTINGS_PERMISSION) {
            if (Settings.System.canWrite(context)) {
                String name = Utils.getDeviceName();
                if (name.contains(getString(R.string.onePlus))) {
                    batteryUsage();
                } else {
                    checkAndValidate();
                }
            } else modifySystemSettings();
        } else if (requestCode == Constraint.POP_UP_RESPONSE) {
            if (!Settings.canDrawOverlays(context)) {
                askForPopUpPermission();
            } else
                callUsageAccessSettings();
        } else if (requestCode == Constraint.RETURN) {
            if (!isAccessGranted()) {
                callUsageAccessSettings();
            } else {
                sessionManager.setModifySystemSettings(true);
                modifySystemSettings();
            }
        } else if (requestCode == Constraint.BATTRY_OPTIMIZATION_CODE) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

            if (pm.isIgnoringBatteryOptimizations(getString(R.string.packageName))) {
                checkAndValidate();
            } else {
                batteryUsage();
            }
        }
    }


    private void modifySystemSettings() {
        Utils.showAlertDialog(context, getString(R.string.modify_system_settings_text), "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.youDesirePermissionCode(EditorTool.this);
            }
        }, false);


    }

    private void callUsageAccessSettings() {
        Utils.showAlertDialog(context, getString(R.string.allow_data_access), "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intent, Constraint.RETURN);
            }
        }, false);


    }


    private void checkAndValidate() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String value = bundle.getString(Constraint.CALLFROM);

            if (value != null) {
                if (!value.equals(Constraint.SETTINGS)) {
                    String url = Utils.getPath();
                    if (url != null) {
                        redirectToMain();
                    }
                }
            } else {
                redirectToMain();
            }
        } else {
            String url = Utils.getPath();
            if (url != null) {
                redirectToMain();
            }
        }
    }


    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(context);
        switch (v.getId()) {
            case R.id.saveAndLoad: {
                saveAndLoad();
                break;
            }
            case R.id.configSettings: {
                openConfigSettings();
                break;
            }

        }
    }

    private void openConfigSettings() {


        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.password_layout, null);
        final EditText password = alertLayout.findViewById(R.id.password);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.lock));
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setPositiveButton(R.string.unlockk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passwordString = password.getText().toString();
                String lockPassword=sessionManager.getPasswordLock();
                if (passwordString.equals(lockPassword)) {
                    dialog.dismiss();
                    Intent intent = new Intent(EditorTool.this, ConfigSettings.class);
                    startActivity(intent);
                } else {
                    ValidationHelper.showToast(context, getString(R.string.invalid_password));
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();

    }


    private void saveAndLoad() {
        try {
            new CheckAvailability().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String checkAndLoadUrl() throws IOException {
        String requestUrl = mBinding.baseUrl.getText().toString();
        URL url = new URL(requestUrl);
        URLConnection c = url.openConnection();
        String contentType = c.getContentType();
        return contentType;
    }

    private void redirectToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    class CheckAvailability extends AsyncTask {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditorTool.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String type = checkAndLoadUrl();
                return type;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                performAction(o);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void performAction(Object o) throws IOException {
            String contentType = (String) o;
            progressDialog.dismiss();
            if (contentType != null) {

                if (contentType.equals(Constraint.TYPE)) {
                    String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;
                    File directory = new File(configFilePath);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    String path = Utils.getPath();
                    if (path != null) {
                        if (!path.equals(mBinding.baseUrl.getText().toString())) {
                            Utils.deleteCardFolder();
                            Utils.writeFile(configFilePath, mBinding.baseUrl.getText().toString());
                            sessionManager.deleteLocation();
                            DBCaller.storeLogInDatabase(context, Constraint.CHANGE_BASE_URL, Constraint.CHANGE_BASE_URL_DESCRIPTION, mBinding.baseUrl.getText().toString(), Constraint.APPLICATION_LOGS);

                        }
                    } else {
                        Utils.writeFile(configFilePath, mBinding.baseUrl.getText().toString());
                    }

                    redirectToMain();
                } else {
                    ValidationHelper.showToast(context, getString(R.string.invalid_url));
                }
            } else {
                ValidationHelper.showToast(context, getString(R.string.invalid_url));
            }
        }
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean askForPopUpPermission() {

        if (!Settings.canDrawOverlays(this)) {
            Utils.showAlertDialog(context, getString(R.string.display_over_the_app), "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, Constraint.POP_UP_RESPONSE);
                }
            }, false);

            return true;
        } else {
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionWork() {
        checkAndValidate();
    }


    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForExternalPermission() {
        if (askForPopUpPermission()) {

        } else if (!isAccessGranted()) {
            callUsageAccessSettings();
        } else if (!Settings.System.canWrite(this)) {
            modifySystemSettings();
        } else
            checkAndValidate();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handleBandleData() {
        String setting = getIntent().getStringExtra(Constraint.SETTINGS);
        if (setting != null && setting.equals(Constraint.SETTINGS)) {
            isSettings = true;
        } else {
            permissionWork();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isSettings) {
            onBackToHome();

        } else {

        }
    }

    public void onBackToHome() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


}
