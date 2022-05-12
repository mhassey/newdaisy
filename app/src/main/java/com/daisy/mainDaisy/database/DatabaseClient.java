package com.daisy.mainDaisy.database;

import android.content.Context;

import androidx.room.Room;

import com.daisy.mainDaisy.utils.Constraint;

public class DatabaseClient {
    private Context context;
    private static  DatabaseClient mInstance;
    private Database database;
    public DatabaseClient(Context context)
    {
        this.context=context;
        database= Room.databaseBuilder(context,Database.class, Constraint.LOGS).build();
    }
    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public Database getAppDatabase() {
        return database;
    }
}
