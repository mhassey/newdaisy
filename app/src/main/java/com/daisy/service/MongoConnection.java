package com.daisy.service;

import android.content.Context;
import android.util.Log;

import com.daisy.database.DBCaller;
import com.daisy.pojo.Logs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MongoConnection {

    public void insertDataToMongo(Context context, List<Logs> logs) {
        Gson gson = new Gson();
        String json = gson.toJson(logs);
        Log.e("kpali", json);

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("content-type: application/json");

        RequestBody body = RequestBody.create(mediaType, "{\"collection\":\"DaisyDatabase\",\n    \"database\":\"logs\",\n    \"dataSource\":\"Cluster0\", \"documents\":" + json + "}");
        Request request = new Request.Builder()
                .url("https://data.mongodb-api.com/app/data-lvidx/endpoint/data/v1/action/insertMany")
                .addHeader("content-type", "application/json")
                .addHeader("access-control-request-headers", "*")
                .addHeader("api-key", "52kCmw06CExW1Wms0pYxFOcWBqdISkHtSPuZz8a8BnJ7B0WC38EVooVrlPgV9jJT")
                .method("POST", body)

                .build();
        try {
            Response response = client.newCall(request).execute();
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Log.e("Kali....", response.body().string());

                    List<Integer> integers = logs.stream().map(Logs::getId).collect(Collectors.toList());
                    DBCaller.setLogData(context, integers);

                }
            } catch (Exception e) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<Logs> getLogsList() {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("content-type: application/json");

        RequestBody body = RequestBody.create(mediaType, "{\"collection\":\"databaseDemo\",\n    \"database\":\"MyDatabase\",\n    \"dataSource\":\"Demo\"\n}");
        Request request = new Request.Builder()
                .url("https://data.mongodb-api.com/app/data-kwhja/endpoint/data/v1/action/find")
                .addHeader("content-type", "application/json")
                .addHeader("access-control-request-headers", "*")
                .addHeader("api-key", "6au9eg3ThPolnuOv8tgkeo0pRg5q8KsRlSpIuZqBWyLdHaceqz1y4uzIPK900O8R")
                .method("POST", body)

                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject data = new JSONObject(response.body().string());
            List<Logs> logs = new Gson().fromJson(data.get("documents").toString(), new TypeToken<List<Logs>>() {
            }.getType());
            return logs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    public void deleteObjects() {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("content-type: application/json");

        RequestBody body = RequestBody.create(mediaType, "{" +
                "\"collection\":\"databaseDemo\",\n" +
                "\"database\":\"MyDatabase\",\n  " +
                "\"dataSource\":\"Demo\",\n" +
                "\"filter\":{\"_id\":{\"$oid\":\"634eedfa13973e1699fb4117\"}}\n}");
        Request request = new Request.Builder()
                .url("https://data.mongodb-api.com/app/data-kwhja/endpoint/data/v1/action/deleteMany")
                .addHeader("content-type", "application/json")
                .addHeader("access-control-request-headers", "*")
                .addHeader("api-key", "6au9eg3ThPolnuOv8tgkeo0pRg5q8KsRlSpIuZqBWyLdHaceqz1y4uzIPK900O8R")
                .method("POST", body)

                .build();
        try {
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            Log.e("Kali--------------....", data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
