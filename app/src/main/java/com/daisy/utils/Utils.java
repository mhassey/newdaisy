package com.daisy.utils;


import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.inputmethod.InputMethodManager;

import com.daisy.common.Constraint;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
        }
        catch (Exception e)
        {
            return null;
        }
        return  null;
    }
    public static void hideKeyboard(Context mContext) {
        try {
            InputMethodManager inputManager = (InputMethodManager) mContext
                    .getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
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
                File file=new File(url);
                String name=file.getName();
                name=name.replace(Constraint.DOT_ZIP,"");
                return name;
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return  null;
    }
}
