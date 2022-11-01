package com.iris.database;

import androidx.room.RoomDatabase;

import com.iris.database.dao.LogDao;
import com.iris.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
