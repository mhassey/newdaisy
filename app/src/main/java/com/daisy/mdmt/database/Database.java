package com.daisy.mdmt.database;

import androidx.room.RoomDatabase;

import com.daisy.mdmt.database.dao.LogDao;
import com.daisy.mdmt.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
