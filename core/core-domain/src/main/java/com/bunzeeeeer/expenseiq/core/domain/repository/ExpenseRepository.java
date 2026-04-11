package com.bunzeeeeer.expenseiq.core.domain.repository;

import com.bunzeeeeer.expenseiq.core.domain.model.Expense;

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
public interface ExpenseRepository {

    Completable addExpense(Expense expense);

    Completable updateExpense(Expense expense);

    Completable deleteExpense(Expense expense);

    Flowable<List<Expense>> getAllExpenses(String userId);

    Single<Expense> getExpenseById(long id, String userId);

    Flowable<List<Expense>> getExpensesByCategory(long categoryId, String userId);

    Flowable<List<Expense>> getExpensesByDateRange(String userId, long startDate, long endDate);

    Single<Double> getTotalExpensesBetween(String userId, long startDate, long endDate);

    Single<Double> getTotalByCategory(String userId, long categoryId, long startDate, long endDate);
}