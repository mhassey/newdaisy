package com.vzwmdm.daisy.database;

import androidx.room.RoomDatabase;

import com.vzwmdm.daisy.database.dao.LogDao;
import com.vzwmdm.daisy.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
