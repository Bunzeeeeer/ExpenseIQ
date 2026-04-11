package com.bunzeeeeer.expenseiq.feature.budget.data;

import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.repository.BudgetRepository;
import com.bunzeeeeer.expenseiq.core.domain.repository.CategoryRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
public class BudgetFeatureRepository {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final String userId;

    public BudgetFeatureRepository(
            String userId,
            BudgetRepository budgetRepository,
            CategoryRepository categoryRepository) {
        this.userId = userId;
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    public Flowable<List<Budget>> getBudgetsByMonth(int month, int year) {
        return budgetRepository.getBudgetsByMonth(userId, month, year);
    }

    public Completable addBudget(Budget budget) {
        return budgetRepository.addBudget(budget);
    }

    public Completable updateBudget(Budget budget) {
        return budgetRepository.updateBudget(budget);
    }

    public Completable deleteBudget(Budget budget) {
        return budgetRepository.deleteBudget(budget);
    }

    public Flowable<List<Category>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }
}