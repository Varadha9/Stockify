package com.example.v2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stock_log")
public class StockLog {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String itemName;
    private String action;   // "ADDED", "UPDATED", "DELETED", "QTY_CHANGED"
    private String detail;   // e.g. "Qty: 10 → 15"
    private long timestamp;

    public StockLog(String itemName, String action, String detail, long timestamp) {
        this.itemName = itemName;
        this.action = action;
        this.detail = detail;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getItemName() { return itemName; }
    public String getAction() { return action; }
    public String getDetail() { return detail; }
    public long getTimestamp() { return timestamp; }
}
