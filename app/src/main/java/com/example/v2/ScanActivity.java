package com.example.v2;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (!granted) {
                    Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Intent> scanLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                IntentResult scanResult = IntentIntegrator.parseActivityResult(
                        result.getResultCode(), result.getData());
                if (scanResult != null) {
                    if (scanResult.getContents() == null) {
                        Toast.makeText(this, R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
                    } else {
                        String barcode = scanResult.getContents();
                        Toast.makeText(this, getString(R.string.scanned_barcode, barcode), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, AddItemActivity.class);
                        intent.putExtra("sku", barcode);
                        startActivity(intent);
                        finish();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        findViewById(R.id.btn_scan).setOnClickListener(v -> startBarcodeScanner());
    }

    private void startBarcodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        scanLauncher.launch(integrator.createScanIntent());
    }
}
