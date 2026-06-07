package com.stockify.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    private final Context context;
    private final List<StockLog> logs;
    private final SimpleDateFormat fmt =
            new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());

    private String formatDate(long timestamp) {
        synchronized (fmt) {
            return fmt.format(new Date(timestamp));
        }
    }

    public RecentActivityAdapter(Context context, List<StockLog> logs) {
        this.context = context;
        this.logs = logs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_recent_activity, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        StockLog log = logs.get(position);

        h.itemName.setText(log.getItemName());
        h.itemCategory.setText(formatAction(log.getAction()));
        h.itemStockStatus.setText(log.getDetail());
        h.itemPrice.setText(formatDate(log.getTimestamp()));

        // Color the action label by type
        int color = actionColor(log.getAction());
        h.itemCategory.setTextColor(ContextCompat.getColor(context, color));
        h.itemStockStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));

        // Hide quantity controls — not relevant for a log entry
        h.itemQuantity.setVisibility(View.GONE);
        h.incrementButton.setVisibility(View.GONE);
        h.decrementButton.setVisibility(View.GONE);
        h.detailsButton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return Math.min(logs.size(), 8);
    }

    private String formatAction(String action) {
        if (action == null) return "Updated";
        switch (action) {
            case "ADDED":           return "Added";
            case "DELETED":         return "Deleted";
            case "QTY_CHANGED":     return "Qty changed";
            case "UPDATED":         return "Updated";
            case "BULK_QTY_CHANGED":return "Bulk update";
            default:                return action;
        }
    }

    private int actionColor(String action) {
        if (action == null) return R.color.text_secondary;
        switch (action) {
            case "ADDED":           return R.color.color_success;
            case "DELETED":         return R.color.color_error;
            case "QTY_CHANGED":
            case "BULK_QTY_CHANGED":return R.color.color_warning;
            default:                return R.color.brand_primary;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCategory, itemStockStatus, itemPrice, itemQuantity;
        View incrementButton, decrementButton, detailsButton;

        ViewHolder(@NonNull View v) {
            super(v);
            itemName        = v.findViewById(R.id.itemName);
            itemCategory    = v.findViewById(R.id.itemCategory);
            itemStockStatus = v.findViewById(R.id.itemStockStatus);
            itemPrice       = v.findViewById(R.id.itemPrice);
            itemQuantity    = v.findViewById(R.id.itemQuantity);
            incrementButton = v.findViewById(R.id.incrementButton);
            decrementButton = v.findViewById(R.id.decrementButton);
            detailsButton   = v.findViewById(R.id.detailsButton);
        }
    }
}
