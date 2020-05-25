package com.daisy.activity.editorTool;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.daisy.R;
import com.daisy.activity.BaseActivity;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.common.Constraint;
import com.daisy.common.session.SessionManager;
import com.daisy.databinding.ActivityEditorToolBinding;
import com.daisy.utils.PermissionManager;
import com.daisy.utils.Utils;
import com.daisy.utils.ValidationHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class EditorTool extends BaseActivity implements View.OnClickListener {
    private ActivityEditorToolBinding mBinding;
    private Context context;
    private SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setOnClickListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        setNoTitleBar(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_editor_tool);
        context = EditorTool.this;
        sessionManager = SessionManager.get();

        PermissionManager.checkPermission(this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);
        checkAndValidate();
        String path = Utils.getPath();
        if (path!=null) {
            mBinding.baseUrl.setText(path);
        }
    }

    private void setOnClickListener() {
        mBinding.saveAndLoad.setOnClickListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
            if (!showRationale) {

            } else {
                // If request is cancelled, the result arrays are empty.
                PermissionManager.checkPermission(EditorTool.this, Constraint.STORAGE_PERMISSION, Constraint.RESPONSE_CODE);
            }
        } else {
            checkAndValidate();
        }
        return;

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
        }
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
                    if (path!=null) {
                        if (!path.equals(mBinding.baseUrl.getText().toString())) {
                            deleteCardFolder();
                            writeFile(configFilePath);
                            sessionManager.deleteLocation();
                        }
                    }
                    else
                    {
                        writeFile(configFilePath);
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

    private void deleteCardFolder() {
        File dir = new File(Environment.getExternalStorageDirectory()+Constraint.SLASH+Constraint.FOLDER_NAME+Constraint.SLASH+Constraint.CARD);
        if (dir.isDirectory())
        {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }




    private void writeFile(String configFilePath) throws IOException {
        File gpxfile = new File(configFilePath, Constraint.configFile);
        FileWriter writer = new FileWriter(gpxfile);
        writer.append(mBinding.baseUrl.getText().toString());
        writer.flush();
        writer.close();
    }
}
