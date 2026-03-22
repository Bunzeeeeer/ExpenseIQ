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
public class CategoryListViewModelFactory implements ViewModelProvider.Factory {

    private final ExpenseFeatureRepository repository;

    public CategoryListViewModelFactory(ExpenseFeatureRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategoryListViewModel.class)) {
            return (T) new CategoryListViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}