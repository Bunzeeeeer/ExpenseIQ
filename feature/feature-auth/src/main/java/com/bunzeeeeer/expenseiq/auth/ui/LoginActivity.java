package com.bunzeeeeer.expenseiq.auth.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.auth.R;
import com.bunzeeeeer.expenseiq.auth.data.AuthRepository;
import com.bunzeeeeer.expenseiq.auth.viewmodel.LoginViewModel;
import com.bunzeeeeer.expenseiq.auth.viewmodel.LoginViewModelFactory;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-29-2026
 *
 */
public class LoginActivity extends BaseActivity {

    @SuppressWarnings("java:S1450")
    private LoginViewModel viewModel;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvRegister;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initDesign() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvRegister = findViewById(R.id.tvRegister);
    }

    @Override
    protected void initViewModel() {
        AuthRepository authRepository = new AuthRepository();
        LoginViewModelFactory factory = new LoginViewModelFactory(authRepository);
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);
    }

    @Override
    protected void initObservers() {
        viewModel.getLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnLogin.setEnabled(!isLoading);
        });

        viewModel.getLoginSuccess().observe(this, user -> {
            Intent intent = new Intent(this, getMainActivityClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        viewModel.getLoginError().observe(this, tilPassword::setError);

        btnLogin.setOnClickListener(v -> {
            tilEmail.setError(null);
            tilPassword.setError(null);
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (email.isEmpty()) {
                tilEmail.setError(getString(R.string.error_empty_email));
                return;
            }
            if (password.isEmpty()) {
                tilPassword.setError(getString(R.string.error_empty_password));
                return;
            }
            viewModel.login(email, password);
        });

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private Class<?> getMainActivityClass() {
        try {
            return Class.forName("com.bunzeeeeer.expenseiq.MainActivity");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}