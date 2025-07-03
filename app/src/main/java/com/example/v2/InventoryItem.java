package com.example.v2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory_table")
public class InventoryItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String category;
    private double price;
    private int quantity;
    private int lowStockThreshold;
    private String description;
    private String imagePath;

    public InventoryItem(String name, String category, double price, int quantity,
                         int lowStockThreshold, String description, String imagePath) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
        this.description = description;
        this.imagePath = imagePath;
    }

    // Getters & Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public String getCategory() { return category; }

    public double getPrice() { return price; }

    public int getQuantity() { return quantity; }

    public int getLowStockThreshold() { return lowStockThreshold; }

    public String getDescription() { return description; }

    public String getImagePath() { return imagePath; }
}
