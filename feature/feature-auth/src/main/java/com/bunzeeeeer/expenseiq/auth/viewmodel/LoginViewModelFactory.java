package com.bunzeeeeer.expenseiq.auth.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.auth.data.AuthRepository;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-29-2026
 *
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final AuthRepository authRepository;

    public LoginViewModelFactory(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(authRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}