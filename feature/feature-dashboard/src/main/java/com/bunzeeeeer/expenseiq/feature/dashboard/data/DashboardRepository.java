package com.bunzeeeeer.expenseiq.feature.dashboard.data;

import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.core.domain.repository.BudgetRepository;
import com.bunzeeeeer.expenseiq.core.domain.repository.CategoryRepository;
import com.bunzeeeeer.expenseiq.core.domain.repository.ExpenseRepository;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-15-2026
 *
 */
public class DashboardRepository {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    public DashboardRepository(
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository,
            BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
    }

    public Flowable<List<Expense>> getRecentExpenses() {
        return expenseRepository.getAllExpenses();
    }

    public Flowable<List<Category>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    public Flowable<List<Budget>> getBudgetsThisMonth() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        return budgetRepository.getBudgetsByMonth(month, year);
    }

    public Single<Double> getTotalExpensesThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startDate = cal.getTimeInMillis();
        long endDate = System.currentTimeMillis();
        return expenseRepository.getTotalExpensesBetween(startDate, endDate);
    }
}