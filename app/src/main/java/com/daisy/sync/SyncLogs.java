package com.daisy.sync;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.daisy.apiService.ApiService;
import com.daisy.apiService.AppRetrofit;
import com.daisy.common.session.SessionManager;
import com.daisy.database.DBCaller;
import com.daisy.pojo.Logs;
import com.daisy.pojo.request.LogServerRequest;
import com.daisy.pojo.response.BlankResponse;
import com.daisy.pojo.response.GlobalResponse;
import com.daisy.utils.Constraint;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncLogs {
    private Context context;
    private static SyncLogs contactSyncing;
    public List<Logs> logsVOList;
    private boolean isSyncingInProgress;
    private int MAX_CONTACT_COUNT_FOR_EACH_CALL = 500;
    private int loopCount;
    private SyncLogs(Context context) {
        this.context = context;
        logsVOList = new ArrayList<>();
    }

    public static SyncLogs getLogsSyncing(Context context) {
        if (contactSyncing == null)
            contactSyncing = new SyncLogs(context);
        return contactSyncing;

    }
    public void saveContactApi() {
        if (isSyncingInProgress)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllLogs();
                isSyncingInProgress = true;
                if (logsVOList == null || logsVOList.size() == 0) {
                    isSyncingInProgress = false;
                    return;
                }
                int totalCount = logsVOList.size();
                loopCount = totalCount / MAX_CONTACT_COUNT_FOR_EACH_CALL;
                if (totalCount % MAX_CONTACT_COUNT_FOR_EACH_CALL != 0)
                    loopCount = loopCount + 1;
                callApiToSync(0);
            }
        }).start();
    }


    private void callApiToSync(final int count) {
        ApiService apiService = AppRetrofit.getInstance().getApiService();
        final HashMap<String,String> request = getRequest(count);
        apiService.sendLogs(request,request.get(Constraint.TOKEN)).enqueue(new Callback<GlobalResponse<BlankResponse>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<GlobalResponse<BlankResponse>> call, Response<GlobalResponse<BlankResponse>> response) {
                 if (count >= loopCount - 1) {
                    syncingComplete();
                } else {
                    int cnt = count + 1;
                    callApiToSync(cnt);
                }

            }

            @Override
            public void onFailure(Call<GlobalResponse<BlankResponse>> call, Throwable t) {
            t.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void syncingComplete() {
        // Do what ever you want
        int totalCount = logsVOList.size();
        int counter=totalCount/MAX_CONTACT_COUNT_FOR_EACH_CALL;
        for (int i=0;i<=counter;i++) {
            int startPosition = i * MAX_CONTACT_COUNT_FOR_EACH_CALL;
            int totalcontToSend = (i + 1) * MAX_CONTACT_COUNT_FOR_EACH_CALL;
            if (totalcontToSend > logsVOList.size())
                totalcontToSend = logsVOList.size();
            int countToSend = totalcontToSend - startPosition;
            int toPosition = startPosition + countToSend;
            List<Logs> logsList = new ArrayList<>(logsVOList.subList(startPosition, toPosition));
            List<Integer> integers=  logsList.stream().map(Logs::getId).collect(Collectors.toList());
            new Thread(new Runnable() {
                @Override
                public void run() {

                    DBCaller.setLogData(context, integers);
                }
            }).start();
        }
        isSyncingInProgress=false;
        }

    private HashMap<String,String> getRequest(int count) {
        if (logsVOList == null)
            logsVOList = new ArrayList<>();
        HashMap<String,String> logSyncRequest = new HashMap<>();
        try {
            int startPosition = count * MAX_CONTACT_COUNT_FOR_EACH_CALL;
            int totalcontToSend = (count + 1) * MAX_CONTACT_COUNT_FOR_EACH_CALL;
            if (totalcontToSend > logsVOList.size())
                totalcontToSend = logsVOList.size();
            int countToSend = totalcontToSend - startPosition;
            int toPosition = startPosition + countToSend;
            List<Logs> logs = new ArrayList<>(logsVOList.subList(startPosition, toPosition));
            List<LogServerRequest> requests=new ArrayList<>();
            for (Logs logs1:logs)
            {
                LogServerRequest logServerRequest=new LogServerRequest();
                logServerRequest.setLog(logs1.getEventName());
                logServerRequest.setTimezone(TimeZone.getDefault().getID());
                logServerRequest.setTime(logs1.getEventDateTime());
                requests.add(logServerRequest);
            }
            logSyncRequest.put("log",getJsonObject(requests).toString());
            logSyncRequest.put(Constraint.TOKEN, SessionManager.get().getDeviceToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return logSyncRequest;
    }


    public JsonArray getJsonObject(List<LogServerRequest> logs)
    {

        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(logs, new TypeToken<List<LogServerRequest>>() {}.getType());

        if (! element.isJsonArray() ) {
         }

        JsonArray jsonArray = element.getAsJsonArray();
        return jsonArray;
    }

    private void getAllLogs() {
        logsVOList= DBCaller.getLogsFromDatabaseNotSync(context);
    }


}
