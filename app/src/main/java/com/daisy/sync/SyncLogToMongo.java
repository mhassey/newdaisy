package com.daisy.sync;

import android.content.Context;

import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.interfaces.SyncLogCallBack;
import com.daisy.pojo.Logs;
import com.daisy.service.MongoConnection;

import java.util.ArrayList;
import java.util.List;

public class SyncLogToMongo {
    private Context context;
    public List<Logs> logsVOList;
    SyncLogCallBack syncLogCallBack;
    SessionManager sessionManager;

    public SyncLogToMongo(Context context) {
        this.context = context;
        logsVOList = new ArrayList<>();
    }

    /**
     * Save logs api
     */
    public void saveContactApi() {
        sessionManager = SessionManager.get();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllLogs();
                callApiToSync(logsVOList);
            }
        }).start();
    }

    private void callApiToSync(List<Logs> logsVOList) {
        new MongoConnection().insertDataToMongo(context,logsVOList);

    }


    /**
     * get All logs from local db
     *
     * @param type
     */
    private void getAllLogs() {
        logsVOList = DBCaller.getAllLogsFromDatabaseNotSync(context);

    }
}
