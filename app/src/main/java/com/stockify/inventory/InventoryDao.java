package com.stockify.inventory;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface InventoryDao {

    @Insert
    long insertItem(InventoryItem item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(List<InventoryItem> items);

    @Update
    void updateItem(InventoryItem item);

    @Delete
    void deleteItem(InventoryItem item);

    @Query("SELECT * FROM inventory_table ORDER BY name ASC")
    List<InventoryItem> getAllItems();

    @Query("SELECT * FROM inventory_table ORDER BY id DESC LIMIT :limit")
    List<InventoryItem> getRecentItems(int limit);

    @Query("SELECT * FROM inventory_table ORDER BY price ASC")
    List<InventoryItem> getAllItemsByPriceAsc();

    @Query("SELECT * FROM inventory_table ORDER BY price DESC")
    List<InventoryItem> getAllItemsByPriceDesc();

    @Query("SELECT * FROM inventory_table ORDER BY quantity ASC")
    List<InventoryItem> getAllItemsByQtyAsc();

    @Query("SELECT * FROM inventory_table ORDER BY quantity DESC")
    List<InventoryItem> getAllItemsByQtyDesc();

    @Query("SELECT * FROM inventory_table WHERE quantity <= lowStockThreshold ORDER BY quantity ASC")
    List<InventoryItem> getLowStockItemsOnly();

    @Query("SELECT * FROM inventory_table WHERE category = :cat ORDER BY name ASC")
    List<InventoryItem> getItemsByCategory(String cat);

    @Query("SELECT DISTINCT category FROM inventory_table ORDER BY category ASC")
    List<String> getAllCategories();

    @Query("SELECT DISTINCT supplier FROM inventory_table WHERE supplier IS NOT NULL AND supplier != '' ORDER BY supplier ASC")
    List<String> getAllSuppliers();

    @Query("SELECT * FROM inventory_table WHERE id = :id")
    InventoryItem findItemById(int id);

    @Query("SELECT * FROM inventory_table WHERE quantity <= :threshold")
    List<InventoryItem> getLowStockItems(int threshold);

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM inventory_table")
    int getTotalStockCount();

    @Query("SELECT COUNT(*) FROM inventory_table")
    int getDistinctItemCount();

    @Query("SELECT * FROM inventory_table WHERE name = :name")
    InventoryItem findItemByName(String name);

    @Query("SELECT * FROM inventory_table WHERE sku = :sku")
    InventoryItem findItemBySku(String sku);

    @Query("SELECT name FROM inventory_table")
    List<String> getAllItemNames();

    @Query("SELECT COUNT(*) FROM inventory_table WHERE quantity <= lowStockThreshold")
    int getLowStockCount();

    @Query("SELECT COALESCE(SUM(price * quantity), 0) FROM inventory_table")
    double getTotalValue();

    @Query("SELECT COALESCE(SUM(costPrice * quantity), 0) FROM inventory_table")
    double getTotalCostValue();

    @Query("UPDATE inventory_table SET quantity = CASE WHEN quantity + :delta < 0 THEN 0 ELSE quantity + :delta END WHERE id IN (:ids)")
    void adjustQuantityForIds(List<Integer> ids, int delta);

    @Query("DELETE FROM inventory_table")
    void deleteAll();
}
