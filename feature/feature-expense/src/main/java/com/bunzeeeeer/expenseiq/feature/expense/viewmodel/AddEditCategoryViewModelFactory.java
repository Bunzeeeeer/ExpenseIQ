package com.bunzeeeeer.expenseiq.feature.expense.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.feature.expense.data.ExpenseFeatureRepository;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-22-2026
 *
 */
public class AddEditCategoryViewModelFactory implements ViewModelProvider.Factory {

    private final ExpenseFeatureRepository repository;

    public AddEditCategoryViewModelFactory(ExpenseFeatureRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddEditCategoryViewModel.class)) {
            return (T) new AddEditCategoryViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}