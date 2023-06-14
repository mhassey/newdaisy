package com.allyy.common.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.allyy.utils.Constraint;


public class PreferenceUtil {

    public static final String SHARED_PREF_NAME = Constraint.APPNAME;
    private final SharedPreferences mSpref;
    private final Context context;
    private String TAG = PreferenceUtil.class.getSimpleName();

    public PreferenceUtil(Context context) {
        this.context = context;
        mSpref = this.context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }


    public static float getSharedPrefFloat(Context context, String key) {
        SharedPreferences userAcountPreference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return userAcountPreference.getFloat(key, 0);
    }

    public int getIntData(String key) {
        return mSpref.getInt(key, 0);
    }

    public void setFloatData(Context context, String key, float value) {
         SharedPreferences.Editor appInstallInfoEditor = mSpref.edit();
        appInstallInfoEditor.putFloat(key, value);
        appInstallInfoEditor.commit();
    }

    public void setIntData(String key, int value) {
        SharedPreferences.Editor appInstallInfoEditor = mSpref.edit();
        appInstallInfoEditor.putInt(key, value);
        appInstallInfoEditor.commit();
    }

    public void setStringData(String key, String value) {
        SharedPreferences.Editor appInstallInfoEditor = mSpref.edit();
        appInstallInfoEditor.putString(key, value);
        appInstallInfoEditor.apply();
    }

    public boolean getBoolean(String key) {
        return mSpref.getBoolean(key, false);
    }

    public String getStringData(String key) {
        return mSpref.getString(key, "");

    }

    public String getStringDataFilterCount(String key) {
        return mSpref.getString(key, "0");

    }
    public void removeSession()
    {

        mSpref.edit().clear().commit();
    }

    public void removeStrpe()
    {

        mSpref.edit().remove(PrefConstant.stripeAccountID).commit();
    }
    public void removeApkDetails()
    {

        mSpref.edit().remove(PrefConstant.APK_DETAILS).commit();
    }

    public void removeLocation()
    {

        mSpref.edit().remove(PrefConstant.LOCATION).commit();
    }




    public void setBooleanData(String key, boolean value) {
        SharedPreferences.Editor appInstallInfoEditor = mSpref.edit();
        appInstallInfoEditor.putBoolean(key, value);
        appInstallInfoEditor.apply();
    }

    public long getLongValue(String key) {
        if (mSpref.contains(key))
            return mSpref.getLong(key, 0L);
        else
            Log.e(TAG, "KEY NOT FOUND");

        return 0l;
    }

    public void setLongData(String key, long value) {
        SharedPreferences.Editor editor = mSpref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void clear() {
        mSpref.edit().clear().commit();
    }


    public void removeBaseUrl() {
        mSpref.edit().remove(PrefConstant.BASE_URL);
    }

    public void removePromotions() {
        mSpref.edit().remove(PrefConstant.PROMOTIONS).commit();
    }

    public void removePriceCard() {
        mSpref.edit().remove(PrefConstant.PRICE_CARD).commit();

    }
}
