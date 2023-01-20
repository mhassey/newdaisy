package com.android_tv.database;

import androidx.room.RoomDatabase;

import com.android_tv.database.dao.LogDao;
import com.android_tv.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
