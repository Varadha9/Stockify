package com.stockify.inventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class EditItemActivity extends AppCompatActivity {

    private InventoryItem currentItem;
    private int currentQty = 0;

    private TextView txtCurrentQty;
    private ImageView itemImageView;
    private Uri imageUri;

    private TextInputEditText nameEditText, priceEditText, lowStockEditText,
            descriptionEditText, skuEditText, supplierEditText,
            costPriceEditText, reorderQtyEditText;
    private AutoCompleteTextView categoryAutoComplete;
    private TextInputLayout nameInputLayout, priceInputLayout, lowStockInputLayout, costPriceInputLayout;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri picked = result.getData().getData();
                    if (picked != null) {
                        imageUri = copyImageToInternal(picked);
                        itemImageView.setImageURI(imageUri);
                        itemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        itemImageView.setPadding(0, 0, 0, 0);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        int itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId == -1) { finish(); return; }

        initViews();

        AppExecutor.get().execute(() -> {
            currentItem = InventoryDatabase.getDatabase(this).inventoryDao().findItemById(itemId);
            if (currentItem == null) { finish(); return; }
            runOnUiThread(this::populateFields);
        });
    }

    private void initViews() {
        findViewById(R.id.backButton).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentItem != null && hasUnsavedChanges()) {
                    new AlertDialog.Builder(EditItemActivity.this)
                            .setTitle(R.string.discard_changes_title)
                            .setMessage(R.string.discard_changes_message)
                            .setPositiveButton(R.string.discard, (d, w) -> finish())
                            .setNegativeButton(R.string.keep_editing, null)
                            .show();
                } else {
                    finish();
                }
            }
        });

        txtCurrentQty     = findViewById(R.id.txt_current_qty);
        itemImageView     = findViewById(R.id.itemImageView);
        nameEditText      = findViewById(R.id.nameEditText);
        priceEditText     = findViewById(R.id.priceEditText);
        lowStockEditText  = findViewById(R.id.lowStockEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        skuEditText       = findViewById(R.id.skuEditText);
        supplierEditText  = findViewById(R.id.supplierEditText);
        categoryAutoComplete = findViewById(R.id.categoryAutoComplete);

        nameInputLayout     = findViewById(R.id.nameInputLayout);
        priceInputLayout    = findViewById(R.id.priceInputLayout);
        lowStockInputLayout = findViewById(R.id.lowStockInputLayout);
        costPriceInputLayout = findViewById(R.id.costPriceInputLayout);
        priceInputLayout.setHint("Selling Price (" + CurrencyFormatter.getSymbol(this) + ")");
        costPriceInputLayout.setHint("Cost Price (" + CurrencyFormatter.getSymbol(this) + ")");

        costPriceEditText = findViewById(R.id.costPriceEditText);
        reorderQtyEditText = findViewById(R.id.reorderQtyEditText);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, InventoryItem.CATEGORIES);
        categoryAutoComplete.setAdapter(catAdapter);

        MaterialButton btnPlus  = findViewById(R.id.btn_qty_plus);
        MaterialButton btnMinus = findViewById(R.id.btn_qty_minus);
        MaterialButton btnSave  = findViewById(R.id.btn_save);
        MaterialButton btnDelete = findViewById(R.id.btn_delete_item);

        btnPlus.setOnClickListener(v -> updateQty(1));
        btnMinus.setOnClickListener(v -> updateQty(-1));
        btnSave.setOnClickListener(v -> saveChanges());
        btnDelete.setOnClickListener(v -> confirmDelete());
        android.view.ViewGroup imageContainer = (android.view.ViewGroup) itemImageView.getParent();
        imageContainer.setOnClickListener(v -> openImagePicker());
    }

    private boolean hasUnsavedChanges() {
        if (currentItem == null) return false;
        String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
        String priceStr = priceEditText.getText() != null ? priceEditText.getText().toString().trim() : "";
        double price = 0;
        try { price = Double.parseDouble(priceStr); } catch (NumberFormatException ignored) {}
        return !name.equals(currentItem.getName())
                || Double.compare(price, currentItem.getPrice()) != 0
                || currentQty != currentItem.getQuantity();
    }

    private void populateFields() {
        currentQty = currentItem.getQuantity();
        txtCurrentQty.setText(String.valueOf(currentQty));
        nameEditText.setText(currentItem.getName());
        priceEditText.setText(String.valueOf(currentItem.getPrice()));
        lowStockEditText.setText(String.valueOf(currentItem.getLowStockThreshold()));
        descriptionEditText.setText(currentItem.getDescription());
        categoryAutoComplete.setText(currentItem.getCategory(), false);
        skuEditText.setText(currentItem.getSku());
        supplierEditText.setText(currentItem.getSupplier());

        costPriceEditText.setText(String.valueOf(currentItem.getCostPrice()));
        reorderQtyEditText.setText(String.valueOf(currentItem.getReorderQuantity()));

        if (currentItem.getImagePath() != null && !currentItem.getImagePath().isEmpty()) {
            imageUri = Uri.parse(currentItem.getImagePath());
            itemImageView.setImageURI(imageUri);
            itemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            itemImageView.setPadding(0, 0, 0, 0);
        }
    }

    private void updateQty(int delta) {
        int newQty = currentQty + delta;
        if (newQty < 0) {
            Toast.makeText(this, R.string.error_qty_negative, Toast.LENGTH_SHORT).show();
            return;
        }
        currentQty = newQty;
        txtCurrentQty.setText(String.valueOf(currentQty));
    }

    private void saveChanges() {
        String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String category = categoryAutoComplete.getText().toString().trim();
        String priceStr = Objects.requireNonNull(priceEditText.getText()).toString().trim();
        String lowStr   = Objects.requireNonNull(lowStockEditText.getText()).toString().trim();
        String desc     = Objects.requireNonNull(descriptionEditText.getText()).toString().trim();

        boolean valid = true;
        if (TextUtils.isEmpty(name)) { nameInputLayout.setError("Required"); valid = false; }
        else nameInputLayout.setError(null);

        double price = 0;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                priceInputLayout.setError("Price cannot be negative");
                valid = false;
            } else {
                priceInputLayout.setError(null);
            }
        }
        catch (NumberFormatException e) { priceInputLayout.setError("Invalid price"); valid = false; }

        int lowStock = 0;
        try { lowStock = Integer.parseInt(lowStr); lowStockInputLayout.setError(null); }
        catch (NumberFormatException e) { lowStockInputLayout.setError("Invalid number"); valid = false; }

        if (!valid) return;

        String oldName = currentItem.getName();
        int oldQty = currentItem.getQuantity();
        double oldPrice = currentItem.getPrice();
        String oldCategory = currentItem.getCategory();
        final int newQty = currentQty;
        final double newPrice = price;

        currentItem.setName(name);
        currentItem.setCategory(category);
        currentItem.setPrice(newPrice);
        currentItem.setQuantity(newQty);
        currentItem.setLowStockThreshold(lowStock);
        currentItem.setDescription(desc);
        String newImagePath = imageUri != null ? imageUri.toString() : currentItem.getImagePath();
        if (imageUri != null && !imageUri.toString().equals(currentItem.getImagePath())) {
            deleteImageFile(currentItem.getImagePath());
        }
        currentItem.setImagePath(newImagePath);
        currentItem.setSku(skuEditText.getText() != null ? skuEditText.getText().toString().trim() : "");
        currentItem.setSupplier(supplierEditText.getText() != null ? supplierEditText.getText().toString().trim() : "");
        try { currentItem.setCostPrice(costPriceEditText.getText() != null && !costPriceEditText.getText().toString().isEmpty()
                ? Double.parseDouble(costPriceEditText.getText().toString()) : 0); } catch (NumberFormatException ignored) {}
        try { currentItem.setReorderQuantity(reorderQtyEditText.getText() != null && !reorderQtyEditText.getText().toString().isEmpty()
                ? Integer.parseInt(reorderQtyEditText.getText().toString()) : 0); } catch (NumberFormatException ignored) {}

        AppExecutor.get().execute(() -> {
            InventoryDatabase db = InventoryDatabase.getDatabase(this);
            db.inventoryDao().updateItem(currentItem);
            String detail = buildUpdateDetail(oldQty, newQty, oldPrice, newPrice, oldCategory, category);
            db.stockLogDao().insert(new StockLog(
                    oldName,
                    oldQty == newQty ? "UPDATED" : "QTY_CHANGED",
                    detail,
                    System.currentTimeMillis()
            ));
            InventoryAnalytics.recordTodaySnapshot(this);
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.item_updated, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    private String buildUpdateDetail(int oldQty, int newQty, double oldPrice, double newPrice,
                                     String oldCategory, String newCategory) {
        StringBuilder detail = new StringBuilder();
        if (oldQty != newQty) {
            detail.append("Qty: ").append(oldQty).append(" -> ").append(newQty);
        }
        if (Double.compare(oldPrice, newPrice) != 0) {
            if (detail.length() > 0) detail.append("; ");
            detail.append("Price changed");
        }
        if (!Objects.equals(oldCategory, newCategory)) {
            if (detail.length() > 0) detail.append("; ");
            detail.append("Category: ").append(oldCategory).append(" -> ").append(newCategory);
        }
        return detail.length() == 0 ? "Details updated" : detail.toString();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_delete_title, currentItem.getName()))
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(R.string.btn_delete, (d, w) -> deleteItem())
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void deleteItem() {
        AppExecutor.get().execute(() -> {
            InventoryDatabase db = InventoryDatabase.getDatabase(this);
            deleteImageFile(currentItem.getImagePath());
            db.inventoryDao().deleteItem(currentItem);
            db.stockLogDao().insert(new StockLog(
                    currentItem.getName(),
                    "DELETED",
                    "Removed item with qty " + currentItem.getQuantity(),
                    System.currentTimeMillis()
            ));
            InventoryAnalytics.recordTodaySnapshot(this);
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    private Uri copyImageToInternal(Uri source) {
        try {
            File dir = new File(getFilesDir(), "item_images");
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(dir, "img_" + System.currentTimeMillis() + ".jpg");
            try (InputStream in = getContentResolver().openInputStream(source);
                 FileOutputStream out = new FileOutputStream(dest)) {
                if (in == null) return source;
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }
            return Uri.fromFile(dest);
        } catch (IOException e) {
            return source;
        }
    }

    private void deleteImageFile(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) return;
        try {
            Uri uri = Uri.parse(imagePath);
            if ("file".equals(uri.getScheme())) {
                File file = new File(uri.getPath());
                if (file.exists() && file.getAbsolutePath()
                        .startsWith(getFilesDir().getAbsolutePath())) {
                    if (!file.delete()) {
                        android.util.Log.w("EditItemActivity", "Failed to delete image: " + imagePath);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(intent);
    }
}
