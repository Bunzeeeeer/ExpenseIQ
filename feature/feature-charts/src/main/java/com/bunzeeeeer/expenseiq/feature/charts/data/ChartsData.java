package com.bunzeeeeer.expenseiq.feature.charts.data;

import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;

import java.util.List;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
public class ChartsData {

    private final List<Expense> expenses;
    private final List<Category> categories;

    public ChartsData(List<Expense> expenses, List<Category> categories) {
        this.expenses = expenses;
        this.categories = categories;
    }

    public List<Expense> getExpenses() { return expenses; }
    public List<Category> getCategories() { return categories; }
}