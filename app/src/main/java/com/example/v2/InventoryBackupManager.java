package com.example.v2;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class InventoryBackupManager {

    private static final int MAX_BACKUP_BYTES = 10 * 1024 * 1024; // 10 MB

    private InventoryBackupManager() {}

    public static class BackupData {
        public final List<InventoryItem> items     = new ArrayList<>();
        public final List<StockLog> logs           = new ArrayList<>();
        public final List<ValueSnapshot> snapshots = new ArrayList<>();
        public String currencySymbol = CurrencyFormatter.DEFAULT_SYMBOL;
    }

    public static void writeBackup(Context context, Uri uri, BackupData data)
            throws IOException, JSONException {
        JSONObject root = new JSONObject();
        root.put("version", 1);
        root.put("currencySymbol", data.currencySymbol);

        JSONArray items = new JSONArray();
        for (InventoryItem item : data.items) {
            JSONObject obj = new JSONObject();
            obj.put("id",                item.getId());
            obj.put("name",              safe(item.getName()));
            obj.put("category",          safe(item.getCategory()));
            obj.put("price",             item.getPrice());
            obj.put("quantity",          item.getQuantity());
            obj.put("lowStockThreshold", item.getLowStockThreshold());
            obj.put("description",       safe(item.getDescription()));
            obj.put("imagePath",         safe(item.getImagePath()));
            obj.put("sku",               safe(item.getSku()));
            obj.put("supplier",          safe(item.getSupplier()));
            items.put(obj);
        }
        root.put("items", items);

        JSONArray logs = new JSONArray();
        for (StockLog log : data.logs) {
            JSONObject obj = new JSONObject();
            obj.put("id",        log.getId());
            obj.put("itemName",  safe(log.getItemName()));
            obj.put("action",    safe(log.getAction()));
            obj.put("detail",    safe(log.getDetail()));
            obj.put("timestamp", log.getTimestamp());
            logs.put(obj);
        }
        root.put("logs", logs);

        JSONArray snapshots = new JSONArray();
        for (ValueSnapshot snapshot : data.snapshots) {
            JSONObject obj = new JSONObject();
            obj.put("dateKey",    snapshot.getDateKey());
            obj.put("totalValue", snapshot.getTotalValue());
            obj.put("totalUnits", snapshot.getTotalUnits());
            obj.put("timestamp",  snapshot.getTimestamp());
            snapshots.put(obj);
        }
        root.put("snapshots", snapshots);

        try (OutputStream out = context.getContentResolver().openOutputStream(uri)) {
            if (out == null) throw new IOException("Unable to open backup file");
            out.write(root.toString(2).getBytes(StandardCharsets.UTF_8));
        }
    }

    public static BackupData readBackup(Context context, Uri uri)
            throws IOException, JSONException {
        JSONObject root = new JSONObject(readAll(context, uri));
        BackupData data = new BackupData();
        data.currencySymbol = root.optString("currencySymbol", CurrencyFormatter.DEFAULT_SYMBOL);

        JSONArray items = root.optJSONArray("items");
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = items.getJSONObject(i);
                InventoryItem item = new InventoryItem(
                        obj.optString("name", ""),
                        obj.optString("category", ""),
                        obj.optDouble("price", 0.0),
                        obj.optInt("quantity", 0),
                        obj.optInt("lowStockThreshold", 0),
                        obj.optString("description", ""),
                        obj.optString("imagePath", ""),
                        obj.optString("sku", ""),
                        obj.optString("supplier", "")
                );
                item.setId(obj.optInt("id", 0));
                data.items.add(item);
            }
        }

        JSONArray logs = root.optJSONArray("logs");
        if (logs != null) {
            for (int i = 0; i < logs.length(); i++) {
                JSONObject obj = logs.getJSONObject(i);
                StockLog log = new StockLog(
                        obj.optString("itemName", ""),
                        obj.optString("action", ""),
                        obj.optString("detail", ""),
                        obj.optLong("timestamp", System.currentTimeMillis())
                );
                log.setId(obj.optInt("id", 0));
                data.logs.add(log);
            }
        }

        JSONArray snapshots = root.optJSONArray("snapshots");
        if (snapshots != null) {
            for (int i = 0; i < snapshots.length(); i++) {
                JSONObject obj = snapshots.getJSONObject(i);
                data.snapshots.add(new ValueSnapshot(
                        obj.optLong("dateKey", 0),
                        obj.optDouble("totalValue", 0.0),
                        obj.optInt("totalUnits", 0),
                        obj.optLong("timestamp", System.currentTimeMillis())
                ));
            }
        }

        return data;
    }

    /** Reads backup content with a hard size cap to prevent DoS. */
    private static String readAll(Context context, Uri uri) throws IOException {
        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            if (in == null) throw new IOException("Unable to open backup file");

            // Size guard — reject files larger than 10 MB
            int available = in.available();
            if (available > MAX_BACKUP_BYTES) {
                throw new IOException("Backup file too large (max 10 MB)");
            }

            StringBuilder builder = new StringBuilder();
            int totalRead = 0;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalRead += line.length();
                    if (totalRead > MAX_BACKUP_BYTES) {
                        throw new IOException("Backup file too large (max 10 MB)");
                    }
                    builder.append(line);
                }
            }
            return builder.toString();
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
