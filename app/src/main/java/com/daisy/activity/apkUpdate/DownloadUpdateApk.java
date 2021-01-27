package com.daisy.activity.apkUpdate;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.daisy.R;
import com.daisy.common.session.SessionManager;
import com.daisy.interfaces.CallBack;
import com.daisy.utils.Constraint;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadUpdateApk extends AsyncTask<String, String, String> {

    private ProgressDialog progressDialog;
    private String fileName;
    private String folder;
    private Context context;
    private CallBack callBack;
    private SessionManager sessionManager;


    public DownloadUpdateApk(Context c, CallBack callBack) {
        context = c;
        sessionManager = SessionManager.get();
        this.callBack = callBack;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.progressDialog.setMessage(Constraint.WAIT);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
      performDoInBackGroundTask(f_url);
        return context.getString(R.string.something_went_wrong);
    }

    private void performDoInBackGroundTask(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[Constraint.ZERO]);
            URLConnection connection = url.openConnection();
            connection.connect();
            int lengthOfFile = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(), Constraint.FILE_SIZE);
            folder = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;
            File directory = new File(folder);

            if (!directory.exists()) {
                directory.mkdirs();
            }
            fileName = Constraint.DAISYAPK;
            String path = folder + fileName;

            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[Constraint.ONE_THOUSAND_TWENTY_FOUR];

            long total = Constraint.ZERO;

            while ((count = input.read(data)) != Constraint.MINUS_ONE) {
                total += count;
                publishProgress("" + (int) ((total * Constraint.HUNDERD) / lengthOfFile));
                output.write(data, Constraint.ZERO, count);
            }

            output.flush();
            output.close();
            input.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        progressDialog.setProgress(Integer.parseInt(progress[0]));
    }


    /**
     * after download zip extract it in following path
     */
    @Override
    protected void onPostExecute(String path) {
        try {
            callBack.callBackApkUpdate(Constraint.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.progressDialog.dismiss();

    }
}