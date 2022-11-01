package com.iris.sync;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.iris.apiService.ApiService;
import com.iris.apiService.AppRetrofit;
import com.iris.common.session.SessionManager;
import com.iris.database.DBCaller;
import com.iris.interfaces.SyncLogCallBack;
import com.iris.pojo.Logs;
import com.iris.pojo.request.LogServerRequest;
import com.iris.pojo.response.BlankResponse;
import com.iris.pojo.response.GlobalResponse;
import com.iris.utils.Constraint;
import com.iris.utils.Utils;
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

/**
 * SyncLogs is an class that helps to send logs to server in background
 */
public class SyncLogs {
    private Context context;
    private static SyncLogs contactSyncing;
    public List<Logs> logsVOList;
    private boolean isSyncingInProgress;
    private int MAX_CONTACT_COUNT_FOR_EACH_CALL = Constraint.MAX_SYNC;
    private int loopCount;
    private SessionManager sessionManager;
    SyncLogCallBack syncLogCallBack;

    private SyncLogs(Context context) {
        this.context = context;
        logsVOList = new ArrayList<>();
    }


    public static SyncLogs getLogsSyncing(Context context) {
        if (contactSyncing == null)
            contactSyncing = new SyncLogs(context);
        return contactSyncing;

    }

    /**
     * Save logs api
     */
    public void saveContactApi(String type,SyncLogCallBack syncLogCallBack) {
        sessionManager = SessionManager.get();
        this.syncLogCallBack=syncLogCallBack;
        if (isSyncingInProgress)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllLogs(type);
                isSyncingInProgress = true;
                if (logsVOList == null || logsVOList.size() == Constraint.ZERO) {
                    isSyncingInProgress = false;
                    return;
                }
                int totalCount = logsVOList.size();
                loopCount = totalCount / MAX_CONTACT_COUNT_FOR_EACH_CALL;
                if (totalCount % MAX_CONTACT_COUNT_FOR_EACH_CALL != Constraint.ZERO)
                    loopCount = loopCount + Constraint.ONE;
                if (type.equals(Constraint.PROMOTION))
                {

                }
                else
                callApiToSync(Constraint.ZERO,type,Constraint.ZERO);
            }
        }).start();
    }

    /**
     * Save logs api
     */
    public void saveContactApi(String type,Integer integer) {
        sessionManager = SessionManager.get();
        if (isSyncingInProgress)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllLogsBasedOnId(type,integer);
                isSyncingInProgress = true;
                if (logsVOList == null || logsVOList.size() == Constraint.ZERO) {
                    isSyncingInProgress = false;
                    return;
                }
                int totalCount = logsVOList.size();
                loopCount = totalCount / MAX_CONTACT_COUNT_FOR_EACH_CALL;
                if (totalCount % MAX_CONTACT_COUNT_FOR_EACH_CALL != Constraint.ZERO)
                    loopCount = loopCount + Constraint.ONE;
                    callApiToSync(Constraint.ZERO,type,integer);
            }
        }).start();
    }


    /**
     * call api to sync
     */
    private void callApiToSync(final int count,String type,int id) {
        ApiService apiService = AppRetrofit.getInstance().getApiService();
        final HashMap<String, String> request = getRequest(count);
        if (type.equals(Constraint.APPLICATION_LOGS))
        {
            if (sessionManager.getPriceCard()!=null)
            request.put(Constraint.ID_PRICE_CARD, sessionManager.getPriceCard().getIdpriceCard());
            else
                return;
        }
        else if (type.equals(Constraint.PROMOTION))
        {
            request.put(Constraint.ID_PROMOTION, id+"");

        }
         apiService.sendLogs(request, request.get(Constraint.TOKEN)).enqueue(new Callback<GlobalResponse<BlankResponse>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<GlobalResponse<BlankResponse>> call, Response<GlobalResponse<BlankResponse>> response) {
                if (count >= loopCount - Constraint.ONE) {
                    syncingComplete(type,id);
                } else {
                    int cnt = count + Constraint.ONE;
                    if (type.equals(Constraint.PROMOTION))
                    callApiToSync(cnt,type,id);
                    else
                        callApiToSync(cnt,type,Constraint.ZERO);

                }

            }

            @Override
            public void onFailure(Call<GlobalResponse<BlankResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    /**
     * handle sync completed
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void syncingComplete(String type,int id) {
        // Do what ever you want
        int totalCount = logsVOList.size();
        int counter = totalCount / MAX_CONTACT_COUNT_FOR_EACH_CALL;
        for (int i = Constraint.ZERO; i <= counter; i++) {
            int startPosition = i * MAX_CONTACT_COUNT_FOR_EACH_CALL;
            int totalcontToSend = (i + Constraint.ONE) * MAX_CONTACT_COUNT_FOR_EACH_CALL;
            if (totalcontToSend > logsVOList.size())
                totalcontToSend = logsVOList.size();
            int countToSend = totalcontToSend - startPosition;
            int toPosition = startPosition + countToSend;
            List<Logs> logsList = new ArrayList<>(logsVOList.subList(startPosition, toPosition));
            List<Integer> integers = logsList.stream().map(Logs::getId).collect(Collectors.toList());
            new Thread(new Runnable() {
                @Override
                public void run() {

                    DBCaller.setLogData(context, integers);
                    syncLogCallBack.syncDone(type,id);
                }
            }).start();
        }
        isSyncingInProgress = false;
    }


    /**
     * Create request
     */
    private HashMap<String, String> getRequest(int count) {

        if (logsVOList == null)
            logsVOList = new ArrayList<>();
        HashMap<String, String> logSyncRequest = new HashMap<>();
        try {
            int startPosition = count * MAX_CONTACT_COUNT_FOR_EACH_CALL;
            int totalcontToSend = (count + Constraint.ONE) * MAX_CONTACT_COUNT_FOR_EACH_CALL;
            if (totalcontToSend > logsVOList.size())
                totalcontToSend = logsVOList.size();
            int countToSend = totalcontToSend - startPosition;
            int toPosition = startPosition + countToSend;
            List<Logs> logs = new ArrayList<>(logsVOList.subList(startPosition, toPosition));

            List<LogServerRequest> requests = new ArrayList<>();
            for (Logs logs1 : logs) {
                LogServerRequest logServerRequest = new LogServerRequest();
                logServerRequest.setLog(logs1.getEventName());
                logServerRequest.setTimezone(TimeZone.getDefault().getID());

                logServerRequest.setTime(Utils.getTime(logs1.getEventDateTime()));
                logServerRequest.setDate(Utils.getDate(logs1.getEventDateTime()));
                requests.add(logServerRequest);
            }
            logSyncRequest.put(Constraint.LOG, getJsonObject(requests).toString());
            logSyncRequest.put(Constraint.TOKEN, SessionManager.get().getDeviceToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return logSyncRequest;
    }



    /**
     * List to json
     */
    public JsonArray getJsonObject(List<LogServerRequest> logs) {

        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(logs, new TypeToken<List<LogServerRequest>>() {
        }.getType());

        if (!element.isJsonArray()) {
        }

        JsonArray jsonArray = element.getAsJsonArray();
        return jsonArray;
    }


    /**
     * get All logs from local db
     * @param type
     */
    private void getAllLogs(String type) {
        logsVOList = DBCaller.getLogsFromDatabaseNotSync(context,type);

    }
    private void getAllLogsBasedOnId(String type,Integer integer) {
        logsVOList = DBCaller.getLogsFromDatabaseNotSyncById(context,type,integer);

    }

}
