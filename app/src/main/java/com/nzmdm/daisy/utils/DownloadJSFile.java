package com.nzmdm.daisy.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.nzmdm.daisy.R;
import com.nzmdm.daisy.common.session.SessionManager;
import com.nzmdm.daisy.interfaces.CallBack;
import com.nzmdm.daisy.pojo.response.Download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadJSFile extends AsyncTask<String, String, String> {

    private ProgressDialog progressDialog;
    private String fileName;
    private String folder;
    private boolean isDownloaded;
    private Context context;
    private boolean promotion;
    private Download downloads;
    private String path;
    int pathSetting = 0;
    int counter = 0;
    private CallBack callBack;
    private SessionManager sessionManager;


    public DownloadJSFile(CallBack callback, Context c, Download downloads, String path) {
        context = c;
        this.path = path;
        this.callBack = callback;
        sessionManager = SessionManager.get();
        this.downloads = downloads;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog != null && progressDialog.isShowing()) {

        } else {
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setMessage(context.getString(R.string.wait));
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {

            int count;
            String urlPath = null;
            try {
                URL url1 = new URL(downloads.getPath());


                try {
                    HttpURLConnection connectionHttp = (HttpURLConnection) url1.openConnection();
                    int code = connectionHttp.getResponseCode();

                    if (code == 200) {
                        urlPath = downloads.getPath();
                        // reachable
                    } else {
                        urlPath = downloads.getPath1();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    urlPath = downloads.getPath1();


                }

                URL url = new URL(urlPath);
                URLConnection connection = url.openConnection();

                connection.setConnectTimeout(10000);
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat(Constraint.TIME_FORMAT).format(new Date());

                //Extract file name from URL
                fileName = downloads.getPath().substring(downloads.getPath().lastIndexOf('/') + 1);

                folder = path + Constraint.SLASH;

                //Create androiddeft folder if it does not exist
                File directory = new File(context.getExternalFilesDir(""), folder);


                // Output stream to write file
                String path = folder + fileName;

                OutputStream output = new FileOutputStream(path);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }


                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                new Thread(() -> {
                    boolean isDone = new JSExtractor().unpackZip(path, downloads);
                    callBack.callBack(Constraint.SUCCESS);

                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "SUCCESS";
        } else {
            int count;
            String urlPath = null;
            try {
                URL url1 = new URL(downloads.getPath());


                try {
                    HttpURLConnection connectionHttp = (HttpURLConnection) url1.openConnection();
                    int code = connectionHttp.getResponseCode();

                    if (code == 200) {
                        urlPath = downloads.getPath();
                        // reachable
                    } else {
                        urlPath = downloads.getPath1();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    urlPath = downloads.getPath1();


                }


                URL url = new URL(urlPath);
                URLConnection connection = url.openConnection();

                connection.setConnectTimeout(10000);
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat(Constraint.TIME_FORMAT).format(new Date());

                //Extract file name from URL
                fileName = downloads.getPath().substring(downloads.getPath().lastIndexOf('/') + 1);

                folder = path + Constraint.SLASH;


                // Output stream to write file
                String path = folder + fileName;

                OutputStream output = new FileOutputStream(path);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }


                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                new Thread(() -> {
                    boolean isDone = new JSExtractor().unpackZip(path, downloads);
                    callBack.callBack(Constraint.SUCCESS);

                }).start();

            } catch (Exception e) {

            }

            // callBack.callBack(Constraint.SUCCESS);
            return "SUCCESS";
        }

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
            if (progressDialog.isShowing())
                progressDialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

