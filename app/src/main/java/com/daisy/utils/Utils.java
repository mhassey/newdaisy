package com.daisy.utils;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.daisy.R;
import com.daisy.activity.onBoarding.slider.slides.signup.vo.SignUpResponse;
import com.daisy.broadcast.broadcastforbackgroundservice.AlaramHelperBackground;
import com.daisy.common.session.SessionManager;
import com.daisy.pojo.LogsDataPojo;
import com.daisy.pojo.response.LoginResponse;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utils {


    public static void screenBrightness(int level, Context context) {
        try {
            android.provider.Settings.System.putInt(
                    context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, level);
        } catch (Exception e) {
            e.printStackTrace();


        }
    }

    public static String stringify(ArrayList listOfStrings) {
        String result;
        if (listOfStrings.isEmpty()) {
            result = "";
        } else {
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = listOfStrings.iterator();
            sb.append('"').append(it.next()).append('"'); // Not empty
            while (it.hasNext()) {
                sb.append(", \"").append(it.next()).append('"');
            }
            result = sb.toString();
        }
        return result;

    }

    public static boolean isPlugged(Context context) {
        boolean isPlugged = false;
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }
        return isPlugged;
    }


    public static boolean isValidUrl(String urlString) {
        try {
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean getInvertedTime() {
        try {
            SessionManager sessionManager = SessionManager.get();

            int serverTime = Integer.parseInt(getServerTime(sessionManager.getServerTime()));

            int dateTime = Integer.parseInt(getTodayTime());

            int openTime = (((Integer.parseInt(sessionManager.getOpen())) * 100));

            int closeTime = (((Integer.parseInt(sessionManager.getClose())) * 100));

            int offcet = ((Integer.parseInt(sessionManager.getUTCOffset())) * 100);

            int dateTimeInUTC = 0;
            if (offcet < 0) {
                dateTimeInUTC = dateTime + (-offcet);
                openTime = openTime + (-offcet);
                closeTime = closeTime + (-offcet);


            } else {
                dateTimeInUTC = dateTime - offcet;
                openTime = openTime - offcet;
                closeTime = closeTime - offcet;
            }
            int CF;

            if (sessionManager.getTimeInverval() != null && !sessionManager.getTimeInverval().equals("")) {
                CF = Integer.parseInt(sessionManager.getTimeInverval());
            } else {
                CF = serverTime - dateTimeInUTC;

            }


            sessionManager.setTimeInterval(CF + "");
            int LT = dateTimeInUTC + CF;


            if (LT >= openTime && LT < closeTime) {
                return false;
            }
            return true;
        } catch (Exception e) {

        }
        return false;

    }

    public static String getTodayTime() {
//        Date date = localToGMT();
        Calendar rightNow = Calendar.getInstance();
        // rightNow.setTime(date);
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        int currentMinutesIn24Format = rightNow.get(Calendar.MINUTE);
        String currentMinutes = "";
        if (currentMinutesIn24Format <= 9) {
            currentMinutes = "0" + currentMinutesIn24Format;
        } else {
            currentMinutes = currentMinutesIn24Format + "";
        }

        return currentHourIn24Format + "" + currentMinutes;
    }

    public static Date localToGMT() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gmt = new Date(sdf.format(date));
        return gmt;
    }

    public static String getServerTime(String time) {
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            rightNow.setTime(sdf.parse(time));// all done
        } catch (ParseException e) {
        }


        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        int currentMinutesIn24Format = rightNow.get(Calendar.MINUTE);
        String currentMinutes = "";
        if (currentMinutesIn24Format <= 9) {
            currentMinutes = "0" + currentMinutesIn24Format;
        } else {
            currentMinutes = currentMinutesIn24Format + "";
        }
        // return the hour in 24 hrs format (ranging from 0-23)
        return currentHourIn24Format + "" + currentMinutes;
    }

    public static HashMap<String, String> ConvertObjectToMap(Object obj) throws
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException {
        Class<?> pomclass = obj.getClass();
        pomclass = obj.getClass();
        Method[] methods = obj.getClass().getMethods();


        HashMap<String, String> map = new HashMap<>();
        for (Method m : methods) {
            if (m.getName().startsWith("get") && !m.getName().startsWith("getClass")) {
                Object value = (Object) m.invoke(obj);
                map.put(m.getName().substring(3), (String) value);
            }
        }
        return map;
    }

    public static int getMaximumScreenBrightnessSetting() {
        final Resources res = Resources.getSystem();
        int id = res.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");  // API17+
        if (id != 0) {
            try {
                id = res.getInteger(id);
                int val = ((id * 70) / 100);
                if (Utils.getDeviceName().contains("Pixel") || Utils.getDeviceName().contains("pixel")) {
                    val = ((id * 37) / 100);
                }
                return val;
            } catch (Resources.NotFoundException e) {
                // ignore
                e.printStackTrace();
            }
        }
        return 255;
    }

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

    public static void deleteNags(Context context) {
        ContentResolver cr = context.getContentResolver();

        Uri inbox = Uri.parse("content://sms/inbox");
        Cursor cursor = cr.query(
                inbox,
                new String[]{"_id", "thread_id", "body"},
                null,
                null,
                null);

        if (cursor == null)
            return;

        if (!cursor.moveToFirst())
            return;

        int count = 0;

        do {
            String body = cursor.getString(2);
            long thread_id = cursor.getLong(1);
            Uri thread = Uri.parse("content://sms/conversations/" + thread_id);
            cr.delete(thread, null, null);
            count++;
        } while (cursor.moveToNext());
       }

    public static boolean getNetworkState(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isInternetOn(Context context) {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {


            return false;
        }
        return false;
    }

    public static void hideKeyboard(Context mContext) {
        try {
            InputMethodManager inputManager = (InputMethodManager) mContext
                    .getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }


    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getFileName(String configFilePath) {
        try {
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

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getDate(String dateComming) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(dateComming);

        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = format1.format(date);

        return formattedDate;
    }

    public static String getTime(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(time);
        DateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = format1.format(date);
        return formattedDate;
    }

    public static String getTodayDateWithTime() {
        Date c = Calendar.getInstance().getTime();
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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


    private static boolean isMIUI() {
        String device = Build.MANUFACTURER;
        if (device.equals("Xiaomi")) {
            return true;

        }
        return false;
    }


    public static void youDesirePermissionCode(Activity context) {
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            //do your code
        } else {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, Constraint.CODE_WRITE_SETTINGS_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, Constraint.CODE_WRITE_SETTINGS_PERMISSION);
            }
        }
    }

    public static long getTime(long hours, long minutes) {
        long minutess = TimeUnit.MINUTES.toMillis(minutes);
        long hourss = TimeUnit.HOURS.toMillis(hours);
        long totalmiles = hourss + minutess;
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
                setTitle(mContext.getString(R.string.permission_req)).setCancelable(true)
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

    public static String getFileNameFromURL(String url) {
        if (url == null) {
            return "";
        }
        try {
            URL resource = new URL(url);
            String host = resource.getHost();
            if (host.length() > 0 && url.endsWith(host)) {
                // handle ...example.com
                return "";
            }
        } catch (MalformedURLException e) {
            return "";
        }

        int startIndex = url.lastIndexOf('/') + 1;
        int length = url.length();

        // find end index for ?
        int lastQMPos = url.lastIndexOf('?');
        if (lastQMPos == -1) {
            lastQMPos = length;
        }

        // find end index for #
        int lastHashPos = url.lastIndexOf('#');
        if (lastHashPos == -1) {
            lastHashPos = length;
        }

        // calculate the end index
        int endIndex = Math.min(lastQMPos, lastHashPos);
        return url.substring(startIndex, endIndex);
    }

    public static void constructJobForBackground(long timeMiles, Context context) {
        AlaramHelperBackground.scheduleRepeatingRTCNotification(context, timeMiles);
        AlaramHelperBackground.enableBootReceiver(context);
    }


    public static void stopService(long timeMiles, Context context) {
        AlaramHelperBackground.cancelAlarmElapsed();
        AlaramHelperBackground.cancelAlarmRTC();

    }


    /**
     * Returns the consumer friendly device name
     */
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

    public static void deleteCardFolder() {
        File dir = new File(Environment.getExternalStorageDirectory() + Constraint.SLASH + Constraint.FOLDER_NAME + Constraint.SLASH + Constraint.CARD);
        if (dir.isDirectory()) {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void deleteGalaryPhoto() {
        final String path = getGalleryPath();
        File dir = new File(path);
        File[] files = dir.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                try {
                    if (!inFile.getName().contains(Constraint.DAISY))
                        FileUtils.deleteDirectory(inFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                inFile.delete();
            }
        }

    }

    public static void deleteCallList(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);
    }

//    public static boolean deleteSMS(Context context) {
//        boolean isDeleted = false;
//        try {
//            Context mContext = context;
//            Log.e("checking","deleteSMS");
//            mContext.getContentResolver().delete(Uri.parse("content://sms/"), null, null);
//
//            isDeleted = true;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            isDeleted = false;
//        }
//        return isDeleted;
//    }

    public static void deleteSMS(Context context) {
        try {

            Uri uriSms = Uri.parse("content://sms");
            Cursor c = context.getContentResolver().query(uriSms,
                    new String[]{"_id", "thread_id", "address",
                            "person", "date", "body"}, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);

                    int i = context.getContentResolver().delete(Uri.parse("content://sms/"), "_id=? and thread_id=? and address=?", new String[]{String.valueOf(id), String.valueOf(threadId), String.valueOf(address)});
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            }
    }

    private static String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/";
    }

    public static void deleteDownloads() {
        final String path = getDownloadPath();
        File dir = new File(path);
        if (dir.isDirectory()) {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static String getDownloadPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS;

    }

    public static boolean isAccessGranted(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
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

    public static void deleteDaisy() {
        String path = Environment.getExternalStorageDirectory() + Constraint.SLASH + Constraint.DAISY;
        File dir = new File(path);
        if (dir.isDirectory()) {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void deletePromotion() {
        SessionManager sessionManager = SessionManager.get();
        String path = sessionManager.getMainFilePath();
        if (path != null && !path.equals("")) {
            path += Constraint.SLASH + Constraint.PROMOTION;
            File dir = new File(path);
            if (dir.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static void getInvertedTimeWithNewCorrectionFactor() {
        try {
            SessionManager sessionManager = SessionManager.get();

            int serverTime = Integer.parseInt(getServerTime(sessionManager.getServerTime()));

            int dateTime = Integer.parseInt(getTodayTime());


            int offcet = ((Integer.parseInt(sessionManager.getUTCOffset())) * 100);

            int dateTimeInUTC = 0;
            if (offcet < 0) {
                dateTimeInUTC = dateTime + (-offcet);


            } else {
                dateTimeInUTC = dateTime - offcet;
            }
            int CF;
            CF = serverTime - dateTimeInUTC;

            sessionManager.setTimeInterval(CF + "");
        } catch (Exception e) {

        }

    }

    public static int searchPromotionUsingPath(String promotionPath) {
        try {
            SessionManager sessionManager = SessionManager.get();
            JSONArray promotionsArray = sessionManager.getPromotions();
            if (promotionsArray != null) {
                for (int i = 0; i < promotionsArray.length(); i++) {
                    JSONObject promtotionJsonObect = promotionsArray.getJSONObject(i);
                    String value = promtotionJsonObect.getString(Constraint.PROMOTION);
                    File check = new File(value);
                    String pathToCheck= Constraint.PROMOTION + Constraint.SLASH + check.getName() + Constraint.SLASH + check.getName() + Constraint.EXTENTION;
                    if (pathToCheck.equals(promotionPath))
                        {
                            return promtotionJsonObect.getInt(Constraint.PROMOTION_ID);
                        }
                      }
            }
        }
        catch (Exception e)
        {

        }
        return 0;
    }
}


