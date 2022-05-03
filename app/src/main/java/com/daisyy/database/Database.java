package com.daisyy.database;

import androidx.room.RoomDatabase;

import com.daisyy.database.dao.LogDao;
import com.daisyy.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
