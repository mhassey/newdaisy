package com.android_tv.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.android_tv.R;
import com.android_tv.common.session.SessionManager;
import com.android_tv.interfaces.CallBack;
import com.android_tv.pojo.response.Download;
import com.android_tv.pojo.response.DownloadFail;

import org.greenrobot.eventbus.EventBus;

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
import java.util.List;


public class DownloadFile extends AsyncTask<String, String, String> {

    private ProgressDialog progressDialog;
    private String fileName;
    private String folder;
    private boolean isDownloaded;
    private Context context;
    private CallBack callBack;
    private boolean promotion;
    private List<Download> downloads;
    int pathSetting = 0;
    int counter = 0;
    private SessionManager sessionManager;


    public DownloadFile(Context c, CallBack callBack, List<Download> downloads) {
        context = c;
        this.callBack = callBack;
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
            for (Download download : downloads) {
                int count;
                String urlPath = null;
                try {
                    URL url1 = new URL(download.getPath());


                    try {
                        HttpURLConnection connectionHttp = (HttpURLConnection) url1.openConnection();
                        int code = connectionHttp.getResponseCode();

                        if (code == 200) {
                            urlPath = download.getPath();
                            // reachable
                        } else {
                            urlPath = download.getPath1();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        urlPath = download.getPath1();


                    }


                    if (download.getType().equals(Constraint.PROMOTION)) {
                        promotion = true;
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
                    fileName = download.getPath().substring(download.getPath().lastIndexOf('/') + 1);

                    //Append timestamp to file name
                    //   fileName = timestamp + "_" + fileName;

                    //External directory path to save file
                    if (promotion) {
                        String path = Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.CARD;
                        File f = new File(context.getExternalFilesDir(""), path);
                        String file[] = f.list();
                        String value = file[1];
                        if (value.contains(Constraint.DOT_ZIP)) {
                            value = file[0];
                        }

                        folder = Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.CARD + Constraint.SLASH + value + Constraint.SLASH + Constraint.PROMOTION + Constraint.SLASH;

                    } else
                        folder = Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.CARD + Constraint.SLASH;

                    //Create androiddeft folder if it does not exist
                    File directory = new File(context.getExternalFilesDir(""), folder);

                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    // Output stream to write file
                    String path = context.getExternalFilesDir("") + "/" + folder + fileName;

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
                    File file = new File(path);
                    if (pathSetting == 0) {
                        pathSetting++;
                        if (!file.getAbsolutePath().contains(Constraint.PROMOTION))
                            SessionManager.get().setLocation(file.getParent());
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isDone = new ZipManager().unpackZip(path, download);
                            if (isDone) {
                                counter++;
                            }
                            if (counter == downloads.size()) {
                                callBack.callBack(Constraint.SUCCESS);

                            }
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (download.getType().equals("")) {

                        return Constraint.SOMETHING_WENT_WRONG;
                    }
                }

            }
            return "SUCCESS";
        } else {
            for (Download download : downloads) {
                int count;
                String urlPath = null;
                try {
                    URL url1 = new URL(download.getPath());


                    try {
                        HttpURLConnection connectionHttp = (HttpURLConnection) url1.openConnection();
                        int code = connectionHttp.getResponseCode();

                        if (code == 200) {
                            urlPath = download.getPath();
                            // reachable
                        } else {
                            urlPath = download.getPath1();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        urlPath = download.getPath1();


                    }


                    if (download.getType().equals(Constraint.PROMOTION)) {
                        promotion = true;
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
                    fileName = download.getPath().substring(download.getPath().lastIndexOf('/') + 1);

                    //Append timestamp to file name
                    //   fileName = timestamp + "_" + fileName;

                    //External directory path to save file
                    if (promotion) {
                        String path = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.CARD;
                        File f = new File(path);
                        String file[] = f.list();
                        String value = file[1];
                        if (value.contains(Constraint.DOT_ZIP)) {
                            value = file[0];
                        }

                        folder = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.CARD + Constraint.SLASH + value + Constraint.SLASH + Constraint.PROMOTION + Constraint.SLASH;

                    } else
                        folder = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.CARD + Constraint.SLASH;

                    //Create androiddeft folder if it does not exist
                    File directory = new File(folder);

                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

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
                    File file = new File(path);
                    if (pathSetting == 0) {
                        pathSetting++;
                        if (!file.getAbsolutePath().contains(Constraint.PROMOTION))
                            SessionManager.get().setLocation(file.getParent());
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isDone = new ZipManager().unpackZip(path, download);
                            if (isDone) {
                                counter++;
                            }
                            if (counter == downloads.size()) {
                                callBack.callBack(Constraint.SUCCESS);

                            }
                        }
                    }).start();

                } catch (Exception e) {
                    if (download.getType().equals("")) {

                        return Constraint.SOMETHING_WENT_WRONG;
                    }
                }

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
            if (!path.equals(Constraint.SOMETHING_WENT_WRONG)) {
                callBack.callBack(Constraint.SUCCESS);
            } else {

                EventBus.getDefault().post(new DownloadFail());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

