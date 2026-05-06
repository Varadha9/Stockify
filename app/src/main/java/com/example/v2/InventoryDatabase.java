package com.example.v2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {InventoryItem.class, StockLog.class, ValueSnapshot.class}, version = 4, exportSchema = false)
public abstract class InventoryDatabase extends RoomDatabase {

    private static volatile InventoryDatabase instance;

    public abstract InventoryDao inventoryDao();
    public abstract StockLogDao stockLogDao();
    public abstract ValueSnapshotDao valueSnapshotDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE inventory_table ADD COLUMN sku TEXT");
            db.execSQL("ALTER TABLE inventory_table ADD COLUMN supplier TEXT");
            db.execSQL("CREATE TABLE IF NOT EXISTS stock_log (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "itemName TEXT," +
                    "action TEXT," +
                    "detail TEXT," +
                    "timestamp INTEGER NOT NULL)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS value_snapshot (" +
                    "dateKey INTEGER NOT NULL," +
                    "totalValue REAL NOT NULL," +
                    "totalUnits INTEGER NOT NULL," +
                    "timestamp INTEGER NOT NULL," +
                    "PRIMARY KEY(dateKey))");
            rebuildInventoryTable(db);
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            rebuildInventoryTable(db);
        }
    };

    public static synchronized InventoryDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    InventoryDatabase.class,
                    "inventory_database"
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
             .fallbackToDestructiveMigration()
             .build();
        }
        return instance;
    }

    private static void rebuildInventoryTable(SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS inventory_table_fixed");
        db.execSQL("CREATE TABLE inventory_table_fixed (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "name TEXT," +
                "category TEXT," +
                "price REAL NOT NULL," +
                "quantity INTEGER NOT NULL," +
                "lowStockThreshold INTEGER NOT NULL," +
                "description TEXT," +
                "imagePath TEXT," +
                "sku TEXT," +
                "supplier TEXT)");
        db.execSQL("INSERT INTO inventory_table_fixed (" +
                "id, name, category, price, quantity, lowStockThreshold, description, imagePath, sku, supplier) " +
                "SELECT id, name, category, price, quantity, lowStockThreshold, description, imagePath, sku, supplier " +
                "FROM inventory_table");
        db.execSQL("DROP TABLE inventory_table");
        db.execSQL("ALTER TABLE inventory_table_fixed RENAME TO inventory_table");
    }
}
