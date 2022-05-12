package com.daisy.optimalPermission.database;

import androidx.room.RoomDatabase;

import com.daisy.optimalPermission.database.dao.LogDao;
import com.daisy.optimalPermission.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
