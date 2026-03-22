package com.bunzeeeeer.expenseiq.feature.dashboard.data;

import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;

import java.util.List;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-15-2026
 *
 */
public class DashboardData {

    private final List<Expense> expenses;
    private final List<Category> categories;

    public DashboardData(List<Expense> expenses, List<Category> categories) {
        this.expenses = expenses;
        this.categories = categories;
    }

    public List<Expense> getExpenses() { return expenses; }
    public List<Category> getCategories() { return categories; }
}