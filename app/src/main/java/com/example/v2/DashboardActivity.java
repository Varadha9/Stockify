package com.example.v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView totalItemsText, lowStockText, totalValueText, emptyActivitiesText;
    private RecyclerView recentRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        totalItemsText      = findViewById(R.id.totalItemsText);
        lowStockText        = findViewById(R.id.lowStockText);
        totalValueText      = findViewById(R.id.totalValueText);
        emptyActivitiesText = findViewById(R.id.emptyActivitiesText);
        recentRecycler      = findViewById(R.id.recentRecycler);
        recentRecycler.setLayoutManager(new LinearLayoutManager(this));
        recentRecycler.setNestedScrollingEnabled(false);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = prefs.getString("regName", "");
        TextView txtWelcome = findViewById(R.id.txt_welcome);
        txtWelcome.setText(name.isEmpty()
                ? getString(R.string.welcome_generic)
                : getString(R.string.welcome_name, name));

        findViewById(R.id.addItemCard).setOnClickListener(
                v -> startActivity(new Intent(this, AddItemActivity.class)));
        findViewById(R.id.viewInventoryCard).setOnClickListener(
                v -> startActivity(new Intent(this, InventoryActivity.class)));
        findViewById(R.id.scanItemCard).setOnClickListener(
                v -> startActivity(new Intent(this, ScanActivity.class)));
        findViewById(R.id.deleteItemCard).setOnClickListener(
                v -> startActivity(new Intent(this, DeleteItemActivity.class)));

        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        nav.setSelectedItemId(R.id.nav_dashboard);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inventory)
                startActivity(new Intent(this, InventoryActivity.class));
            else if (id == R.id.nav_scan)
                startActivity(new Intent(this, ScanActivity.class));
            else if (id == R.id.nav_reports || id == R.id.nav_settings)
                startActivity(new Intent(this, MainActivity.class));
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        AppExecutor.get().execute(() -> {
            InventoryDatabase db = InventoryDatabase.getDatabase(this);
            int total  = db.inventoryDao().getTotalStockCount();
            int low    = db.inventoryDao().getLowStockCount();
            double val = db.inventoryDao().getTotalValue();
            List<StockLog> logs = db.stockLogDao().getRecentLogs();
            runOnUiThread(() -> {
                totalItemsText.setText(String.format(java.util.Locale.getDefault(), "%d", total));
                lowStockText.setText(String.format(java.util.Locale.getDefault(), "%d", low));
                totalValueText.setText(CurrencyFormatter.formatRounded(this, val));
                if (logs.isEmpty()) {
                    recentRecycler.setVisibility(View.GONE);
                    emptyActivitiesText.setVisibility(View.VISIBLE);
                } else {
                    recentRecycler.setVisibility(View.VISIBLE);
                    emptyActivitiesText.setVisibility(View.GONE);
                    recentRecycler.setAdapter(new RecentActivityAdapter(this, logs));
                }
            });
        });
    }
}
