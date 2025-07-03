package com.example.v2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {InventoryItem.class}, version = 1, exportSchema = false)
public abstract class InventoryDatabase extends RoomDatabase {

    private static InventoryDatabase instance;

    public abstract InventoryDao inventoryDao();

    public static synchronized InventoryDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    InventoryDatabase.class,
                    "inventory_database"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
