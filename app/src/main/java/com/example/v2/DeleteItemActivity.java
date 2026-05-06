package com.example.v2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DeleteItemActivity extends AppCompatActivity {

    private AutoCompleteTextView itemToDelete;
    private InventoryDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);

        db = InventoryDatabase.getDatabase(this);

        TextView title = findViewById(R.id.delete_title);
        itemToDelete = findViewById(R.id.itemToDelete);
        Button deleteButton = findViewById(R.id.deleteButton);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        title.setText("Delete Inventory Item");
        itemToDelete.setHint("Search or enter item name");

        AppExecutor.get().execute(() -> {
            List<String> itemNames = db.inventoryDao().getAllItemNames();
            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        itemNames
                );
                itemToDelete.setAdapter(adapter);
                itemToDelete.setThreshold(1);
            });
        });

        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());

        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> {
            String itemName = itemToDelete.getText().toString().trim();
            if (itemName.isEmpty()) {
                Toast.makeText(this, "Please enter an item name.", Toast.LENGTH_SHORT).show();
            } else {
                checkAndDelete(itemName);
            }
        });
    }

    private void checkAndDelete(String itemName) {
        AppExecutor.get().execute(() -> {
            List<InventoryItem> allItems = db.inventoryDao().getAllItems();
            long count = 0;
            for (InventoryItem item : allItems) {
                if (itemName.equalsIgnoreCase(item.getName())) count++;
            }

            final long duplicateCount = count;
            runOnUiThread(() -> {
                if (duplicateCount == 0) {
                    Toast.makeText(this, "Item not found!", Toast.LENGTH_SHORT).show();
                } else if (duplicateCount > 1) {
                    new AlertDialog.Builder(this)
                            .setTitle("Multiple items found")
                            .setMessage(duplicateCount + " items named \"" + itemName + "\" exist. "
                                    + "Please edit each item individually to delete the correct one.")
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Delete \"" + itemName + "\"?")
                            .setMessage("This cannot be undone.")
                            .setPositiveButton("Delete", (d, w) -> deleteItemFromDatabase(itemName))
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        });
    }

    private List<InventoryItem> findAllByName(String name) {
        return db.inventoryDao().getAllItems();
    }

    private void deleteItemFromDatabase(String itemName) {
        AppExecutor.get().execute(() -> {
            InventoryItem item = db.inventoryDao().findItemByName(itemName);
            if (item == null) {
                runOnUiThread(() ->
                        Toast.makeText(this, R.string.invalid_item, Toast.LENGTH_SHORT).show());
            } else {
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
                    itemToDelete.setText("");
                    setResult(RESULT_OK);
                    finish();
                });
            }
        });
    }
}
