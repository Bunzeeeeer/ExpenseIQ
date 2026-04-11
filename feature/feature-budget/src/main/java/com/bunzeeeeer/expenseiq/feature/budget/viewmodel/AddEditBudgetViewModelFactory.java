package com.bunzeeeeer.expenseiq.feature.budget.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.feature.budget.data.BudgetFeatureRepository;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-22-2026
 *
 */
public class AddEditBudgetViewModelFactory implements ViewModelProvider.Factory {

    private final BudgetFeatureRepository repository;

    public AddEditBudgetViewModelFactory(BudgetFeatureRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddEditBudgetViewModel.class)) {
            return (T) new AddEditBudgetViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}