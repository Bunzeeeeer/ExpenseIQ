package com.bunzeeeeer.expenseiq.auth.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.auth.R;
import com.bunzeeeeer.expenseiq.auth.data.AuthRepository;
import com.bunzeeeeer.expenseiq.auth.viewmodel.RegisterViewModel;
import com.bunzeeeeer.expenseiq.auth.viewmodel.RegisterViewModelFactory;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-29-2026
 *
 */
public class RegisterActivity extends BaseActivity {

    @SuppressWarnings("java:S1450")
    private RegisterViewModel viewModel;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initDesign() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    @Override
    protected void initViewModel() {
        AuthRepository authRepository = new AuthRepository();
        RegisterViewModelFactory factory = new RegisterViewModelFactory(authRepository);
        viewModel = new ViewModelProvider(this, factory).get(RegisterViewModel.class);
    }

    @Override
    protected void initObservers() {
        viewModel.getLoading().observe(this, isLoading -> {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnRegister.setEnabled(!isLoading);
        });

        viewModel.getRegisterSuccess().observe(this, success -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        viewModel.getRegisterError().observe(this, tilPassword::setError);

        btnRegister.setOnClickListener(v -> handleRegister());

        tvLogin.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.error_empty_email));
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_empty_password));
            return;
        }
        if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_too_short));
            return;
        }
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            return;
        }
        viewModel.register(email, password);
    }
}