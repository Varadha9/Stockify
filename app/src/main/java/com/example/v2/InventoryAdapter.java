package com.example.v2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private Context context;
    private List<InventoryItem> inventoryList = new ArrayList<>();

    public InventoryAdapter(Context context) {
        this.context = context;
    }

    public void setInventoryList(List<InventoryItem> inventoryList) {
        this.inventoryList = inventoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText("Qty: " + item.getQuantity());
        holder.price.setText("₹" + item.getPrice());
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, price;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemNameText);
            quantity = itemView.findViewById(R.id.itemQuantityText);
            price = itemView.findViewById(R.id.itemPriceText);
        }
    }
}
