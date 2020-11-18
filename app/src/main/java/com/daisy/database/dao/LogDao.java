package com.daisy.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.daisy.utils.Constraint;
import com.daisy.pojo.Logs;

import java.util.List;

@Dao
public interface LogDao {
    @Insert
    void insert(Logs logs);
    @Query("SELECT * FROM "+ Constraint.LOGS+" where logType = :type and isClear = :value order by id DESC")
    List<Logs> getAll(String type,boolean value);
    @Query("SELECT * FROM "+ Constraint.LOGS+" where  isSync = :value order by id DESC")
    List<Logs> getAllApplicationAndDeviceLog(boolean value);

    @Query("UPDATE Logs SET isClear = :val where logType= :type")
    void clearLog(boolean val,String type);

    @Query("delete from logs WHERE  id IN (:ids)")
    void deleteItemPlaces(List<Integer> ids);
}
