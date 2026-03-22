package com.bunzeeeeer.expenseiq.feature.expense.data;

import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.core.domain.repository.CategoryRepository;
import com.bunzeeeeer.expenseiq.core.domain.repository.ExpenseRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-15-2026
 *
 */
public class ExpenseFeatureRepository {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseFeatureRepository(
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public Flowable<List<Expense>> getAllExpenses() {
        return expenseRepository.getAllExpenses();
    }

    public Single<Expense> getExpenseById(long id) {
        return expenseRepository.getExpenseById(id);
    }

    public Completable addExpense(Expense expense) {
        return expenseRepository.addExpense(expense);
    }

    public Completable updateExpense(Expense expense) {
        return expenseRepository.updateExpense(expense);
    }

    public Completable deleteExpense(Expense expense) {
        return expenseRepository.deleteExpense(expense);
    }

    public Completable addCategory(Category category) {
        return categoryRepository.addCategory(category);
    }

    public Completable updateCategory(Category category) {
        return categoryRepository.updateCategory(category);
    }

    public Completable deleteCategory(Category category) {
        return categoryRepository.deleteCategory(category);
    }

    public Flowable<List<Category>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }
}