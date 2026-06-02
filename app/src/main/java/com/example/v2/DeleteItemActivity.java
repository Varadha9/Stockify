package com.example.v2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeleteItemActivity extends AppCompatActivity {

    private InventoryDatabase db;
    private DeleteAdapter deleteAdapter;
    private List<InventoryItem> allItems = new ArrayList<>();
    private TextInputEditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);

        db = InventoryDatabase.getDatabase(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_delete_items);
        searchBox = findViewById(R.id.search_delete);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());

        deleteAdapter = new DeleteAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(deleteAdapter);

        searchBox.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int b, int c) {
                filterItems(s.toString());
            }
        });

        loadItems();
    }

    private void loadItems() {
        AppExecutor.get().execute(() -> {
            allItems = db.inventoryDao().getAllItems();
            runOnUiThread(() -> deleteAdapter.setItems(allItems));
        });
    }

    private void filterItems(String query) {
        if (query.trim().isEmpty()) {
            deleteAdapter.setItems(allItems);
            return;
        }
        String q = query.toLowerCase(Locale.ROOT);
        List<InventoryItem> filtered = new ArrayList<>();
        for (InventoryItem item : allItems) {
            if ((item.getName() != null && item.getName().toLowerCase(Locale.ROOT).contains(q))
                    || (item.getSku() != null && item.getSku().toLowerCase(Locale.ROOT).contains(q))) {
                filtered.add(item);
            }
        }
        deleteAdapter.setItems(filtered);
    }

    private void confirmDelete(InventoryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete \"" + item.getName() + "\"?")
                .setMessage("This cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> deleteItem(item))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem(InventoryItem item) {
        AppExecutor.get().execute(() -> {
            db.inventoryDao().deleteItem(item);
            db.stockLogDao().insert(new StockLog(
                    item.getName(),
                    "DELETED",
                    "Removed item with qty " + item.getQuantity(),
                    System.currentTimeMillis()
            ));
            InventoryAnalytics.recordTodaySnapshot(this);
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                allItems.remove(item);
                filterItems(searchBox.getText() != null ? searchBox.getText().toString() : "");
                setResult(RESULT_OK);
            });
        });
    }

    // ── Adapter ──────────────────────────────────────────────────────────────

    class DeleteAdapter extends RecyclerView.Adapter<DeleteAdapter.VH> {

        private final List<InventoryItem> list = new ArrayList<>();

        void setItems(List<InventoryItem> items) {
            list.clear();
            list.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_inventory, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int pos) {
            InventoryItem item = list.get(pos);
            h.name.setText(item.getName());
            h.qty.setText("Qty: " + item.getQuantity());
            h.category.setText(item.getCategory());
            String initial = (item.getName() == null || item.getName().isEmpty())
                    ? "?" : String.valueOf(item.getName().charAt(0)).toUpperCase(Locale.ROOT);
            h.initial.setText(initial);
            h.lowBadge.setVisibility(
                    item.getQuantity() <= item.getLowStockThreshold() ? View.VISIBLE : View.GONE);
            // hide price/meta/checkbox — not needed in delete list
            h.price.setVisibility(View.GONE);
            h.meta.setVisibility(View.GONE);
            h.checkBox.setVisibility(View.GONE);
            h.itemView.setOnClickListener(v -> confirmDelete(item));
        }

        @Override
        public int getItemCount() { return list.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView name, qty, category, initial, lowBadge, price, meta;
            android.widget.CheckBox checkBox;
            VH(View v) {
                super(v);
                name      = v.findViewById(R.id.itemNameText);
                qty       = v.findViewById(R.id.itemQuantityText);
                price     = v.findViewById(R.id.itemPriceText);
                category  = v.findViewById(R.id.txt_category_badge);
                initial   = v.findViewById(R.id.txt_item_initial);
                lowBadge  = v.findViewById(R.id.txt_low_stock_badge);
                meta      = v.findViewById(R.id.itemMetaText);
                checkBox  = v.findViewById(R.id.itemCheckBox);
            }
        }
    }
}
