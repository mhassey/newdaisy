package com.ally.database;

import androidx.room.RoomDatabase;

import com.ally.database.dao.LogDao;
import com.ally.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
