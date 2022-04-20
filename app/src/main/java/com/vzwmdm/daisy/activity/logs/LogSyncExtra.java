package com.vzwmdm.daisy.activity.logs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import com.vzwmdm.daisy.R;
import com.vzwmdm.daisy.apiService.ApiService;
import com.vzwmdm.daisy.apiService.AppRetrofit;
import com.vzwmdm.daisy.common.session.SessionManager;
import com.vzwmdm.daisy.pojo.request.LogServerRequest;
import com.vzwmdm.daisy.pojo.response.BlankResponse;
import com.vzwmdm.daisy.pojo.response.GlobalResponse;
import com.vzwmdm.daisy.utils.Constraint;
import com.vzwmdm.daisy.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogSyncExtra {
    Context context;
    boolean showToast = false;
    private ProgressDialog progressDialog;

    public LogSyncExtra(Context context, boolean showToast) {
        this.context = context;
        this.showToast = showToast;
        if (showToast) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null && progressDialog.isShowing()) {

                    } else {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage(context.getString(R.string.loading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                }
            });
        }
    }

    public void fireLogExtra() {
        ApiService apiService = AppRetrofit.getInstance().getApiService();

        final HashMap<String, String> request = getRequest();
        request.put(Constraint.ID_PRICE_CARD, SessionManager.get().getPriceCard().getIdpriceCard());
        apiService.sendLogs(request, request.get(Constraint.TOKEN)).enqueue(new Callback<GlobalResponse<BlankResponse>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<GlobalResponse<BlankResponse>> call, Response<GlobalResponse<BlankResponse>> response) {
                if (showToast) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse<BlankResponse>> call, Throwable t) {
                if (showToast) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    });
                }
                t.printStackTrace();
            }
        });
    }

    private HashMap<String, String> getRequest() {

        HashMap<String, String> logSyncRequest = new HashMap<>();
        try {

            List<LogServerRequest> requests = new ArrayList<>();

            LogServerRequest logServerRequest = new LogServerRequest();
            logServerRequest.setLog(Constraint.IDLE);
            logServerRequest.setTimezone(TimeZone.getDefault().getID());

            logServerRequest.setTime(Utils.getTodayDateWithTime());
            logServerRequest.setDate(Utils.getTodayDateWithTime());
            requests.add(logServerRequest);
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

}
