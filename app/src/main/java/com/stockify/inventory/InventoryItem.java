package com.stockify.inventory;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory_table")
public class InventoryItem {

    public static final String[] CATEGORIES = {
            "Electronics", "Clothing", "Grocery",
            "Furniture", "Accessories", "Other"
    };

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String category;
    private double price;
    private int quantity;
    private int lowStockThreshold;
    private String description;
    private String imagePath;
    private String sku;
    private String supplier;
    private double costPrice;
    private int reorderQuantity;

    public InventoryItem(String name, String category, double price, int quantity,
                         int lowStockThreshold, String description, String imagePath,
                         String sku, String supplier) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
        this.description = description;
        this.imagePath = imagePath;
        this.sku = sku;
        this.supplier = supplier;
        this.costPrice = 0;
        this.reorderQuantity = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int t) { this.lowStockThreshold = t; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
    public int getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(int reorderQuantity) { this.reorderQuantity = reorderQuantity; }
}
