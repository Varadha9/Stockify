package com.example.v2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface InventoryDao {



    @Insert
    void insertItem(InventoryItem item);

    @Update
    void updateItem(InventoryItem item);

    @Delete
    void deleteItem(InventoryItem item);

    @Query("SELECT * FROM inventory_table ORDER BY name ASC")
    List<InventoryItem> getAllItems();

    @Query("SELECT * FROM inventory_table WHERE quantity <= :threshold")
    List<InventoryItem> getLowStockItems(int threshold);

    @Query("SELECT SUM(quantity) FROM inventory_table")
    int getTotalStockCount();

    @Query("SELECT * FROM inventory_table WHERE name = :name")
    InventoryItem findItemByName(String name);

    @Query("SELECT name FROM inventory_table")
    List<String> getAllItemNames();

}
