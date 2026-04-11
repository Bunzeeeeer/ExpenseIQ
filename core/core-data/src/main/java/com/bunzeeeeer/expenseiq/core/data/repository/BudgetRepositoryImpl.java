package com.bunzeeeeer.expenseiq.core.data.repository;

import com.bunzeeeeer.expenseiq.core.data.dao.BudgetDao;
import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.repository.BudgetRepository;

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
public class BudgetRepositoryImpl implements BudgetRepository {

    private final BudgetDao budgetDao;

    public BudgetRepositoryImpl(BudgetDao budgetDao) {
        this.budgetDao = budgetDao;
    }

    @Override
    public Completable addBudget(Budget budget) {
        return budgetDao.insert(budget);
    }

    @Override
    public Completable updateBudget(Budget budget) {
        return budgetDao.update(budget);
    }

    @Override
    public Completable deleteBudget(Budget budget) {
        return budgetDao.delete(budget);
    }

    @Override
    public Flowable<List<Budget>> getBudgetsByMonth(String userId, int month, int year) {
        return budgetDao.getBudgetsByMonth(userId, month, year);
    }

    @Override
    public Single<Budget> getBudgetByCategoryAndMonth(String userId, long categoryId, int month, int year) {
        return budgetDao.getBudgetByCategoryAndMonth(userId, categoryId, month, year);
    }
}