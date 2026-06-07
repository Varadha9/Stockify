package com.stockify.inventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class AddItemActivity extends AppCompatActivity {

    private ImageView itemImageView;
    private Uri imageUri;

    private TextInputEditText nameEditText, priceEditText, quantityEditText,
            lowStockEditText, descriptionEditText, skuEditText, supplierEditText,
            costPriceEditText, reorderQtyEditText;

    private AutoCompleteTextView categoryAutoComplete;
    private TextInputLayout nameInputLayout, priceInputLayout,
            quantityInputLayout, lowStockInputLayout, costPriceInputLayout;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri picked = result.getData().getData();
                    if (picked != null) {
                        imageUri = copyImageToInternal(picked);
                        itemImageView.setImageURI(imageUri);
                        itemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        initializeViews();
        setupCategoryDropdown();
        setupClickListeners();

        // Pre-fill SKU if launched from barcode scan.
        String sku = getIntent().getStringExtra("sku");
        if (sku != null && !sku.isEmpty()) {
            skuEditText.setText(sku);
        }
    }

    private void initializeViews() {
        ImageButton backButton = findViewById(R.id.backButton);
        itemImageView = findViewById(R.id.itemImageView);
        categoryAutoComplete = findViewById(R.id.categoryAutoComplete);

        nameEditText = findViewById(R.id.nameEditText);
        priceEditText = findViewById(R.id.priceEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        lowStockEditText = findViewById(R.id.lowStockEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        skuEditText = findViewById(R.id.skuEditText);
        supplierEditText = findViewById(R.id.supplierEditText);
        costPriceEditText = findViewById(R.id.costPriceEditText);
        reorderQtyEditText = findViewById(R.id.reorderQtyEditText);

        nameInputLayout = findViewById(R.id.nameInputLayout);
        priceInputLayout = findViewById(R.id.priceInputLayout);
        quantityInputLayout = findViewById(R.id.quantityInputLayout);
        lowStockInputLayout = findViewById(R.id.lowStockInputLayout);
        costPriceInputLayout = findViewById(R.id.costPriceInputLayout);
        priceInputLayout.setHint("Selling Price (" + CurrencyFormatter.getSymbol(this) + ") *");
        costPriceInputLayout.setHint("Cost Price (" + CurrencyFormatter.getSymbol(this) + ")");

        MaterialButton submitButton = findViewById(R.id.submitButton);

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        submitButton.setOnClickListener(v -> validateAndSubmitItem());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (hasUnsavedInput()) {
                    new AlertDialog.Builder(AddItemActivity.this)
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
    }

    private boolean hasUnsavedInput() {
        return !TextUtils.isEmpty(nameEditText.getText())
                || !TextUtils.isEmpty(skuEditText.getText())
                || !TextUtils.isEmpty(supplierEditText.getText())
                || !TextUtils.isEmpty(descriptionEditText.getText())
                || imageUri != null;
    }

    private void setupCategoryDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                InventoryItem.CATEGORIES
        );
        categoryAutoComplete.setAdapter(adapter);
    }

    private void setupClickListeners() {
        android.view.ViewGroup imageContainer = (android.view.ViewGroup) itemImageView.getParent();
        imageContainer.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(intent);
    }

    private void validateAndSubmitItem() {
        if (!validateInputs()) return;

        String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String category = Objects.requireNonNull(categoryAutoComplete.getText()).toString().trim();
        double price = Double.parseDouble(Objects.requireNonNull(priceEditText.getText()).toString());
        int quantity = Integer.parseInt(Objects.requireNonNull(quantityEditText.getText()).toString());
        int lowStockThreshold = Integer.parseInt(Objects.requireNonNull(lowStockEditText.getText()).toString());
        String description = Objects.requireNonNull(descriptionEditText.getText()).toString().trim();
        String imagePath = (imageUri != null) ? imageUri.toString() : "";
        String sku = skuEditText.getText() != null ? skuEditText.getText().toString().trim() : "";
        String supplier = supplierEditText.getText() != null ? supplierEditText.getText().toString().trim() : "";
        double costPrice = 0;
        try { costPrice = costPriceEditText.getText() != null && !costPriceEditText.getText().toString().isEmpty()
                ? Double.parseDouble(costPriceEditText.getText().toString()) : 0; } catch (NumberFormatException ignored) {}
        int reorderQty = 0;
        try { reorderQty = reorderQtyEditText.getText() != null && !reorderQtyEditText.getText().toString().isEmpty()
                ? Integer.parseInt(reorderQtyEditText.getText().toString()) : 0; } catch (NumberFormatException ignored) {}

        InventoryItem item = new InventoryItem(name, category, price, quantity, lowStockThreshold, description, imagePath, sku, supplier);
        item.setCostPrice(costPrice);
        item.setReorderQuantity(reorderQty);
        saveItemToDatabase(item);
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(nameEditText.getText())) {
            nameInputLayout.setError("Item name is required");
            isValid = false;
        } else {
            nameInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(categoryAutoComplete.getText())) {
            categoryAutoComplete.setError("Category is required");
            isValid = false;
        } else {
            categoryAutoComplete.setError(null);
        }

        if (TextUtils.isEmpty(priceEditText.getText())) {
            priceInputLayout.setError("Price is required");
            isValid = false;
        } else {
            try {
                double price = Double.parseDouble(priceEditText.getText().toString());
                if (price < 0) {
                    priceInputLayout.setError("Price cannot be negative");
                    isValid = false;
                } else {
                    priceInputLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                priceInputLayout.setError("Invalid price");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(quantityEditText.getText())) {
            quantityInputLayout.setError("Quantity is required");
            isValid = false;
        } else {
            try {
                Integer.parseInt(quantityEditText.getText().toString());
                quantityInputLayout.setError(null);
            } catch (NumberFormatException e) {
                quantityInputLayout.setError("Invalid quantity");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(lowStockEditText.getText())) {
            lowStockInputLayout.setError("Low stock threshold is required");
            isValid = false;
        } else {
            try {
                Integer.parseInt(lowStockEditText.getText().toString());
                lowStockInputLayout.setError(null);
            } catch (NumberFormatException e) {
                lowStockInputLayout.setError("Invalid number");
                isValid = false;
            }
        }

        return isValid;
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
            return source; // fallback to original URI
        }
    }

    private void saveItemToDatabase(InventoryItem item) {
        InventoryDatabase db = InventoryDatabase.getDatabase(this);
        AppExecutor.get().execute(() -> {
            db.inventoryDao().insertItem(item);
            db.stockLogDao().insert(new StockLog(
                    item.getName(),
                    "ADDED",
                    "Created with qty " + item.getQuantity(),
                    System.currentTimeMillis()
            ));
            InventoryAnalytics.recordTodaySnapshot(this);
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.item_added, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }
}
