package com.example.v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    private ActivityResultLauncher<Intent> backupLauncher;
    private ActivityResultLauncher<Intent> restoreLauncher;

    private final String[] currencyLabels = {
            "INR (\u20B9)",
            "USD ($)",
            "EUR (\u20AC)",
            "GBP (\u00A3)",
            "JPY (\u00A5)"
    };

    private final String[] currencySymbols = {
            "\u20B9", "$", "\u20AC", "\u00A3", "\u00A5"
    };

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backupLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null
                            && result.getData().getData() != null) {
                        exportJsonBackup(result.getData().getData());
                    }
                });
        restoreLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null
                            && result.getData().getData() != null) {
                        confirmRestore(result.getData().getData());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        String email = prefs.getString("userEmail", "Not set");
        String name = prefs.getString("regName", "User");

        TextView txtName = view.findViewById(R.id.txt_settings_name);
        TextView txtEmail = view.findViewById(R.id.txt_settings_email);
        TextView txtAvatar = view.findViewById(R.id.txt_avatar_initial);

        txtName.setText(name.isEmpty() ? "User" : name);
        txtEmail.setText(email);
        txtAvatar.setText(name.isEmpty() ? "U" : String.valueOf(name.charAt(0)).toUpperCase(Locale.ROOT));

        setupCurrencyDropdown(view.findViewById(R.id.currencyAutoComplete));

        view.findViewById(R.id.btn_export_csv_settings).setOnClickListener(v -> exportCsv());
        view.findViewById(R.id.btn_backup_json).setOnClickListener(v -> startJsonBackup());
        view.findViewById(R.id.btn_restore_json).setOnClickListener(v -> startJsonRestore());
        view.findViewById(R.id.btn_change_password).setOnClickListener(v -> showChangePasswordDialog(prefs));
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> logout(prefs));

        // Privacy Policy — required by Google Play
        View btnPrivacy = view.findViewById(R.id.btn_privacy_policy);
        if (btnPrivacy != null) {
            btnPrivacy.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.privacy_policy_url)));
                if (browserIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(browserIntent);
                }
            });
        }
    }

    private void setupCurrencyDropdown(AutoCompleteTextView currencyAutoComplete) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                currencyLabels
        );
        currencyAutoComplete.setAdapter(adapter);
        currencyAutoComplete.setText(labelForSymbol(CurrencyFormatter.getSymbol(requireContext())), false);
        currencyAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            CurrencyFormatter.setSymbol(requireContext(), currencySymbols[position]);
            Toast.makeText(requireContext(), R.string.currency_updated, Toast.LENGTH_SHORT).show();
        });
    }

    private String labelForSymbol(String symbol) {
        for (int i = 0; i < currencySymbols.length; i++) {
            if (currencySymbols[i].equals(symbol)) return currencyLabels[i];
        }
        return currencyLabels[0];
    }

    private void exportCsv() {
        AppExecutor.get().execute(() -> {
            try {
                InventoryDatabase db = InventoryDatabase.getDatabase(requireContext());
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

    private void startJsonBackup() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "stockify_backup_" + timestamp + ".json");
        backupLauncher.launch(intent);
    }

    private void startJsonRestore() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        restoreLauncher.launch(intent);
    }

    private void exportJsonBackup(Uri uri) {
        AppExecutor.get().execute(() -> {
            try {
                InventoryDatabase db = InventoryDatabase.getDatabase(requireContext());
                InventoryBackupManager.BackupData data = new InventoryBackupManager.BackupData();
                data.items.addAll(db.inventoryDao().getAllItems());
                data.logs.addAll(db.stockLogDao().getAllLogs());
                data.snapshots.addAll(db.valueSnapshotDao().getAllSnapshots());
                data.currencySymbol = CurrencyFormatter.getSymbol(requireContext());
                InventoryBackupManager.writeBackup(requireContext(), uri, data);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), R.string.backup_saved, Toast.LENGTH_SHORT).show());
            } catch (IOException | JSONException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), R.string.backup_failed, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void confirmRestore(Uri uri) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Restore backup?")
                .setMessage("This replaces current inventory, logs, and snapshots.")
                .setPositiveButton("Restore", (dialog, which) -> restoreJson(uri))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void restoreJson(Uri uri) {
        AppExecutor.get().execute(() -> {
            try {
                InventoryBackupManager.BackupData data = InventoryBackupManager.readBackup(requireContext(), uri);
                InventoryDatabase db = InventoryDatabase.getDatabase(requireContext());
                db.runInTransaction(() -> {
                    db.inventoryDao().deleteAll();
                    db.stockLogDao().deleteAll();
                    db.valueSnapshotDao().deleteAll();
                    if (!data.items.isEmpty()) db.inventoryDao().insertItems(data.items);
                    if (!data.logs.isEmpty()) db.stockLogDao().insertLogs(data.logs);
                    if (!data.snapshots.isEmpty()) db.valueSnapshotDao().insertSnapshots(data.snapshots);
                });
                CurrencyFormatter.setSymbol(requireContext(), data.currencySymbol);
                InventoryAnalytics.recordTodaySnapshot(requireContext());
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), R.string.restore_success, Toast.LENGTH_SHORT).show());
            } catch (IOException | JSONException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), R.string.restore_failed, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showChangePasswordDialog(SharedPreferences prefs) {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 8);

        android.widget.EditText etCurrent = new android.widget.EditText(requireContext());
        etCurrent.setHint("Current password");
        etCurrent.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etCurrent);

        android.widget.EditText etNew = new android.widget.EditText(requireContext());
        etNew.setHint("New password (min 6 chars)");
        etNew.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNew);

        android.widget.EditText etConfirm = new android.widget.EditText(requireContext());
        etConfirm.setHint("Confirm new password");
        etConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etConfirm);

        new AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(layout)
                .setPositiveButton("Update", (dialog, which) -> {
                    String current = etCurrent.getText().toString();
                    String newPass = etNew.getText().toString();
                    String confirm = etConfirm.getText().toString();

                    String savedHash = prefs.getString("regPassword", "");
                    if (!PasswordUtils.verifyPassword(current, savedHash)) {
                        Toast.makeText(requireContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPass.length() < 6) {
                        Toast.makeText(requireContext(), "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPass.equals(confirm)) {
                        Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    prefs.edit().putString("regPassword", PasswordUtils.hashNewPassword(newPass)).apply();
                    Toast.makeText(requireContext(), R.string.password_updated, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout(SharedPreferences prefs) {
        prefs.edit()
                .putBoolean("isLoggedIn", false)
                .remove("userEmail")
                .remove("regEmail")
                .remove("regPassword")
                .remove("regName")
                .apply();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
