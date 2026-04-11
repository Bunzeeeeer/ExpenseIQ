package com.bunzeeeeer.expenseiq.core.data.repository;

import com.bunzeeeeer.expenseiq.core.data.dao.ExpenseDao;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
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
public class ExpenseRepositoryImpl implements ExpenseRepository {

    private final ExpenseDao expenseDao;

    public ExpenseRepositoryImpl(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;
    }

    @Override
    public Completable addExpense(Expense expense) {
        return expenseDao.insert(expense);
    }

    @Override
    public Completable updateExpense(Expense expense) {
        return expenseDao.update(expense);
    }

    @Override
    public Completable deleteExpense(Expense expense) {
        return expenseDao.delete(expense);
    }

    @Override
    public Flowable<List<Expense>> getAllExpenses(String userId) {
        return expenseDao.getAllExpenses(userId);
    }

    @Override
    public Single<Expense> getExpenseById(long id, String userId) {
        return expenseDao.getExpenseById(id, userId);
    }

    @Override
    public Flowable<List<Expense>> getExpensesByCategory(long categoryId, String userId) {
        return expenseDao.getExpensesByCategory(categoryId, userId);
    }

    @Override
    public Flowable<List<Expense>> getExpensesByDateRange(String userId, long startDate, long endDate) {
        return expenseDao.getExpensesByDateRange(userId, startDate, endDate);
    }

    @Override
    public Single<Double> getTotalExpensesBetween(String userId, long startDate, long endDate) {
        return expenseDao.getTotalExpensesBetween(userId, startDate, endDate);
    }

    @Override
    public Single<Double> getTotalByCategory(String userId, long categoryId, long startDate, long endDate) {
        return expenseDao.getTotalByCategory(userId, categoryId, startDate, endDate);
    }
}