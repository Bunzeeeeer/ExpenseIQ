package com.bunzeeeeer.expenseiq.core.domain.repository;

import com.bunzeeeeer.expenseiq.core.domain.model.Budget;

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
public interface BudgetRepository {

    Completable addBudget(Budget budget);

    Completable updateBudget(Budget budget);

    Completable deleteBudget(Budget budget);

    Flowable<List<Budget>> getBudgetsByMonth(int month, int year);

    Single<Budget> getBudgetByCategoryAndMonth(long categoryId, int month, int year);
}