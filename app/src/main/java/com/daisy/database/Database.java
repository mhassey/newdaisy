package com.daisy.database;

import androidx.room.RoomDatabase;

import com.daisy.dao.LogDao;
import com.daisy.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
