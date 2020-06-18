package com.daisy.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.daisy.common.Constraint;
import com.daisy.pojo.Logs;

import java.util.List;

@Dao
public interface LogDao {
    @Insert
    void insert(Logs logs);
    @Query("SELECT * FROM "+ Constraint.LOGS+" where logType = :type and isClear = :value")
    List<Logs> getAll(String type,boolean value);


    @Query("UPDATE Logs SET isClear = :val where logType= :type")
    void clearLog(boolean val,String type);
}
