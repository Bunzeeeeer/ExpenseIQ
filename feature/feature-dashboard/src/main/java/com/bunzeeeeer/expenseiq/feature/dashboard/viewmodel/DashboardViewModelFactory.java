package com.bunzeeeeer.expenseiq.feature.dashboard.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.feature.dashboard.data.DashboardRepository;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-15-2026
 *
 */
public class DashboardViewModelFactory implements ViewModelProvider.Factory {

    private final DashboardRepository repository;

    public DashboardViewModelFactory(DashboardRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}