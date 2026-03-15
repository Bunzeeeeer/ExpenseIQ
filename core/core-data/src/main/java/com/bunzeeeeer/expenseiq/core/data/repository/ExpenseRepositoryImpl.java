package com.bunzeeeeer.expenseiq.core.data.repository;

import com.bunzeeeeer.expenseiq.core.data.dao.ExpenseDao;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.core.domain.repository.ExpenseRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

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
    public Flowable<List<Expense>> getAllExpenses() {
        return expenseDao.getAllExpenses();
    }

    @Override
    public Single<Expense> getExpenseById(long id) {
        return expenseDao.getExpenseById(id);
    }

    @Override
    public Flowable<List<Expense>> getExpensesByCategory(long categoryId) {
        return expenseDao.getExpensesByCategory(categoryId);
    }

    @Override
    public Flowable<List<Expense>> getExpensesByDateRange(long startDate, long endDate) {
        return expenseDao.getExpensesByDateRange(startDate, endDate);
    }

    @Override
    public Single<Double> getTotalExpensesBetween(long startDate, long endDate) {
        return expenseDao.getTotalExpensesBetween(startDate, endDate);
    }

    @Override
    public Single<Double> getTotalByCategory(long categoryId, long startDate, long endDate) {
        return expenseDao.getTotalByCategory(categoryId, startDate, endDate);
    }
}