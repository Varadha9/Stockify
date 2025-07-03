package com.example.v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private TextView emptyText;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddItem;

    private InventoryDatabase database;
    private InventoryDao inventoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Initialize views
        recyclerView = findViewById(R.id.inventoryRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        progressBar = findViewById(R.id.progressBar);
        fabAddItem = findViewById(R.id.fabAddItem);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(this);
        recyclerView.setAdapter(adapter);

        // Setup Room DB
        database = InventoryDatabase.getDatabase(this);

        inventoryDao = database.inventoryDao();

        // Add item button
        fabAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        // Load items
        loadInventoryItems();
    }

    private void loadInventoryItems() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            List<InventoryItem> items = inventoryDao.getAllItems();
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (items.isEmpty()) {
                    emptyText.setVisibility(View.VISIBLE);
                    adapter.setInventoryList(items);
                } else {
                    emptyText.setVisibility(View.GONE);
                    adapter.setInventoryList(items);
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventoryItems(); // Refresh when coming back
    }
}
