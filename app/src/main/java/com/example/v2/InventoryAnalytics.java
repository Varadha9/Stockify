package com.example.v2;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class InventoryAnalytics {

    private InventoryAnalytics() {}

    public static void recordTodaySnapshot(Context context) {
        InventoryDatabase db = InventoryDatabase.getDatabase(context);
        double totalValue = db.inventoryDao().getTotalValue();
        int totalUnits = db.inventoryDao().getTotalStockCount();
        db.valueSnapshotDao().upsert(new ValueSnapshot(
                getTodayKey(),
                totalValue,
                totalUnits,
                System.currentTimeMillis()
        ));
    }

    public static long getTodayKey() {
        String date = new SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(new Date());
        return Long.parseLong(date);
    }
}
