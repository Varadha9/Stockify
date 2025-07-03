package com.example.v2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {
    private final Context context;
    private final List<RecentActivityItem> itemList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onIncrementClick(int position);
        void onDecrementClick(int position);
        void onDetailsClick(int position);
    }

    public RecentActivityAdapter(Context context, List<RecentActivityItem> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentActivityItem item = itemList.get(position);

        holder.itemName.setText(item.getName());
        holder.itemCategory.setText(item.getCategory());
        holder.itemPrice.setText("₹" + item.getPrice());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        holder.itemStockStatus.setText(item.isInStock() ? "In Stock" : "Out of Stock");
        holder.itemStockStatus.setTextColor(context.getResources().getColor(
                item.isInStock() ? R.color.text_secondary : R.color.warning));

        holder.itemImageView.setImageResource(item.getImageResId());

        holder.incrementButton.setOnClickListener(v -> listener.onIncrementClick(position));
        holder.decrementButton.setOnClickListener(v -> listener.onDecrementClick(position));
        holder.detailsButton.setOnClickListener(v -> listener.onDetailsClick(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCategory, itemStockStatus, itemPrice, itemQuantity;
        ImageView itemImageView;
        MaterialButton incrementButton, decrementButton, detailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            itemName = itemView.findViewById(R.id.itemName);
            itemCategory = itemView.findViewById(R.id.itemCategory);
            itemStockStatus = itemView.findViewById(R.id.itemStockStatus);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            detailsButton = itemView.findViewById(R.id.detailsButton);
        }
    }
}
