package com.stockify.inventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private final DashboardFragment dashboardFragment = new DashboardFragment();
    private final InventoryFragment inventoryFragment = new InventoryFragment();
    private final ReportsFragment reportsFragment = new ReportsFragment();
    private final SettingsFragment settingsFragment = new SettingsFragment();
    private BottomNavigationView bottomNav;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                loadFragment(dashboardFragment);
            } else if (id == R.id.nav_inventory) {
                loadFragment(inventoryFragment);
            } else if (id == R.id.nav_scan) {
                startActivity(new Intent(this, ScanActivity.class));
                return true;
            } else if (id == R.id.nav_reports) {
                loadFragment(reportsFragment);
            } else if (id == R.id.nav_settings) {
                loadFragment(settingsFragment);
            }
            return true;
        });

        bottomNav.setSelectedItemId(R.id.nav_dashboard);
        requestNotificationPermissionIfNeeded();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("sort_low_stock", false)) {
            inventoryFragment.preselectLowStockSort();
            bottomNav.setSelectedItemId(R.id.nav_inventory);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBadges();
    }

    public void switchToInventoryTab() {
        bottomNav.setSelectedItemId(R.id.nav_inventory);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void updateBadges() {
        AppExecutor.get().execute(() -> {
            InventoryDatabase db = InventoryDatabase.getDatabase(this);
            int totalItems = db.inventoryDao().getAllItems().size();
            int lowStock = db.inventoryDao().getLowStockCount();
            runOnUiThread(() -> {
                setBadge(R.id.nav_inventory, totalItems);
                setBadge(R.id.nav_reports, lowStock);
            });
        });
    }

    private void setBadge(int menuItemId, int count) {
        if (bottomNav == null) return;
        if (count <= 0) {
            bottomNav.removeBadge(menuItemId);
        } else {
            BadgeDrawable badge = bottomNav.getOrCreateBadge(menuItemId);
            badge.setVisible(true);
            badge.setNumber(count);
        }
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
