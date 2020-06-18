package com.daisy.utils;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.daisy.R;
import com.daisy.broadcastforbackgroundservice.AlaramHelperBackground;
import com.daisy.common.Constraint;
import com.daisy.database.DatabaseClient;
import com.daisy.notification.NotificationHelper;
import com.daisy.pojo.Logs;
import com.daisy.pojo.LogsDataPojo;
import com.daisy.pojo.request.LogClearRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utils {

    public static String getPath() {
        try {
            String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;

            File file1 = new File(configFilePath, Constraint.configFile);
            if (file1.exists()) {
                Scanner input = null;
                try {
                    input = new Scanner(file1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                String url = input.next();
                return url;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static void hideKeyboard(Context mContext) {
        try {
            InputMethodManager inputManager = (InputMethodManager) mContext
                    .getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }



    public static boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getFileName() {
        try {
            String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH;

            File file1 = new File(configFilePath, Constraint.configFile);
            if (file1.exists()) {
                Scanner input = null;
                try {
                    input = new Scanner(file1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                String url = input.next();
                File file = new File(url);
                String name = file.getName();
                name = name.replace(Constraint.DOT_ZIP, "");
                return name;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static void storeLogInDatabase(Context context, String eventName, String message, String eventUrl, String logType) {
        Logs logs = new Logs();
        logs.setEventName(eventName);
        logs.setEventDescription(message);
        logs.setEventUrl(eventUrl);
        logs.setLogType(logType);
        logs.setEventDateTime(getTodayDateWithTime());
        logs.setEventTimeStemp(getTimeStemp());
        new AddLog().execute(logs, context);

    }

    public static String getTimeStemp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

    public static boolean storeLogInFile(String fileName, String message) {
        try {
            Scanner input = null;
            String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.SLASH + Constraint.LOGS + Constraint.SLASH + fileName;
            File directory = new File(configFilePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            configFilePath = configFilePath + Constraint.SLASH + Constraint.LOGS + Constraint.TEXT;
            File file1 = new File(configFilePath);
            if (!file1.exists()) {
                file1.createNewFile();

            }
            input = new Scanner(file1);
            String data = "";
            while (input.hasNextLine()) {
                String line = input.nextLine();
                data = data + line;
            }
            data = data + Constraint.SLASH + getTodayDateWithTime() + message;
            writeFileWith(configFilePath, data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<LogsDataPojo> getLogInFile(String fileName) {
        try {
            Scanner input = null;
            String configFilePath = Environment.getExternalStorageDirectory() + File.separator + Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.LOGS + Constraint.SLASH + fileName;
            File directory = new File(configFilePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            configFilePath = configFilePath + Constraint.SLASH + Constraint.LOGS + Constraint.TEXT;
            File file1 = new File(configFilePath);
            if (!file1.exists()) {
                file1.createNewFile();

            }
            input = new Scanner(file1);
            List<LogsDataPojo> logsAdapters = new ArrayList<>();
            while (input.hasNextLine()) {
                String line = input.nextLine();
                String date = "";
                String mainData[] = line.split(Constraint.SLASH);
                for (String data : mainData) {
                    if (!data.equals("")) {

                        LogsDataPojo pojo = new LogsDataPojo();
                        String[] saprate = data.split(Constraint.ADTHERATE);
                        try {
                            String mainDate = saprate[Constraint.ZERO];
                            if (date.equals(mainDate)) {

                            } else {
                                pojo.setHasHeader(true);
                                date = mainDate;
                            }
                            pojo.setDate(mainDate);

                            pojo.setLog(saprate[Constraint.TWO]);
                            pojo.setTime(saprate[Constraint.ONE]);
                            logsAdapters.add(pojo);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return logsAdapters;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getTodayDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    private static String getTodayDateWithTime() {
        Date c = Calendar.getInstance().getTime();
        DateFormat date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String formattedDate = date.format(c);
        return formattedDate;
    }

    public static void writeFile(String configFilePath, String message) throws IOException {
        File gpxfile = new File(configFilePath, Constraint.configFile);
        FileWriter writer = new FileWriter(gpxfile);
        writer.append(message);
        writer.flush();
        writer.close();
    }

    public static void writeFileWith(String configFilePath, String message) throws IOException {
        File gpxfile = new File(configFilePath);
        FileWriter writer = new FileWriter(gpxfile);
        writer.append(message);
        writer.flush();
        writer.close();
    }

    public static List<Logs> getLogsFromDatabase(Context context, String type) {
        List<Logs> logs = DatabaseClient.getInstance(context).getAppDatabase().logDao().getAll(type, Constraint.FALSE);
        return logs;
    }

    public static boolean clearLog(LogClearRequest logClearRequest) {
        try {
            DatabaseClient.getInstance(logClearRequest.getContext()).getAppDatabase().logDao().clearLog(Constraint.TRUE,logClearRequest.getType());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static boolean isMIUI() {
        String device = Build.MANUFACTURER;
        if (device.equals("Xiaomi")) {
            return true;

        }
        return false;
    }

    public static class AddLog extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            Logs logs = (Logs) objects[0];
            Context context = (Context) objects[1];
            DatabaseClient.getInstance(context).getAppDatabase().logDao().insert(logs);
            return null;
        }
    }

    public static void youDesirePermissionCode(Activity context){
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            //do your code
        }  else {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, Constraint.CODE_WRITE_SETTINGS_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, Constraint.CODE_WRITE_SETTINGS_PERMISSION);
            }
        }
    }
    public static long getTime(long hours,long minutes)
    {
        long minutess= TimeUnit.MINUTES.toMillis(minutes);
        long hourss= TimeUnit.HOURS.toMillis(hours);
        long totalmiles=hourss+minutess;
        return totalmiles;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean hasWriteSettingsPermission(Context context) {
        return Settings.System.canWrite(context);
    }

    public static android.app.AlertDialog showAlertDialog(Context mContext, String text, String buttonText, final DialogInterface.OnClickListener clickListener, boolean isCancelable) {
        if (text == null)
            text = "";
        android.app.AlertDialog mAlertDialog = new AlertDialog.Builder(mContext).setMessage(text).
                setTitle(Constraint.PERMISSION_REQUIRED).setCancelable(isCancelable)
                .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (clickListener != null)
                            clickListener.onClick(dialog, which);
                    }
                }).create();

        mAlertDialog.show();
        Button button = mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        return mAlertDialog;
    }
   public static int getThemeId(Context context) {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void constructJob(long timeMiles,Context context) {
        NotificationHelper.scheduleRepeatingRTCNotification(context, timeMiles);
        NotificationHelper.enableBootReceiver(context);
    }

    public static void constructJobForBackground(long timeMiles,Context context) {
        AlaramHelperBackground.scheduleRepeatingRTCNotification(context, timeMiles);
        AlaramHelperBackground.enableBootReceiver(context);
    }





    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }
}


