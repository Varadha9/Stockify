package com.stockify.inventory;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "value_snapshot")
public class ValueSnapshot {

    @PrimaryKey
    private long dateKey;

    private double totalValue;
    private int totalUnits;
    private long timestamp;

    public ValueSnapshot(long dateKey, double totalValue, int totalUnits, long timestamp) {
        this.dateKey = dateKey;
        this.totalValue = totalValue;
        this.totalUnits = totalUnits;
        this.timestamp = timestamp;
    }

    public long getDateKey() { return dateKey; }
    public void setDateKey(long dateKey) { this.dateKey = dateKey; }
    public double getTotalValue() { return totalValue; }
    public void setTotalValue(double totalValue) { this.totalValue = totalValue; }
    public int getTotalUnits() { return totalUnits; }
    public void setTotalUnits(int totalUnits) { this.totalUnits = totalUnits; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
