package com.daisy.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.daisy.R;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.common.session.SessionManager;
import com.daisy.interfaces.CallBack;
import com.daisy.pojo.response.Download;
import com.daisy.pojo.response.DownloadFail;
import com.daisy.pojo.response.Url;

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
import java.util.concurrent.atomic.AtomicInteger;

public  class DownloadFile extends AsyncTask<String, String, String> {

    private ProgressDialog progressDialog;
    private String fileName;
    private String folder;
    private boolean isDownloaded;
    private Context context;
    private CallBack callBack;
    private  boolean promotion;
    private List<Download> downloads;
    int pathSetting=0;
    int counter=0;
    private SessionManager sessionManager;


    public DownloadFile(Context c, CallBack callBack,List<Download> downloads) {
        context = c;
        this.callBack=callBack;
        sessionManager=SessionManager.get();
        this.downloads=downloads;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog!=null && progressDialog.isShowing())
        {

        }
        else {
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setMessage(Constraint.WAIT);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        for (Download download:downloads) {
            int count;
            String urlPath = null;
            try {
                URL url1 = new URL(download.getPath());

                HttpURLConnection connectionHttp = (HttpURLConnection) url1.openConnection();
                try {
                    int code = connectionHttp.getResponseCode();

                    if (code == 200) {
                        urlPath=download.getPath();
                        // reachable
                    } else {
                        urlPath=download.getPath1();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    urlPath=download.getPath1();


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
                    String path=Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.CARD;
                    File f = new File(path);
                    String file[] = f.list();

                    folder = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.CARD + Constraint.SLASH+file[1]+Constraint.SLASH+context.getString(R.string.promotion)+Constraint.SLASH;

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
                File file=new File(path);
                if (pathSetting==0) {
                    pathSetting++;
                    if (!file.getAbsolutePath().contains(Constraint.PROMOTION))
                        SessionManager.get().setLocation(file.getParent());
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isDone=  new ZipManager().unpackZip(path,download);
                        if (isDone)
                        {
                            counter++;
                        }
                        if (counter==downloads.size())
                        {
                            callBack.callBack(Constraint.SUCCESS);

                        }
                    }
                }).start();

            } catch (Exception e) {
                if (download.getType().equals(""))
                {
                    EventBus.getDefault().post(new DownloadFail());
                }
                e.printStackTrace();
            }
        }
        // callBack.callBack(Constraint.SUCCESS);
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
         try {
             this.progressDialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}