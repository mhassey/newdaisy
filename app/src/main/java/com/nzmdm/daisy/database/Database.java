package com.nzmdm.daisy.database;

import androidx.room.RoomDatabase;

import com.nzmdm.daisy.database.dao.LogDao;
import com.nzmdm.daisy.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
