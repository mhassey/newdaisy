package com.daisy.database;

import android.content.Context;
import android.os.AsyncTask;

import com.daisy.pojo.Logs;
import com.daisy.pojo.request.LogClearRequest;
import com.daisy.utils.Constraint;
import com.daisy.utils.Utils;

import java.util.List;

public class DBCaller {
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
    public static List<Logs> getLogsFromDatabase(Context context, String type) {
        List<Logs> logs = DatabaseClient.getInstance(context).getAppDatabase().logDao().getAll(type, Constraint.FALSE);
        return logs;
    }
    public static void setLogData(Context context,List<Integer> integers) {
        DatabaseClient.getInstance(context).getAppDatabase().logDao().updateItemPlaces(integers);
    }

    public static List<Logs> getLogsFromDatabaseNotSync(Context context) {
        List<Logs> logs = DatabaseClient.getInstance(context).getAppDatabase().logDao().getAllApplicationAndDeviceLog(Constraint.FALSE);
        return logs;
    }
    public static boolean clearLog(LogClearRequest logClearRequest) {
        try {
            DatabaseClient.getInstance(logClearRequest.getContext()).getAppDatabase().logDao().clearLog(Constraint.TRUE, logClearRequest.getType());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
