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
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection connection = url.openConnection();
            //connection.setConnectTimeout(5000);
            connection.connect();
            // getting file length
            int lengthOfFile = connection.getContentLength();
            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            folder = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;
            //Create androiddeft folder if it does not exist
            File directory = new File(folder);

            if (!directory.exists()) {
                directory.mkdirs();
            }
            fileName = Constraint.DAISYAPK;
            // Output stream to write file
            String path = folder + fileName;

            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[Constraint.ONE_THOUSAND_TWENTY_FOUR];

            long total = Constraint.ZERO;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * Constraint.HUNDERD) / lengthOfFile));

                // writing data to file
                output.write(data, Constraint.ZERO, count);
            }


            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.getString(R.string.something_went_wrong);
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
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