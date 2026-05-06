package com.example.v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private LinearLayout emptyStateLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.inventoryRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        progressBar = findViewById(R.id.progressBar);
        FloatingActionButton fab = findViewById(R.id.fabAddItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(this);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> startActivity(new Intent(this, AddItemActivity.class)));

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        progressBar.setVisibility(View.VISIBLE);
        AppExecutor.get().execute(() -> {
            List<InventoryItem> items = InventoryDatabase.getDatabase(this)
                    .inventoryDao().getAllItems();
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                adapter.setInventoryList(items);
                boolean empty = items.isEmpty();
                recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
                emptyStateLayout.setVisibility(empty ? View.VISIBLE : View.GONE);
            });
        });
    }
}
