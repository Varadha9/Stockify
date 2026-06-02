package com.example.v2;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

public class LowStockWorker extends Worker {

    private static final String CHANNEL_ID = "stockify_low_stock";
    private static final int NOTIF_ID = 1001;

    public LowStockWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context ctx = getApplicationContext();
        List<InventoryItem> lowItems = InventoryDatabase.getDatabase(ctx)
                .inventoryDao().getLowStockItemsOnly();

        if (lowItems.isEmpty()) return Result.success();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return Result.success();
        }

        createChannel(ctx);

        StringBuilder names = new StringBuilder();
        for (int i = 0; i < Math.min(3, lowItems.size()); i++) {
            if (i > 0) names.append(", ");
            names.append(lowItems.get(i).getName());
        }
        if (lowItems.size() > 3) names.append(" +").append(lowItems.size() - 3).append(" more");

        Intent mainIntent = new Intent(ctx, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent mainPi = PendingIntent.getActivity(ctx, 0, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Open inventory (low-stock sorted) — not just one item
        Intent stockIntent = new Intent(ctx, MainActivity.class);
        stockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        stockIntent.putExtra("open_tab", "inventory");
        stockIntent.putExtra("sort_low_stock", true);
        PendingIntent stockPi = PendingIntent.getActivity(ctx, 1, stockIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_inventory)
                .setContentTitle("Low Stock Alert - " + lowItems.size() + " item(s)")
                .setContentText(names.toString())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        lowItems.size() + " item(s) are running low: " + names))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(mainPi)
                .addAction(R.drawable.ic_inventory, "View Low Stock", stockPi)
                .setAutoCancel(true);

        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIF_ID, builder.build());

        return Result.success();
    }

    private void createChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, "Low Stock Alerts", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Alerts when items fall below their low stock threshold");
            ((NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(ch);
        }
    }
}
