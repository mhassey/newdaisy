package com.daisy.activity.logs.logs_show;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daisy.pojo.Logs;
import com.daisy.pojo.request.LogClearRequest;
import com.daisy.pojo.request.LogsRequest;
import com.daisy.pojo.response.LogClearResponse;
import com.daisy.utils.Utils;

import java.util.List;

public class LogsRepository {
    private MutableLiveData<List<Logs>> liveData=new MutableLiveData<>();
    private MutableLiveData<LogClearResponse> logClearResponseMutableLiveData=new MutableLiveData<>();
    public LiveData<List<Logs>> getLogs(LogsRequest input) {

        new LogsShow().execute(input);
        return liveData;
    }

    public LiveData<LogClearResponse> clearLog(LogClearRequest input) {
        new clearLog().execute(input);
        return logClearResponseMutableLiveData;
    }

    class LogsShow extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] objects) {
            LogsRequest logsRequest= (LogsRequest) objects[0];
            List<Logs> logs= Utils.getLogsFromDatabase(logsRequest.getContext(),logsRequest.getType());
            liveData.postValue(logs);
            return null;
        }
    }
    class clearLog extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            LogClearRequest logClearRequest= (LogClearRequest) objects[0];
            boolean b=  Utils.clearLog(logClearRequest);
            LogClearResponse logClearResponse=new LogClearResponse();
            logClearResponse.setClear(b);
            logClearResponseMutableLiveData.postValue(logClearResponse);
            return null;
        }
    }
}
