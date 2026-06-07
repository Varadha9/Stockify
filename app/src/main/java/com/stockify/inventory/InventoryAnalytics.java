package com.stockify.inventory;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class InventoryAnalytics {

    private static final long LOG_RETENTION_MS = 90L * 24 * 60 * 60 * 1000; // 90 days

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
        db.stockLogDao().deleteOlderThan(System.currentTimeMillis() - LOG_RETENTION_MS);
    }

    public static long getTodayKey() {
        String date = new SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(new Date());
        return Long.parseLong(date);
    }
}
