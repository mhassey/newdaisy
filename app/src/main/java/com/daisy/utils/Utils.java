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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.daisy.R;
import com.daisy.broadcast.broadcastforbackgroundservice.AlaramHelperBackground;
import com.daisy.pojo.LogsDataPojo;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utils {


    public static void screenBrightness(int level,Context context) {
        try {
            android.provider.Settings.System.putInt(
                    context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, level);
        } catch (Exception e) {
            e.printStackTrace();


        }
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
                id= res.getInteger(id);
                int val=((id*70)/100);
                if (Utils.getDeviceName().contains("Pixel") || Utils.getDeviceName().contains("pixel"))
                {
                    val=((id*37)/100);
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

        Uri inbox = Uri.parse( "content://sms/inbox" );
        Cursor cursor = cr.query(
                inbox,
                new String[] { "_id", "thread_id", "body" },
                null,
                null,
                null);

        if (cursor == null)
            return;

        if (!cursor.moveToFirst())
            return;

        int count = 0;

        do {
            String body = cursor.getString( 2 );
            Log.e("kalo",body);
            long thread_id = cursor.getLong( 1 );
            Uri thread = Uri.parse( "content://sms/conversations/" + thread_id );
            cr.delete( thread, null, null );
            count++;
        } while ( cursor.moveToNext() );
        Log.e( "Deleted: " , count+"" );
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
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getTodayDateWithTime() {
        Date c = Calendar.getInstance().getTime();
        DateFormat date = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
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
                setTitle(Constraint.PERMISSION_REQUIRED).setCancelable(true)
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


    public static void constructJobForBackground(long timeMiles, Context context) {
        AlaramHelperBackground.scheduleRepeatingRTCNotification(context, timeMiles);
        AlaramHelperBackground.enableBootReceiver(context);
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
            }
            else
            {
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

                    Log.e("Deleting SMS with id: ", threadId + "");
                 int i=   context.getContentResolver().delete(Uri.parse("content://sms/"),"_id=? and thread_id=? and address=?",new String[] { String.valueOf(id),String.valueOf(threadId),String.valueOf(address) });
                 } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Could not delete SMS", e.getMessage());
        }
    }

    private static String getGalleryPath() {
        return Environment.getExternalStorageDirectory()+"/";
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
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS ;

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
}


