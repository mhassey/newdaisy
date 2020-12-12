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
    @Query("SELECT * FROM "+ Constraint.LOGS+" where  logType = :applicationType and isSync = :value order by id DESC")
    List<Logs> getAllApplicationAndDeviceLog(boolean value,String applicationType);

    @Query("UPDATE Logs SET isClear = :val where logType= :type")
    void clearLog(boolean val,String type);

    @Query("delete from logs WHERE  id IN (:ids)")
    void deleteItemPlaces(List<Integer> ids);

    @Query("SELECT * FROM "+ Constraint.LOGS+" where  isSync = :value GROUP BY eventUrl order by id  DESC")
     List<Logs> getAllPromotionLog(boolean value);
    @Query("SELECT * FROM "+ Constraint.LOGS+" where  isSync = :value AND eventDescription =:id  order by id  DESC")
    List<Logs> getAllPromotionLog(boolean value,int id);

    @Query("SELECT MIN(eventDescription) as id FROM "+ Constraint.LOGS+"  GROUP BY eventUrl ")
    List<Integer> getPromotionCount();



}
