package com.daisy.activity.logs.logs_show;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.database.DBCaller;
import com.daisy.pojo.Logs;
import com.daisy.pojo.request.LogClearRequest;
import com.daisy.pojo.request.LogsRequest;
import com.daisy.pojo.response.LogClearResponse;
import com.daisy.utils.Utils;

import java.util.List;

public class LogsRepository {
    private MutableLiveData<List<Logs>> liveData=new MutableLiveData<>();
    private MutableLiveData<LogClearResponse> logClearResponseMutableLiveData=new MutableLiveData<>();

    //TODO getLogs method is use for get all logs from db
    public LiveData<List<Logs>> getLogs(LogsRequest input) {

        new LogsShow().execute(input);
        return liveData;
    }

    // TODO Clear log method is use for clearing logs from db
    public LiveData<LogClearResponse> clearLog(LogClearRequest input) {
        new clearLog().execute(input);
        return logClearResponseMutableLiveData;
    }

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
