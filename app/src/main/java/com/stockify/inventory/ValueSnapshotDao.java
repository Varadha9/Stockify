package com.stockify.inventory;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ValueSnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(ValueSnapshot snapshot);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSnapshots(List<ValueSnapshot> snapshots);

    @Query("SELECT * FROM (SELECT * FROM value_snapshot ORDER BY dateKey DESC LIMIT 7) ORDER BY dateKey ASC")
    List<ValueSnapshot> getLastSevenSnapshots();

    @Query("SELECT * FROM value_snapshot ORDER BY dateKey ASC")
    List<ValueSnapshot> getAllSnapshots();

    @Query("DELETE FROM value_snapshot")
    void deleteAll();
}
