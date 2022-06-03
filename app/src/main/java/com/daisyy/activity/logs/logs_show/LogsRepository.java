package com.daisyy.activity.logs.logs_show;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisyy.database.DBCaller;
import com.daisyy.pojo.Logs;
import com.daisyy.pojo.request.LogClearRequest;
import com.daisyy.pojo.request.LogsRequest;
import com.daisyy.pojo.response.LogClearResponse;

import java.util.List;

/**
 * Purpose -  LogsRepository is an class that help to connect with db and help to get and push logs in db
 * Responsibility - Main task to get logs from database and pass to view model and save all logs that are coming from view model
 **/
public class LogsRepository {
    private MutableLiveData<List<Logs>> liveData=new MutableLiveData<>();
    private MutableLiveData<LogClearResponse> logClearResponseMutableLiveData=new MutableLiveData<>();


    /**
     * Responsibility - getLogs method is help to get all logs according to request
     * Parameters - It takes LogsRequest that help to determine what type of logs user want to see
     **/
    public LiveData<List<Logs>> getLogs(LogsRequest input) {

        new LogsShow().execute(input);
        return liveData;
    }

    /**
     * Responsibility - clearLog method is use for clearing logs from db according to request
     * Parameters - Its takes LogClearRequest that help to determine which type of logs we need to clear from list
     **/
    public LiveData<LogClearResponse> clearLog(LogClearRequest input) {
        new clearLog().execute(input);
        return logClearResponseMutableLiveData;
    }

    /**
     * Purpose -  LogsShow is an Async class that help to get logs from db and pass to  view
     * Responsibility - Its take an request that help of determine which kind of logs user wants and get data from database and set value in live data
     **/
    class LogsShow extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] objects) {
            LogsRequest logsRequest= (LogsRequest) objects[0];
            List<Logs> logs= DBCaller.getLogsFromDatabase(logsRequest.getContext(),logsRequest.getType());
            liveData.postValue(logs);
            return null;
        }
    }

    /**
     * Purpose -  clearLog is an Async class that help to clear logs from db
     * Responsibility - Its take an request that help of determine which kind of logs user wants and clear data from database
     **/
    class clearLog extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            LogClearRequest logClearRequest= (LogClearRequest) objects[0];
            boolean b=  DBCaller.clearLog(logClearRequest);
            LogClearResponse logClearResponse=new LogClearResponse();
            logClearResponse.setClear(b);
            logClearResponseMutableLiveData.postValue(logClearResponse);
            return null;
        }
    }
}
