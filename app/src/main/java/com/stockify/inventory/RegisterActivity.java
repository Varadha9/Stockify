package com.stockify.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout nameInputLayout, emailInputLayout, passwordInputLayout, confirmPasswordInputLayout;
    private TextInputEditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        MaterialButton registerButton = findViewById(R.id.registerButton);
        TextView loginLinkText = findViewById(R.id.loginLinkText);

        registerButton.setOnClickListener(v -> attemptRegistration());
        loginLinkText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegistration() {
        nameInputLayout.setError(null);
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);

        String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
        String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
        String password = passwordEditText.getText() != null ? passwordEditText.getText().toString() : "";
        String confirmPassword = confirmPasswordEditText.getText() != null ? confirmPasswordEditText.getText().toString() : "";

        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError(getString(R.string.error_name_empty));
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError(getString(R.string.error_email_empty));
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError(getString(R.string.error_password_empty));
            isValid = false;
        } else if (password.length() < 6) {
            passwordInputLayout.setError(getString(R.string.error_password_length));
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.setError(getString(R.string.error_password_empty));
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError(getString(R.string.error_passwords_dont_match));
            isValid = false;
        }

        if (isValid) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            if (prefs.contains("regEmail")) {
                new AlertDialog.Builder(this)
                        .setTitle("Account Already Exists")
                        .setMessage("Registering will overwrite the existing account. Continue?")
                        .setPositiveButton("Continue", (d, w) -> showDataSafetyDialog(email, password, name))
                        .setNegativeButton(getString(R.string.btn_cancel), null)
                        .show();
            } else {
                showDataSafetyDialog(email, password, name);
            }
        }
    }

    private void showDataSafetyDialog(String email, String password, String name) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.data_safety_title)
                .setMessage(R.string.data_safety_message)
                .setPositiveButton(R.string.data_safety_ok, (dialog, which) -> saveAndProceed(email, password, name))
                .setCancelable(false)
                .show();
    }

    private void saveAndProceed(String email, String password, String name) {
            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit()
                    .putString("regEmail", email)
                    .putString("regPassword", PasswordUtils.hashNewPassword(password))
                    .putString("regName", name)
                    .apply();
            Toast.makeText(this, R.string.registration_successful, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
    }
}
