package com.daisy.daisyGo.database;

import androidx.room.RoomDatabase;

import com.daisy.daisyGo.database.dao.LogDao;
import com.daisy.daisyGo.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
