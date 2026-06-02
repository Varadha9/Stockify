package com.example.v2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {

    private InventoryAdapter adapter;
    private LinearLayout emptyState;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView txtItemCountLabel;
    private ChipGroup categoryChipGroup;
    private MaterialButton btnBulkAdjust;
    private InventoryDatabase db;

    public InventoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = InventoryDatabase.getDatabase(requireContext());
        recyclerView = view.findViewById(R.id.inventoryRecyclerView);
        emptyState = view.findViewById(R.id.empty_state);
        progressBar = view.findViewById(R.id.progressBar);
        txtItemCountLabel = view.findViewById(R.id.txt_item_count_label);
        categoryChipGroup = view.findViewById(R.id.chip_group_categories);
        TextInputEditText searchBox = view.findViewById(R.id.search_inventory);
        MaterialButton btnSort = view.findViewById(R.id.btn_sort);
        btnBulkAdjust = view.findViewById(R.id.btn_bulk_adjust);
        MaterialButton btnExportCsv = view.findViewById(R.id.btn_export_csv);

        adapter = new InventoryAdapter(requireContext(), this::onSelectionChanged);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        if (pendingLowStockSort) {
            pendingLowStockSort = false;
            adapter.setSortMode(InventoryAdapter.SORT_LOW_STOCK);
        }

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                refreshEmptyState();
            }
        });

        btnSort.setOnClickListener(v -> showSortDialog());
        btnBulkAdjust.setOnClickListener(v -> handleBulkButton());
        btnExportCsv.setOnClickListener(v -> exportCsv());

        // Header + Add button
        view.findViewById(R.id.btn_add_item_header).setOnClickListener(
                v -> startActivity(new Intent(requireContext(), AddItemActivity.class)));

        loadItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) loadItems();
    }

    private void loadItems() {
        progressBar.setVisibility(View.VISIBLE);
        AppExecutor.get().execute(() -> {
            List<InventoryItem> items = db.inventoryDao().getAllItems();
            List<String> categories = db.inventoryDao().getAllCategories();
            requireActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                adapter.setInventoryList(items);
                setupCategoryChips(categories);
                txtItemCountLabel.setText(getResources().getQuantityString(
                        R.plurals.item_count, items.size(), items.size()));
                refreshEmptyState();
            });
        });
    }

    private void setupCategoryChips(List<String> categories) {
        categoryChipGroup.setOnCheckedChangeListener(null);
        categoryChipGroup.removeAllViews();
        addCategoryChip("All", true);
        for (String category : categories) {
            addCategoryChip(category, false);
        }
        adapter.setCategoryFilter("All");
        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            adapter.setCategoryFilter(chip == null ? "All" : chip.getText().toString());
            refreshEmptyState();
        });
    }

    private void addCategoryChip(String label, boolean checked) {
        Chip chip = new Chip(requireContext());
        chip.setId(View.generateViewId());
        chip.setText(label);
        chip.setCheckable(true);
        chip.setChecked(checked);
        chip.setChipBackgroundColorResource(R.color.brand_primary_surface);
        chip.setTextColor(getResources().getColor(R.color.brand_primary, null));
        categoryChipGroup.addView(chip);
    }

    private void showSortDialog() {
        String[] labels = {
                "Name A-Z",
                "Low stock first",
                "Quantity low to high",
                "Quantity high to low",
                "Price low to high",
                "Price high to low"
        };
        new AlertDialog.Builder(requireContext())
                .setTitle("Sort inventory")
                .setItems(labels, (dialog, which) -> {
                    adapter.setSortMode(which);
                    refreshEmptyState();
                })
                .show();
    }

    private void handleBulkButton() {
        if (!adapter.isSelectionMode()) {
            adapter.setSelectionMode(true);
            Toast.makeText(requireContext(), "Select items to update", Toast.LENGTH_SHORT).show();
            return;
        }
        if (adapter.getSelectedCount() == 0) {
            adapter.setSelectionMode(false);
            return;
        }
        showBulkDialog(new ArrayList<>(adapter.getSelectedIds()));
    }

    private void showBulkDialog(List<Integer> selectedIds) {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        input.setHint("Use 5 to add, -2 to subtract");
        input.setPadding(32, 16, 32, 16);

        new AlertDialog.Builder(requireContext())
                .setTitle("Adjust " + selectedIds.size() + " item(s)")
                .setView(input)
                .setPositiveButton("Next", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    if (value.isEmpty()) return;
                    try {
                        int delta = Integer.parseInt(value);
                        confirmBulkDelta(selectedIds, delta);
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmBulkDelta(List<Integer> selectedIds, int delta) {
        String sign = delta > 0 ? "+" + delta : String.valueOf(delta);
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm bulk adjust")
                .setMessage("Apply " + sign + " to " + selectedIds.size() + " item(s)?")
                .setPositiveButton("Apply", (d, w) -> applyBulkDelta(selectedIds, delta))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void applyBulkDelta(List<Integer> selectedIds, int delta) {
        AppExecutor.get().execute(() -> {
            db.inventoryDao().adjustQuantityForIds(selectedIds, delta);
            db.stockLogDao().insert(new StockLog(
                    "Bulk update",
                    "BULK_QTY_CHANGED",
                    "Adjusted " + selectedIds.size() + " item(s) by " + signed(delta),
                    System.currentTimeMillis()
            ));
            InventoryAnalytics.recordTodaySnapshot(requireContext());
            requireActivity().runOnUiThread(() -> {
                adapter.setSelectionMode(false);
                Toast.makeText(requireContext(), R.string.bulk_update_applied, Toast.LENGTH_SHORT).show();
                loadItems();
            });
        });
    }

    private void exportCsv() {
        AppExecutor.get().execute(() -> {
            try {
                Intent shareIntent = CsvExporter.export(requireContext(), db.inventoryDao().getAllItems());
                requireActivity().runOnUiThread(() -> {
                    if (shareIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                        startActivity(shareIntent);
                    } else {
                        Toast.makeText(requireContext(), "No app available to share CSV", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "CSV export failed", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void onSelectionChanged(int count) {
        if (btnBulkAdjust == null) return;
        if (!adapter.isSelectionMode()) {
            btnBulkAdjust.setText("Bulk");
        } else if (count == 0) {
            btnBulkAdjust.setText("Cancel");
        } else {
            btnBulkAdjust.setText("Apply (" + count + ")");
        }
    }

    private boolean pendingLowStockSort = false;

    public void preselectLowStockSort() {
        if (adapter != null) {
            adapter.setSortMode(InventoryAdapter.SORT_LOW_STOCK);
            refreshEmptyState();
        } else {
            pendingLowStockSort = true;
        }
    }

    private void refreshEmptyState() {
        boolean empty = adapter.getItemCount() == 0;
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }
}
