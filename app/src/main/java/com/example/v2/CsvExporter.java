package com.example.v2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CsvExporter {

    private static final long MAX_EXPORT_SIZE_BYTES = 50 * 1024 * 1024; // 50 MB guard

    public static Intent export(Context ctx, List<InventoryItem> items) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        File cacheDir = ctx.getCacheDir().getCanonicalFile();
        File file = new File(cacheDir, "stockify_export_" + timestamp + ".csv");

        // Path traversal guard
        if (!file.getCanonicalPath().startsWith(cacheDir.getCanonicalPath())) {
            throw new IOException("Invalid export path");
        }

        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Name,SKU,Category,Supplier,Price,Quantity,Low Stock Threshold,Description\n");
            for (InventoryItem item : items) {
                fw.write(String.format(Locale.ROOT,
                        "\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%d,%d,\"%s\"\n",
                        safe(item.getName()), safe(item.getSku()), safe(item.getCategory()),
                        safe(item.getSupplier()), item.getPrice(), item.getQuantity(),
                        item.getLowStockThreshold(), safe(item.getDescription())));
            }
        }

        if (file.length() > MAX_EXPORT_SIZE_BYTES) {
            file.delete();
            throw new IOException("Export file too large");
        }

        Uri uri = FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".provider", file);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/csv");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_SUBJECT, "Stockify Inventory Export");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Guard: only launch if something can handle it
        Intent chooser = Intent.createChooser(share, "Export Inventory via...");
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return chooser;
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace("\"", "\"\"");
    }
}
