package com.example.v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardFragment extends Fragment {

    private TextView txtItemsInStock, txtLowStock, txtTotalValue, txtNoRecent;
    private RecyclerView recyclerRecent;
    private LineChart trendChart;

    public DashboardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtItemsInStock = view.findViewById(R.id.txt_items_in_stock);
        txtLowStock     = view.findViewById(R.id.txt_low_stock);
        txtTotalValue   = view.findViewById(R.id.txt_total_value);
        txtNoRecent     = view.findViewById(R.id.txt_no_recent);
        recyclerRecent  = view.findViewById(R.id.recycler_recent);
        trendChart      = view.findViewById(R.id.chart_value_trend);

        recyclerRecent.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerRecent.setNestedScrollingEnabled(false);

        // Greet user
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
        String name  = prefs.getString("regName", "");
        String email = prefs.getString("userEmail", "");

        TextView txtGreeting  = view.findViewById(R.id.txt_greeting);
        TextView txtUsername  = view.findViewById(R.id.txt_username);
        txtGreeting.setText(getGreeting());
        txtUsername.setText(name.isEmpty()
                ? (email.isEmpty() ? getString(R.string.welcome_back) : email)
                : getString(R.string.hey_name, name));

        // Quick action buttons
        view.findViewById(R.id.card_add_item).setOnClickListener(
                v -> startActivity(new Intent(getActivity(), AddItemActivity.class)));
        view.findViewById(R.id.card_view_inventory).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).switchToInventoryTab();
        });
        view.findViewById(R.id.card_scan_item).setOnClickListener(
                v -> startActivity(new Intent(getActivity(), ScanActivity.class)));
        view.findViewById(R.id.card_delete_item).setOnClickListener(
                v -> startActivity(new Intent(getActivity(), DeleteItemActivity.class)));
        view.findViewById(R.id.txt_view_all).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).switchToInventoryTab();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        if (!isAdded()) return;
        InventoryDatabase db = InventoryDatabase.getDatabase(requireContext());
        AppExecutor.get().execute(() -> {
            InventoryAnalytics.recordTodaySnapshot(requireContext());
            int totalItems  = db.inventoryDao().getTotalStockCount();
            int lowStock    = db.inventoryDao().getLowStockCount();
            double totalVal = db.inventoryDao().getTotalValue();
            // Use StockLog for recent activity — semantically correct for a dashboard feed
            List<StockLog> recentLogs = db.stockLogDao().getRecentLogs();
            List<ValueSnapshot> snapshots = db.valueSnapshotDao().getLastSevenSnapshots();

            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;
                txtItemsInStock.setText(String.format(java.util.Locale.getDefault(), "%d", totalItems));
                txtLowStock.setText(String.format(java.util.Locale.getDefault(), "%d", lowStock));
                // Highlight low stock count in warning color when > 0
                txtLowStock.setTextColor(ContextCompat.getColor(requireContext(),
                        lowStock > 0 ? R.color.color_warning : R.color.text_heading));
                txtTotalValue.setText(CurrencyFormatter.formatRounded(requireContext(), totalVal));
                renderTrend(snapshots);

                if (recentLogs.isEmpty()) {
                    recyclerRecent.setVisibility(View.GONE);
                    txtNoRecent.setVisibility(View.VISIBLE);
                } else {
                    recyclerRecent.setVisibility(View.VISIBLE);
                    txtNoRecent.setVisibility(View.GONE);
                    recyclerRecent.setAdapter(
                            new RecentActivityAdapter(requireContext(), recentLogs));
                }
            });
        });
    }

    private void renderTrend(List<ValueSnapshot> snapshots) {
        if (trendChart == null || !isAdded()) return;
        if (snapshots.isEmpty()) {
            trendChart.clear();
            trendChart.setNoDataText("No trend data yet");
            trendChart.setNoDataTextColor(
                    ContextCompat.getColor(requireContext(), R.color.text_hint));
            return;
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < snapshots.size(); i++) {
            entries.add(new Entry(i, (float) snapshots.get(i).getTotalValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Value");
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.brand_primary));
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.brand_primary));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ContextCompat.getColor(requireContext(), R.color.brand_primary_surface));
        dataSet.setFillAlpha(80);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        trendChart.setData(new LineData(dataSet));
        trendChart.getDescription().setEnabled(false);
        trendChart.getLegend().setEnabled(false);
        trendChart.getAxisRight().setEnabled(false);
        trendChart.getAxisLeft().setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_secondary));
        trendChart.getAxisLeft().setGridColor(
                ContextCompat.getColor(requireContext(), R.color.neutral_100));
        trendChart.getXAxis().setEnabled(false);
        trendChart.setDrawGridBackground(false);
        trendChart.setBackgroundColor(Color.TRANSPARENT);
        trendChart.setTouchEnabled(false);
        trendChart.animateX(600);
        trendChart.invalidate();
    }

    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) return "Good Morning";
        if (hour < 17) return "Good Afternoon";
        return "Good Evening";
    }
}
