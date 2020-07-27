package com.daisy.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.daisy.common.session.SessionManager;
import com.daisy.interfaces.CallBack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public  class DownloadFile extends AsyncTask<String, String, String> {

    private ProgressDialog progressDialog;
    private String fileName;
    private String folder;
    private boolean isDownloaded;
    private Context context;
    private CallBack callBack;

    public DownloadFile(Context c, CallBack callBack) {
        context = c;
        this.callBack=callBack;
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
            connection.connect();
            // getting file length
            int lengthOfFile = connection.getContentLength();


            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            String timestamp = new SimpleDateFormat(Constraint.TIME_FORMAT).format(new Date());

            //Extract file name from URL
            fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);

            //Append timestamp to file name
            fileName = timestamp + "_" + fileName;

            //External directory path to save file
            folder = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME+Constraint.SLASH+Constraint.CARD+Constraint.SLASH;

            //Create androiddeft folder if it does not exist
            File directory = new File(folder);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Output stream to write file
            String path=folder + fileName;
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

            return path;

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            e.printStackTrace();
        }

        return "Something went wrong";
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
     * */
    @Override
    protected void onPostExecute(String path) {
        // dismiss the dialog after the file was downloaded
        try {

            File file=new File(path);
            SessionManager.get().setLocation(file.getParent());

            new ZipManager().unpackZip(path,callBack);
            } catch (Exception e) {
            e.printStackTrace();
        }
        this.progressDialog.dismiss();

    }
}