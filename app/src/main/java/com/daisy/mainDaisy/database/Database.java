package com.daisy.mainDaisy.database;

import androidx.room.RoomDatabase;

import com.daisy.mainDaisy.database.dao.LogDao;
import com.daisy.mainDaisy.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
