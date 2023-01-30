package com.daisy.database;

import androidx.room.RoomDatabase;

import com.daisy.database.dao.LogDao;
import com.daisy.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1,exportSchema = false)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
