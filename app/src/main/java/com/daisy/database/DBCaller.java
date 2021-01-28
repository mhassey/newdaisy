package com.daisy.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.daisy.pojo.Logs;
import com.daisy.pojo.request.LogClearRequest;
import com.daisy.pojo.response.PriceCard;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;

import java.util.Collections;
import java.util.List;

public class DBCaller {

    /**
     * Store Log in local db
     */
    public static void storeLogInDatabase(Context context, String eventName, String message, String eventUrl, String logType) {
        Logs logs = new Logs();
        logs.setEventName(eventName);
        logs.setEventDescription(message);
        logs.setEventUrl(eventUrl);
        logs.setLogType(logType);
        logs.setEventDateTime(Utils.getTodayDateWithTime());
        logs.setEventTimeStemp(Utils.getTimeStemp());
        new AddLog().execute(logs, context);

    }


    /**
     * Get log from local db
     */
    public static List<Logs> getLogsFromDatabase(Context context, String type) {
        List<Logs> logs = DatabaseClient.getInstance(context).getAppDatabase().logDao().getAll(type, Constraint.FALSE);
        return logs;
    }

    /**
     * delete logs according to id
     */
    public static void setLogData(Context context,List<Integer> integers) {
        DatabaseClient.getInstance(context).getAppDatabase().logDao().deleteItemPlaces(integers);
    }


    /**
     * Get not sync logs
     */
    public static List<Logs> getLogsFromDatabaseNotSync(Context context,String type) {
        List<Logs> logs=null;
        if (type.equals(Constraint.PROMOTION))
        {

            logs = DatabaseClient.getInstance(context).getAppDatabase().logDao().getAllPromotionLog(Constraint.FALSE);

        }
        else
        {
            logs = DatabaseClient.getInstance(context).getAppDatabase().logDao().getAllApplicationAndDeviceLog(Constraint.FALSE, type,Constraint.PRICECARD_LOG);
        }
        return logs;
    }

    public static List<Logs> getLogsFromDatabaseNotSyncById(Context context,String type,Integer integer) {
        List<Logs> logs=null;
        if (type.equals(Constraint.PROMOTION))
        {

            logs = DatabaseClient.getInstance(context).getAppDatabase().logDao().getAllPromotionLog(Constraint.FALSE,integer);

        }
        else
        {
            logs = DatabaseClient.getInstance(context).getAppDatabase().logDao().getAllApplicationAndDeviceLog(Constraint.FALSE, type,Constraint.PRICECARD_LOG);
        }
        return logs;
    }

    /**
     * Clear logs
     */
    public static boolean clearLog(LogClearRequest logClearRequest) {
        try {
            DatabaseClient.getInstance(logClearRequest.getContext()).getAppDatabase().logDao().clearLog(Constraint.TRUE, logClearRequest.getType());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Integer> getPromotionCountByID(Context context) {
        try {
          List<Integer>integers = DatabaseClient.getInstance(context).getAppDatabase().logDao().getPromotionCount();
          integers.removeAll(Collections.singleton(0));

            return integers;
        } catch (Exception e) {
            e.printStackTrace();
         }
        return null;

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
}
