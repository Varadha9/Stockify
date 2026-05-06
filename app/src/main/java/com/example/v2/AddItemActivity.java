package com.example.v2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class AddItemActivity extends AppCompatActivity {

    private static final String[] CATEGORIES = {
            "Electronics", "Clothing", "Grocery",
            "Furniture", "Accessories", "Other"
    };

    private ImageView itemImageView;
    private Uri imageUri;

    private TextInputEditText nameEditText, priceEditText, quantityEditText,
            lowStockEditText, descriptionEditText, skuEditText, supplierEditText;

    private AutoCompleteTextView categoryAutoComplete;
    private TextInputLayout nameInputLayout, priceInputLayout,
            quantityInputLayout, lowStockInputLayout;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    itemImageView.setImageURI(imageUri);
                    itemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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

        nameInputLayout = findViewById(R.id.nameInputLayout);
        priceInputLayout = findViewById(R.id.priceInputLayout);
        quantityInputLayout = findViewById(R.id.quantityInputLayout);
        lowStockInputLayout = findViewById(R.id.lowStockInputLayout);
        priceInputLayout.setHint("Price (" + CurrencyFormatter.getSymbol(this) + ") *");

        MaterialButton submitButton = findViewById(R.id.submitButton);

        backButton.setOnClickListener(v -> finish());
        submitButton.setOnClickListener(v -> validateAndSubmitItem());
    }

    private void setupCategoryDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                CATEGORIES
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

        InventoryItem item = new InventoryItem(name, category, price, quantity, lowStockThreshold, description, imagePath, sku, supplier);
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
                Double.parseDouble(priceEditText.getText().toString());
                priceInputLayout.setError(null);
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
