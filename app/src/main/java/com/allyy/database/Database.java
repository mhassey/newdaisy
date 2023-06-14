package com.allyy.database;

import androidx.room.RoomDatabase;

import com.allyy.database.dao.LogDao;
import com.allyy.pojo.Logs;

@androidx.room.Database(entities = {Logs.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract LogDao logDao();

}
