package com.example.v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    LinearLayout cardAddItem, cardViewInventory, cardScanItem, cardDeleteItem;
    TextView txtItemsInStock, txtLowStock, txtTotalValue;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        cardAddItem = view.findViewById(R.id.card_add_item);
        cardViewInventory = view.findViewById(R.id.card_view_inventory);
        cardScanItem = view.findViewById(R.id.card_scan_item);
        cardDeleteItem = view.findViewById(R.id.card_delete_item);

        txtItemsInStock = view.findViewById(R.id.txt_items_in_stock);
        txtLowStock = view.findViewById(R.id.txt_low_stock);
        txtTotalValue = view.findViewById(R.id.txt_total_value);

        // TODO: Replace these with dynamic values from your database
        txtItemsInStock.setText(""); // or "Loading..."
        txtLowStock.setText("");
        txtTotalValue.setText("");

        cardAddItem.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddItemActivity.class)));
        cardViewInventory.setOnClickListener(v -> startActivity(new Intent(getActivity(), InventoryActivity.class)));
        cardScanItem.setOnClickListener(v -> startActivity(new Intent(getActivity(), ScanActivity.class)));
        cardDeleteItem.setOnClickListener(v -> startActivity(new Intent(getActivity(), DeleteItemActivity.class)));

        return view;
    }
}
