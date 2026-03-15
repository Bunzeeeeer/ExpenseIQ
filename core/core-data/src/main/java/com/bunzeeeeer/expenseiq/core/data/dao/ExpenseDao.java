package com.bunzeeeeer.expenseiq.core.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
@Dao
public interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Expense expense);

    @Update
    Completable update(Expense expense);

    @Delete
    Completable delete(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    Flowable<List<Expense>> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE id = :id")
    Single<Expense> getExpenseById(long id);

    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId ORDER BY date DESC")
    Flowable<List<Expense>> getExpensesByCategory(long categoryId);

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    Flowable<List<Expense>> getExpensesByDateRange(long startDate, long endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    Single<Double> getTotalExpensesBetween(long startDate, long endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE categoryId = :categoryId AND date BETWEEN :startDate AND :endDate")
    Single<Double> getTotalByCategory(long categoryId, long startDate, long endDate);
}