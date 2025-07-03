package com.example.v2;

public class RecentActivityItem {
    private final String name;
    private final String category;
    private final double price;
    private final int quantity;
    private final int imageResId;
    private final boolean inStock;

    public RecentActivityItem(String name, String category, double price, int quantity, int imageResId, boolean inStock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.imageResId = imageResId;
        this.inStock = inStock;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getImageResId() { return imageResId; }
    public boolean isInStock() { return inStock; }
}
