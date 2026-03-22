package com.bunzeeeeer.expenseiq.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.core.domain.repository.BudgetRepository;
import com.bunzeeeeer.expenseiq.core.domain.repository.CategoryRepository;
import com.bunzeeeeer.expenseiq.core.domain.repository.ExpenseRepository;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-22-2026
 *
 */
public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    public MainViewModelFactory(
            ExpenseRepository expenseRepository,
            BudgetRepository budgetRepository,
            CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(
                    expenseRepository,
                    budgetRepository,
                    categoryRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}