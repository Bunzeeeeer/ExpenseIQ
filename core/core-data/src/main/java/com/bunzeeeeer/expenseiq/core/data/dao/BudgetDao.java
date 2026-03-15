package com.bunzeeeeer.expenseiq.core.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bunzeeeeer.expenseiq.core.data.model.Budget;

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
@Dao
public interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Budget budget);

    @Update
    Completable update(Budget budget);

    @Delete
    Completable delete(Budget budget);

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    Flowable<List<Budget>> getBudgetsByMonth(int month, int year);

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year")
    Single<Budget> getBudgetByCategoryAndMonth(long categoryId, int month, int year);
}