package com.example.v2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportsFragment extends Fragment {

    public ReportsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadReport(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) loadReport(getView());
    }

    private void loadReport(View view) {
        TextView txtTotalItems = view.findViewById(R.id.report_total_items);
        TextView txtTotalValue = view.findViewById(R.id.report_total_value);
        TextView txtLowStock = view.findViewById(R.id.report_low_stock);
        LinearLayout categoryContainer = view.findViewById(R.id.report_category_container);
        LinearLayout supplierContainer = view.findViewById(R.id.report_supplier_container);
        LinearLayout activityContainer = view.findViewById(R.id.report_activity_container);
        PieChart pieChart = view.findViewById(R.id.chart_category);
        BarChart barChart = view.findViewById(R.id.chart_stock_levels);
        View emptyState = view.findViewById(R.id.reports_empty_state);

        AppExecutor.get().execute(() -> {
            InventoryDatabase db = InventoryDatabase.getDatabase(requireContext());
            List<InventoryItem> items = db.inventoryDao().getAllItems();
            List<StockLog> logs = db.stockLogDao().getRecentLogs();

            int totalUnits = 0;
            int lowStockCount = 0;
            double totalValue = 0.0;
            Map<String, Integer> categoryCount = new HashMap<>();
            Map<String, Integer> supplierCount = new HashMap<>();

            for (InventoryItem item : items) {
                totalUnits += item.getQuantity();
                totalValue += item.getPrice() * item.getQuantity();
                if (item.getQuantity() <= item.getLowStockThreshold()) lowStockCount++;
                categoryCount.merge(emptyAs(item.getCategory(), "Uncategorized"), item.getQuantity(), Integer::sum);
                supplierCount.merge(emptyAs(item.getSupplier(), "No supplier"), item.getQuantity(), Integer::sum);
            }

            final int finalTotalUnits = totalUnits;
            final int finalLowStockCount = lowStockCount;
            final double finalTotalValue = totalValue;

            requireActivity().runOnUiThread(() -> {
                boolean empty = items.isEmpty();
                emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
                txtTotalItems.setText(String.valueOf(finalTotalUnits));
                txtTotalValue.setText(CurrencyFormatter.format(requireContext(), finalTotalValue));
                txtLowStock.setText(String.valueOf(finalLowStockCount));

                renderBreakdown(categoryContainer, categoryCount, "units");
                renderBreakdown(supplierContainer, supplierCount, "units");
                renderActivity(activityContainer, logs);
                renderPieChart(pieChart, empty ? new java.util.HashMap<>() : categoryCount);
                renderBarChart(barChart, items);
            });
        });
    }

    private void renderBreakdown(LinearLayout container, Map<String, Integer> values, String suffix) {
        container.removeAllViews();
        if (values.isEmpty()) {
            container.addView(simpleText("No data available", R.color.text_hint, 14));
            return;
        }

        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            View row = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_category_row, container, false);
            ((TextView) row.findViewById(android.R.id.text1)).setText(entry.getKey());
            ((TextView) row.findViewById(android.R.id.text2)).setText(entry.getValue() + " " + suffix);
            container.addView(row);
        }
    }

    private void renderActivity(LinearLayout container, List<StockLog> logs) {
        container.removeAllViews();
        if (logs.isEmpty()) {
            container.addView(simpleText("No activity yet", R.color.text_hint, 14));
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
        int max = Math.min(8, logs.size());
        for (int i = 0; i < max; i++) {
            StockLog log = logs.get(i);
            TextView tv = simpleText(
                    log.getAction() + " - " + log.getItemName() + "\n"
                            + log.getDetail() + " - " + formatter.format(new Date(log.getTimestamp())),
                    R.color.text_body,
                    13
            );
            tv.setPadding(0, 10, 0, 10);
            container.addView(tv);
        }
    }

    private void renderPieChart(PieChart chart, Map<String, Integer> values) {
        if (values.isEmpty()) {
            chart.clear();
            chart.setNoDataText("No category data");
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(chartColors());
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(11f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));

        chart.setUsePercentValues(true);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setEntryLabelColor(ContextCompat.getColor(requireContext(), R.color.text_body));
        chart.setEntryLabelTextSize(11f);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.invalidate();
    }

    private void renderBarChart(BarChart chart, List<InventoryItem> items) {
        if (items.isEmpty()) {
            chart.clear();
            chart.setNoDataText("No stock data");
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int max = Math.min(8, items.size());
        for (int i = 0; i < max; i++) {
            InventoryItem item = items.get(i);
            entries.add(new BarEntry(i, item.getQuantity()));
            labels.add(shortLabel(item.getName()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Units");
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.brand_primary));
        dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.text_body));
        dataSet.setValueTextSize(10f);

        chart.setData(new BarData(dataSet));
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        chart.invalidate();
    }

    private int[] chartColors() {
        return new int[] {
                ContextCompat.getColor(requireContext(), R.color.brand_primary),
                ContextCompat.getColor(requireContext(), R.color.color_success),
                ContextCompat.getColor(requireContext(), R.color.color_warning),
                ContextCompat.getColor(requireContext(), R.color.color_error),
                ContextCompat.getColor(requireContext(), R.color.color_info)
        };
    }

    private TextView simpleText(String text, int colorRes, int sizeSp) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextSize(sizeSp);
        tv.setTextColor(ContextCompat.getColor(requireContext(), colorRes));
        return tv;
    }

    private String emptyAs(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private String shortLabel(String value) {
        if (value == null) return "";
        return value.length() <= 8 ? value : value.substring(0, 8);
    }
}
