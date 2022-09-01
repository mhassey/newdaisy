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

/**
 * Purpose -  DownloadUpdateApk class help to download apk when any update comes
 * Responsibility - DownloadUpdateApk class works as async and download apk file using url
 **/
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
     * Responsibility - onPreExecute method runs before background thread and show progress dialog in front of screen
     * Parameters - No parameter
     **/
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
     * Responsibility - doInBackground method execute code in background
     * Parameters - String array   here String array is list of url that contains apk
     **/
    @Override
    protected String doInBackground(String... f_url) {
        performDoInBackGroundTask(f_url);
        return context.getString(R.string.something_went_wrong);
    }

    /**
     * Responsibility - performDoInBackGroundTask is an method call from doInBackground method and download apj file using urls
     * Parameters - String array   here String array is list of url that contains apk
     **/
    private void performDoInBackGroundTask(String... f_url) {
        int count;

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
            try {
                URL url = new URL(f_url[Constraint.ZERO]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), Constraint.FILE_SIZE);
                folder = Constraint.FOLDER_NAME + Constraint.SLASH;
                File directory = new File(context.getExternalFilesDir(""), folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }
                fileName = Constraint.DAISYAPK;
                String path = context.getExternalFilesDir("") + "/" + folder + fileName;
                try {
                    File file = new File(context.getExternalFilesDir(""), path);
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception e) {

                }

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
        } else {
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
    }

    /**
     * Responsibility - onProgressUpdate method is used for updating progress value  for example 1%....100%
     * Parameters - String array   here array first value contains how much file downloaded
     **/
    protected void onProgressUpdate(String... progress) {
        progressDialog.setProgress(Integer.parseInt(progress[0]));
    }


    /**
     * Responsibility - onPostExecute method is used when downloading done here we need to extract it in following path
     * Parameters - String value its an value that return from doInBackground method
     **/
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