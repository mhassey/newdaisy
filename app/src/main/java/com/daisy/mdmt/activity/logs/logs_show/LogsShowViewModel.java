package com.daisy.mdmt.activity.logs.logs_show;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.daisy.mdmt.pojo.Logs;
import com.daisy.mdmt.pojo.request.LogClearRequest;
import com.daisy.mdmt.pojo.request.LogsRequest;
import com.daisy.mdmt.pojo.response.LogClearResponse;

import java.util.List;

/**
 * Purpose -  LogsShowViewModel is an class that helps to connect view with repository
 * Responsibility - Its helps to create LogsRepository object which help us to store log and return list of logs
 **/
public class LogsShowViewModel extends AndroidViewModel {

    private String type;
    private MutableLiveData<LogsRequest> logMutableLiveData = new MutableLiveData<>();
    private LiveData<List<Logs>> logResponse;
    private LogsRepository logsRepository = new LogsRepository();
    private MutableLiveData<LogClearRequest> logClearRequestMutableLiveData = new MutableLiveData<>();
    private LiveData<LogClearResponse> logClearResponseLiveData;

    public LogsShowViewModel(@NonNull Application application) {
        super(application);
        logResponse = Transformations.switchMap(logMutableLiveData, new Function<LogsRequest, LiveData<List<Logs>>>() {
            @Override
            public LiveData<List<Logs>> apply(LogsRequest input) {
                return logsRepository.getLogs(input);
            }
        });

        logClearResponseLiveData = Transformations.switchMap(logClearRequestMutableLiveData, new Function<LogClearRequest, LiveData<LogClearResponse>>() {
            @Override
            public LiveData<LogClearResponse> apply(LogClearRequest input) {
                return logsRepository.clearLog(input);
            }
        });
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }

    public void setMutableLiveData(LogsRequest request) {
        logMutableLiveData.setValue(request);
    }

    public LiveData<List<Logs>> getLogResponse() {
        return logResponse;
    }

    public void clearLogMutableRequest(LogClearRequest logClearRequest) {
        logClearRequestMutableLiveData.setValue(logClearRequest);

    }

    public LiveData<LogClearResponse> getLogClearResponseLiveData() {
        return logClearResponseLiveData;
    }
}
