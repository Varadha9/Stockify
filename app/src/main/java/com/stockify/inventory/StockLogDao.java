package com.stockify.inventory;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StockLogDao {

    @Insert
    void insert(StockLog log);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLogs(List<StockLog> logs);

    @Query("SELECT * FROM stock_log ORDER BY timestamp DESC LIMIT 50")
    List<StockLog> getRecentLogs();

    @Query("SELECT * FROM stock_log ORDER BY timestamp ASC")
    List<StockLog> getAllLogs();

    @Query("DELETE FROM stock_log WHERE timestamp < :cutoff")
    void deleteOlderThan(long cutoff);

    @Query("DELETE FROM stock_log")
    void deleteAll();
}
