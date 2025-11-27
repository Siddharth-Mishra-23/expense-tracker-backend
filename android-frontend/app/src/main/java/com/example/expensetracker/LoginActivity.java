package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextView tvError;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvLoginError);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> tryLogin());
    }

    private void tryLogin() {
        String email = safeText(etEmail);
        String password = safeText(etPassword);

        if (!isValidEmail(email)) {
            showError("Enter a valid email.");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        // Mock login: no backend call, just continue.
        tvError.setVisibility(View.GONE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_ID", email); // use email as userId
        startActivity(intent);
        finish();
    }

    private String safeText(TextInputEditText editText) {
        CharSequence cs = editText.getText();
        return cs == null ? "" : cs.toString().trim();
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showError(String msg) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(msg);
    }
}
