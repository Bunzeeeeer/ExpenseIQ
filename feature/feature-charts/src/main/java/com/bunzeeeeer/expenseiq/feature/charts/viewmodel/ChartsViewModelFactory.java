package com.bunzeeeeer.expenseiq.feature.charts.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.feature.charts.data.ChartsRepository;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-22-2026
 *
 */
public class ChartsViewModelFactory implements ViewModelProvider.Factory {

    private final ChartsRepository repository;

    public ChartsViewModelFactory(ChartsRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChartsViewModel.class)) {
            return (T) new ChartsViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}