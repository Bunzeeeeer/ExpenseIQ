package com.bunzeeeeer.expenseiq.feature.charts.data;

import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.core.domain.repository.CategoryRepository;
import com.bunzeeeeer.expenseiq.core.domain.repository.ExpenseRepository;

import java.util.List;

import io.reactivex.Flowable;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
public class ChartsRepository {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final String userId;

    public ChartsRepository(
            String userId,
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository) {
        this.userId = userId;
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public Flowable<List<Expense>> getAllExpenses() {
        return expenseRepository.getAllExpenses(userId);
    }

    public Flowable<List<Category>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }
}