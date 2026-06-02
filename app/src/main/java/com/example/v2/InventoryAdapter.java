package com.example.v2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    public interface SelectionListener {
        void onSelectionChanged(int count);
    }

    public static final int SORT_NAME = 0;
    public static final int SORT_LOW_STOCK = 1;
    public static final int SORT_QTY_LOW_HIGH = 2;
    public static final int SORT_QTY_HIGH_LOW = 3;
    public static final int SORT_PRICE_LOW_HIGH = 4;
    public static final int SORT_PRICE_HIGH_LOW = 5;

    private final Context context;
    private final SelectionListener selectionListener;
    private final List<InventoryItem> fullList = new ArrayList<>();
    private final List<InventoryItem> inventoryList = new ArrayList<>();
    private final Set<Integer> selectedIds = new HashSet<>();

    private boolean selectionMode = false;
    private String query = "";
    private String categoryFilter = "All";
    private int sortMode = SORT_NAME;

    public InventoryAdapter(Context context) {
        this(context, null);
    }

    public InventoryAdapter(Context context, SelectionListener selectionListener) {
        this.context = context;
        this.selectionListener = selectionListener;
    }

    public void setInventoryList(List<InventoryItem> list) {
        fullList.clear();
        fullList.addAll(list);
        applyFilters();
    }

    public void filter(String query) {
        this.query = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        applyFilters();
    }

    public void setCategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter == null || categoryFilter.trim().isEmpty()
                ? "All" : categoryFilter;
        applyFilters();
    }

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
        applyFilters();
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
        if (!selectionMode) selectedIds.clear();
        notifyItemRangeChanged(0, inventoryList.size());
        notifySelectionChanged();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public List<Integer> getSelectedIds() {
        return new ArrayList<>(selectedIds);
    }

    public int getSelectedCount() {
        return selectedIds.size();
    }

    private void applyFilters() {
        List<InventoryItem> newList = new ArrayList<>();
        for (InventoryItem item : fullList) {
            if (!"All".equals(categoryFilter) && !categoryFilter.equals(item.getCategory())) continue;
            if (!query.isEmpty() && !matchesQuery(item, query)) continue;
            newList.add(item);
        }
        sortList(newList);
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return inventoryList.size(); }
            @Override public int getNewListSize() { return newList.size(); }
            @Override public boolean areItemsTheSame(int o, int n) {
                return inventoryList.get(o).getId() == newList.get(n).getId();
            }
            @Override public boolean areContentsTheSame(int o, int n) {
                InventoryItem a = inventoryList.get(o), b = newList.get(n);
                return a.getQuantity() == b.getQuantity()
                        && Double.compare(a.getPrice(), b.getPrice()) == 0
                        && Objects.equals(a.getName(), b.getName())
                        && Objects.equals(a.getCategory(), b.getCategory());
            }
        });
        inventoryList.clear();
        inventoryList.addAll(newList);
        diff.dispatchUpdatesTo(this);
    }

    private boolean matchesQuery(InventoryItem item, String query) {
        return contains(item.getName(), query)
                || contains(item.getCategory(), query)
                || contains(item.getSku(), query)
                || contains(item.getSupplier(), query);
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private void sortList(List<InventoryItem> list) {
        Comparator<InventoryItem> comparator;
        switch (sortMode) {
            case SORT_LOW_STOCK:
                comparator = Comparator
                        .comparing((InventoryItem item) -> item.getQuantity() > item.getLowStockThreshold())
                        .thenComparingInt(InventoryItem::getQuantity);
                break;
            case SORT_QTY_LOW_HIGH:
                comparator = Comparator.comparingInt(InventoryItem::getQuantity);
                break;
            case SORT_QTY_HIGH_LOW:
                comparator = (a, b) -> Integer.compare(b.getQuantity(), a.getQuantity());
                break;
            case SORT_PRICE_LOW_HIGH:
                comparator = Comparator.comparingDouble(InventoryItem::getPrice);
                break;
            case SORT_PRICE_HIGH_LOW:
                comparator = (a, b) -> Double.compare(b.getPrice(), a.getPrice());
                break;
            case SORT_NAME:
            default:
                comparator = Comparator.comparing(item -> safe(item.getName()).toLowerCase(Locale.ROOT));
                break;
        }
        Collections.sort(list, comparator);
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
        holder.price.setText(CurrencyFormatter.format(context, item.getPrice()));
        holder.categoryBadge.setText(item.getCategory());
        holder.meta.setText(buildMeta(item));

        String initial = item.getName() == null || item.getName().isEmpty()
                ? "?" : String.valueOf(item.getName().charAt(0)).toUpperCase(Locale.getDefault());
        holder.initial.setText(initial);

        boolean isLow = item.getQuantity() <= item.getLowStockThreshold();
        holder.lowStockBadge.setVisibility(isLow ? View.VISIBLE : View.GONE);
        holder.quantity.setTextColor(ContextCompat.getColor(context,
                isLow ? R.color.color_error : R.color.text_secondary));

        holder.checkBox.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(selectedIds.contains(item.getId()));

        holder.itemView.setOnLongClickListener(v -> {
            if (!selectionMode) setSelectionMode(true);
            toggleSelection(item.getId());
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (selectionMode) {
                toggleSelection(item.getId());
            } else {
                Intent intent = new Intent(context, EditItemActivity.class);
                intent.putExtra("item_id", item.getId());
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });

        holder.checkBox.setOnClickListener(v -> toggleSelection(item.getId()));
    }

    private String buildMeta(InventoryItem item) {
        List<String> parts = new ArrayList<>();
        if (item.getSku() != null && !item.getSku().isEmpty()) parts.add("SKU " + item.getSku());
        if (item.getSupplier() != null && !item.getSupplier().isEmpty()) parts.add(item.getSupplier());
        return parts.isEmpty() ? "No SKU or supplier" : join(parts);
    }

    private String join(List<String> parts) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) builder.append(" | ");
            builder.append(parts.get(i));
        }
        return builder.toString();
    }

    private void toggleSelection(int itemId) {
        if (selectedIds.contains(itemId)) selectedIds.remove(itemId);
        else selectedIds.add(itemId);
        int pos = indexOfId(itemId);
        if (pos >= 0) notifyItemChanged(pos);
        notifySelectionChanged();
    }

    private int indexOfId(int id) {
        for (int i = 0; i < inventoryList.size(); i++) {
            if (inventoryList.get(i).getId() == id) return i;
        }
        return -1;
    }

    private void notifySelectionChanged() {
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(selectedIds.size());
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, price, initial, categoryBadge, lowStockBadge, meta;
        CheckBox checkBox;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemNameText);
            quantity = itemView.findViewById(R.id.itemQuantityText);
            price = itemView.findViewById(R.id.itemPriceText);
            initial = itemView.findViewById(R.id.txt_item_initial);
            categoryBadge = itemView.findViewById(R.id.txt_category_badge);
            lowStockBadge = itemView.findViewById(R.id.txt_low_stock_badge);
            meta = itemView.findViewById(R.id.itemMetaText);
            checkBox = itemView.findViewById(R.id.itemCheckBox);
        }
    }
}
