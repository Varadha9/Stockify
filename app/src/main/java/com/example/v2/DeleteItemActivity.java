package com.example.v2;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.concurrent.Executors;

public class DeleteItemActivity extends AppCompatActivity {

    private AutoCompleteTextView itemToDelete;
    private Button deleteButton;
    private InventoryDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);

        // Initialize database
        db = InventoryDatabase.getDatabase(this);

        // View bindings
        TextView title = findViewById(R.id.delete_title);
        itemToDelete = findViewById(R.id.itemToDelete);
        deleteButton = findViewById(R.id.deleteButton);

        title.setText("Delete Inventory Item");
        itemToDelete.setHint("Search or enter item name");

        // Load item names into dropdown
        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> itemNames = db.inventoryDao().getAllItemNames();
            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        DeleteItemActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        itemNames
                );
                itemToDelete.setAdapter(adapter);
                itemToDelete.setThreshold(1); // Start suggesting from first character
            });
        });

        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> {
            String itemName = itemToDelete.getText().toString().trim();

            if (itemName.isEmpty()) {
                Toast.makeText(this, "Please enter an item name.", Toast.LENGTH_SHORT).show();
            } else {
                deleteItemFromDatabase(itemName);
            }
        });
    }

    private void deleteItemFromDatabase(String itemName) {
        Executors.newSingleThreadExecutor().execute(() -> {
            InventoryItem item = db.inventoryDao().findItemByName(itemName);

            if (item == null) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Item not found!", Toast.LENGTH_SHORT).show()
                );
            } else {
                db.inventoryDao().deleteItem(item);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Item deleted successfully!", Toast.LENGTH_SHORT).show();
                    itemToDelete.setText(""); // Clear input
                    setResult(RESULT_OK);
                    finish();
                });
            }
        });
    }
}
